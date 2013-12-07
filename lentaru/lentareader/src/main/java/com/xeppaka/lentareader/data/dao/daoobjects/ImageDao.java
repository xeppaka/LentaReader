package com.xeppaka.lentareader.data.dao.daoobjects;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.xeppaka.lentareader.data.dao.DaoObservable;
import com.xeppaka.lentareader.data.provider.LentaProvider;
import com.xeppaka.lentareader.downloader.LentaHttpImageDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.URLHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nnm on 11/22/13.
 */
public class ImageDao implements DaoObservable<BitmapReference> {
    private static final LruCache<String, CachedLazyLoadBitmapReference> bitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(LentaConstants.BITMAP_CACHE_MAX_SIZE_IN_BYTES) {
        @Override
        protected void entryRemoved(boolean evicted, String key,
                                    CachedLazyLoadBitmapReference oldValue, CachedLazyLoadBitmapReference newValue) {
            if (newValue != oldValue && evicted) {
                oldValue.onRemoveFromCache();
            }
        }

        @Override
        protected int sizeOf(String key, CachedLazyLoadBitmapReference value) {
            return value.bitmapSize();
        }
    };

    private ContentResolver contentResolver;
    private static final StrongBitmapReference notAvailableImageRef;

    private static final List<Observer> observers = new ArrayList<Observer>();
    private static final Object observersSync = new Object();

    private static final ThreadPoolExecutor downloadExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    static {
        notAvailableImageRef = new StrongBitmapReference(createNotAvailableBitmap());
    }

    private ImageDao() {
    }

    public static ImageDao newInstance() {
        return new ImageDao();
    }

    public BitmapReference read(String imageUrl) {
        return read(imageUrl, false);
    }

    private BitmapReference read(String imageUrl, boolean cacheOnly) {
        if (imageUrl == null || TextUtils.isEmpty(imageUrl)) {
            Log.d(LentaConstants.LoggerAnyTag,
                    "ImageDao trying to read image with empty URL");

            return getNotAvailableImage();
        }

        Log.d(LentaConstants.LoggerAnyTag,
                "ImageDao read bitmap with URL: " + imageUrl);

        String imageKey;
        try {
            imageKey = URLHelper.getImageId(imageUrl);
        } catch (MalformedURLException e) {
            Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
            return getNotAvailableImage();
        }

        CachedLazyLoadBitmapReference imageRef;
        synchronized(bitmapCache) {
            imageRef = bitmapCache.get(imageKey);
        }

        if (cacheOnly || imageRef != null) {
            Log.d(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
            return imageRef;
        }

        imageRef = new CachedLazyLoadBitmapReference(imageKey, imageUrl);
        synchronized(bitmapCache) {
            bitmapCache.put(imageKey, imageRef);
        }

        Log.d(LentaConstants.LoggerAnyTag, "Created empty lazy load reference.");

        return imageRef;
    }

    /**
     * Gets image with "Not available" phrase written in it. Can be when image for
     * some reason is not available for news/article/etc.
     *
     * @return reference to the bitmap. Never null.
     */
    public static BitmapReference getNotAvailableImage() {
        return notAvailableImageRef;
    }

    /**
     * Creates bitmap in cache. It means it will store bitmap in the memory
     * cache and save it to the external drive cache if it's available.
     *
     * @param url
     *            is the URL where image can be downloaded.
     * @param bitmap
     *            is the bitmap that should be saved.
     * @return reference to the BitmapReference instance. Should be used later
     *         on to retrieve image from the cache.
     */
    public BitmapReference create(String url, Bitmap bitmap) {
        Log.d(LentaConstants.LoggerAnyTag, "Create bitmap in disk and/or memory cache for image URL: " + url);

        String imageKey;
        try {
            imageKey = URLHelper.getImageId(url);
        } catch (MalformedURLException e) {
            Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + url);
            return getNotAvailableImage();
        }

        CachedLazyLoadBitmapReference imageRef = bitmapCache.get(imageKey);

        if (imageRef != null) {
            imageRef.setBitmap(bitmap);

            Log.d(LentaConstants.LoggerAnyTag, "Create bitmap: found reference in the cache, set bitmap to this reference. Cache size: " + bitmapCache.size());
        } else {
            imageRef = new CachedLazyLoadBitmapReference(imageKey, url, bitmap);

            try {
                //createBitmapOnDisk(imageKey, bitmap);

                synchronized(bitmapCache) {
                    bitmapCache.put(imageKey, imageRef);
                }
                Log.d(LentaConstants.LoggerAnyTag, "Put bitmap to the cache. Cache size: " + bitmapCache.size());
            } finally {
                /**
                 * When CachedLazyLoadBitmapReference is created it supposes that we are
                 * the one user of the bitmap. So we need to release bitmap before
                 * return lazy load reference to it.
                 */
                imageRef.releaseBitmap();
            }

            Log.d(LentaConstants.LoggerAnyTag, "Create bitmap: no old references in the cache found, created new reference. Cache size: " + bitmapCache.size());
        }

        notifyDataChanged(imageRef);
        return imageRef;
    }

//    public boolean imageExist(String imageUrl) {
//        String imageKey;
//        try {
//            imageKey = URLHelper.getImageId(imageUrl);
//        } catch (MalformedURLException e) {
//            Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
//            return false;
//        }
//
//        if (isBitmapInMemory(imageKey) || checkImageInDiskCache(imageKey)) {
//            return true;
//        }
//
//        return false;
//    }

//    private boolean checkImageInDiskCache(String imageKey) {
//        Log.d(LentaConstants.LoggerAnyTag, "Check bitmap in disk cache for image key: " + imageKey);
//
//        ParcelFileDescriptor.AutoCloseInputStream input = null;
//
//        try {
//            ParcelFileDescriptor fileDescriptor = contentResolver
//                    .openFileDescriptor(
//                            LentaProvider.CONTENT_URI_CACHED_IMAGE.buildUpon().appendPath(imageKey).build(), "r");
//            input = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
//
//            return true;
//        } catch (FileNotFoundException e) {
//            return false;
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    Log.e(LentaConstants.LoggerAnyTag, "Error occured during closing output stream.", e);
//                }
//            }
//        }
//    }

    private boolean isBitmapInMemory(String imageKey) {
        Log.d(LentaConstants.LoggerAnyTag, "Check bitmap in memory cache for image key: " + imageKey);

        CachedLazyLoadBitmapReference bitmap;
        synchronized(bitmapCache) {
            bitmap = bitmapCache.get(imageKey);
        }

        if (bitmap != null) {
            return bitmap.isBitmapInMemory();
        }

        return false;
    }

    private static Bitmap createNotAvailableBitmap() {
        Bitmap result = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);

        canvas.drawText("Изображение", 20, 20, paint);
        canvas.drawText("не доступно.", 20, 100, paint);
        return result;
    }

    private boolean createBitmapOnDisk(String key, Bitmap bitmap) {
        ParcelFileDescriptor.AutoCloseOutputStream output = null;
        try {
            ParcelFileDescriptor fileDescriptor = contentResolver
                    .openFileDescriptor(
                            LentaProvider.CONTENT_URI_CACHED_IMAGE.buildUpon().appendPath(key).build(), "wt");
            output = new ParcelFileDescriptor.AutoCloseOutputStream(fileDescriptor);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);

            return true;
        } catch (FileNotFoundException e) {
            Log.d(LentaConstants.LoggerAnyTag, "Cannot find on the external drive bitmap with id: " + key, e);
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "Error occured during flushing and closing output stream.", e);
                }
            }
        }
    }

    private Bitmap readBitmapFromDisk(String key) {
        ParcelFileDescriptor.AutoCloseInputStream input = null;
        try {
            ParcelFileDescriptor fileDescriptor = contentResolver
                    .openFileDescriptor(
                            LentaProvider.CONTENT_URI_CACHED_IMAGE.buildUpon().appendPath(key).build(), "r");
            input = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);

            return BitmapFactory.decodeStream(input);
        } catch (FileNotFoundException e) {
            Log.d(LentaConstants.LoggerAnyTag, "Cannot find on the external drive bitmap with id: " + key, e);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "Error occured during closing output stream.", e);
                }
            }
        }
    }

    @Override
    public void registerContentObserver(Observer<BitmapReference> observer) {
        synchronized (observersSync) {
            observers.add(observer);
        }
//        DaoObserver<BitmapReference> daoObserver;
//
//        if (observer instanceof DaoObserver) {
//            daoObserver = (DaoObserver<BitmapReference>)observer;
//        } else {
//            throw new IllegalArgumentException("observer is not derived from DaoObserver which is not supported now. You should create observer by extending DaoObserver abstract class.");
//        }
//
//        contentResolver.registerContentObserver(LentaProvider.CONTENT_URI_CACHED_IMAGE, true, daoObserver.getContentObserver());
    }

    @Override
    public void unregisterContentObserver(Observer<BitmapReference> observer) {
        synchronized (observersSync) {
            observers.remove(observer);
        }

//        DaoObserver<BitmapReference> daoObserver;
//
//        if (observer instanceof DaoObserver) {
//            daoObserver = (DaoObserver<BitmapReference>)observer;
//        } else {
//            throw new IllegalArgumentException("observer is not derived from DaoObserver which is not supported now. You should create observer by extending DaoObserver abstract class.");
//        }
//
//        contentResolver.unregisterContentObserver(daoObserver.getContentObserver());
    }

    private void notifyDataChanged(BitmapReference bitmap) {
        synchronized (observersSync) {
            for (Observer<BitmapReference> observer : observers) {
                observer.onDataChanged(false, bitmap);
            }
        }
    }

    public class CachedLazyLoadBitmapReference implements BitmapReference {
        private volatile int bitmapReferences;
        private volatile boolean cached;
        private Bitmap bitmap;
        private final String cacheKey;
        private final String url;

        private class BitmapLoadTask extends AsyncTask<Callback, Void, Bitmap> {
            private Callback[] listeners;

            @Override
            protected Bitmap doInBackground(Callback... listeners) {
                this.listeners = listeners;

                return getBitmap();
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                for (Callback listener : listeners) {
                    listener.onSuccess(result);
                }
            }
        }

        public CachedLazyLoadBitmapReference(String key, String url) {
            this(key, url, null);
        }

        public CachedLazyLoadBitmapReference(String key, String url, Bitmap bitmap) {
            this.bitmap = bitmap;
            this.cacheKey = key;
            this.url = url;

            if (bitmap != null) {

                /**
                 * This class suppose that the user of the bitmap already has
                 * one reference to the bitmap. Also it allows to not recycle
                 * bitmap immediately after creation if cache is full of
                 * bitmaps.
                 */
                bitmapReferences = 1;
            }

            this.cached = true;
        }

        private synchronized void setBitmap(Bitmap bitmap) {
            // set bitmap with cache size recalculating
            synchronized(bitmapCache) {
                CachedLazyLoadBitmapReference imageRef = bitmapCache.remove(cacheKey);
                this.bitmap = bitmap;

                if (imageRef != null) {
                    bitmapCache.put(cacheKey, imageRef);
                }
            }
        }

        private Bitmap downloadBitmap() throws IOException, HttpStatusCodeException {
            return LentaHttpImageDownloader.downloadBitmap(url);
        }

        @Override
        public synchronized Bitmap getBitmap() {
            if (bitmap != null) {
                return bitmap;
            }

            // TODO: try to find in disk cache

            try {
                // download and set bitmap
                Bitmap bitm = downloadBitmap();
                setBitmap(bitm);

                return bitm;
            } catch (IOException e) {
                Log.e(LentaConstants.LoggerAnyTag, "IO Error while downloading bitmap, url: " + url, e);
                return null;
            } catch (HttpStatusCodeException e) {
                Log.e(LentaConstants.LoggerAnyTag, "IO Error while downloading bitmap, url: " + url, e);
                return null;
            }
        }

        @Override
        public synchronized Bitmap getBitmapIfCached() {
            return seizeBitmap();
        }

        @Override
        public void getBitmapAsync(Callback callback) {
            if (bitmap != null) {
                callback.onSuccess(bitmap);
            } else {
                new BitmapLoadTask().executeOnExecutor(downloadExecutor, callback);
            }
        }

        //        /*
//         * Note: <b>MUST be called from UI thread.</b>
//         * (non-Javadoc)
//         * @see cz.fit.lentaruand.data.dao.BitmapReference#getBitmapAsync(cz.fit.lentaruand.data.dao.BitmapReference.BitmapLoadListener)
//         */
//        @Override
//        public void getBitmapAsync(final Callback callback) {
//            Bitmap seizedBitmap = seizeBitmap();
//            if (seizedBitmap != null) {
//                callback.onSuccess(seizedBitmap);
//            } else {
//                serviceHelper.downloadImage(url, new com.xeppaka.lentareader.service.Callback() {
//                    @Override
//                    public void onSuccess() {
//                        callback.onSuccess(bitmap);
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        callback.onFailure();
//                    }
//                });
//            }
//        }

        public synchronized void releaseBitmap() {
            if (bitmap != null && --bitmapReferences <= 0 && !isCached()) {
                Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);

                recycleBitmap();
            }

            Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
        }

        public synchronized boolean isCached() {
            return cached;
        }

        public synchronized boolean isBitmapInMemory() {
            return bitmap != null && !bitmap.isRecycled();
        }

        private synchronized void onRemoveFromCache() {
            if (bitmapReferences <= 0) {
                recycleBitmap();
            }

            cached = false;
        }

        private int getBytesPerPixel(Bitmap.Config config) {
            switch (config) {
                case ALPHA_8:
                    return 1;
                case ARGB_4444:
                case RGB_565:
                    return 2;
                case ARGB_8888:
                    return 4;
            }

            return 1;
        }

        private synchronized int bitmapSize() {
            if (bitmap != null) {
                if (LentaConstants.SDK_VER > 11) {
                    return bitmap.getByteCount();
                } else {
                    return bitmap.getWidth() * bitmap.getHeight() * getBytesPerPixel(bitmap.getConfig());
                }
            }

            return 0;
        }

        private synchronized Bitmap seizeBitmap() {
            if (bitmap != null) {
                ++bitmapReferences;
                Log.d(LentaConstants.LoggerAnyTag, "seizeBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
            }

            return bitmap;
        }

        private void recycleBitmap() {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
                Log.d(LentaConstants.LoggerAnyTag, "recycleBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
            }
        }
    }
}
