package com.xeppaka.lentareader.data.dao.daoobjects;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.xeppaka.lentareader.data.dao.DaoObservable;
import com.xeppaka.lentareader.downloader.LentaHttpImageDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.URLHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by nnm on 11/22/13.
 */
public class ImageDao implements DaoObservable<BitmapReference> {
    private static LruCache<String, CachedLazyLoadBitmapReference> bitmapCache;
    private static LruCache<String, CachedLazyLoadBitmapReference> thumbnailsBitmapCache;

    private static final String imageNotAvailableText1 = "Изображение";
    private static final String imageNotAvailableText2 = "не доступно";

    private static final String imageLoadingText1 = "Изображение";
    private static final String imageLoadingText2 = "загружается";

    private static final String imageTurnedOffText1 = "Загрузка изображений";
    private static final String imageTurnedOffText2 = "отключена";

    private static final float THUMBNAIL_RATIO = 3.5f;

    // private ContentResolver contentResolver;
    private static final StrongBitmapReference notAvailableImageRef;
    private static final StrongBitmapReference loadingImageRef;

    private static final StrongBitmapReference notAvailableThumbnailImageRef;
    private static final StrongBitmapReference loadingThumbnailImageRef;

    private static final StrongBitmapReference turnedOffImagesThumbnailImageRef;

//    private static final List<Observer> observers = new ArrayList<Observer>();
//    private static final Object observersSync = new Object();

    private static final ThreadPoolExecutor downloadImageExecutor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    static {
        notAvailableImageRef = new StrongBitmapReference(createNotAvailableBitmap());
        loadingImageRef = new StrongBitmapReference(createLoadingBitmap());

        notAvailableThumbnailImageRef = new StrongBitmapReference(createNotAvailableThumbnailBitmap());
        loadingThumbnailImageRef = new StrongBitmapReference(createLoadingThumbnailBitmap());

        turnedOffImagesThumbnailImageRef = new StrongBitmapReference(createTurnedOffThumbnailBitmap());
    }

    private ImageDao() {}
    private static ImageDao INSTANCE;

    public static ImageDao newInstance(Context context) {
        if (bitmapCache == null || thumbnailsBitmapCache == null) {
            ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            final int memClass = activityManager.getMemoryClass();
            LentaConstants.adjustCacheSizes(memClass);

            bitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(LentaConstants.BITMAP_CACHE_MAX_SIZE_IN_BYTES) {
                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            CachedLazyLoadBitmapReference oldValue, CachedLazyLoadBitmapReference newValue) {
                    if ((newValue != oldValue) && evicted) {
                        oldValue.onRemoveFromCache();
                    }
                }

                @Override
                protected int sizeOf(String key, CachedLazyLoadBitmapReference value) {
                    return value.bitmapSize();
                }
            };

            thumbnailsBitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(LentaConstants.THUMBNAILS_BITMAP_CACHE_MAX_SIZE_IN_BYTES) {
                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            CachedLazyLoadBitmapReference oldValue, CachedLazyLoadBitmapReference newValue) {
                    if ((newValue != oldValue) && evicted) {
                        oldValue.onRemoveFromCache();
                    }
                }

                @Override
                protected int sizeOf(String key, CachedLazyLoadBitmapReference value) {
                    return value.bitmapSize();
                }
            };
        }

        if (INSTANCE == null)
            INSTANCE = new ImageDao();

        return INSTANCE;
    }

    public BitmapReference read(String imageUrl) {
        return read(imageUrl, false);
    }

    public BitmapReference readThumbnail(String imageUrl) {
        return readThumbnail(imageUrl, false);
    }

    private BitmapReference readThumbnail(String imageUrl, boolean cacheOnly) {
        if (imageUrl == null || TextUtils.isEmpty(imageUrl)) {
            Log.d(LentaConstants.LoggerAnyTag,
                    "ImageDao trying to read image with empty URL");

            return getNotAvailableThumbnailImage();
        }

        Log.d(LentaConstants.LoggerAnyTag,
                "ImageDao read thumbnail bitmap with URL: " + imageUrl);
        Log.d(LentaConstants.LoggerAnyTag,
                "Caches sizes: full bitmap cache is " + bitmapCache.size() + ", thumbnail bitmap cache is " + thumbnailsBitmapCache.size());

        Log.d(LentaConstants.LoggerAnyTag, "Free memory: " + Runtime.getRuntime().freeMemory());


        String imageKey;
        try {
            imageKey = URLHelper.getImageId(imageUrl);
        } catch (MalformedURLException e) {
            Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
            return getNotAvailableThumbnailImage();
        }

        CachedLazyLoadBitmapReference imageRef;
        synchronized(thumbnailsBitmapCache) {
            imageRef = thumbnailsBitmapCache.get(imageKey);
        }

        if (cacheOnly || imageRef != null) {
            Log.d(LentaConstants.LoggerAnyTag, "Found thumbnail in cache. Bitmap size: " + imageRef.bitmapSize());
            return imageRef;
        }

        imageRef = new CachedLazyLoadBitmapReference(imageKey, imageUrl, true);
        synchronized(thumbnailsBitmapCache) {
            thumbnailsBitmapCache.put(imageKey, imageRef);
        }

        Log.d(LentaConstants.LoggerAnyTag, "Created empty lazy load reference.");

        return imageRef;
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
     * Gets thumbnail image with "Image loading" phrase written in it.
     *
     * @return reference to the bitmap. Never null.
     */
    public static BitmapReference getLoadingThumbnailImage() {
        return loadingThumbnailImageRef;
    }
    /**
     * Gets thumbnail image with "Not available" phrase written in it. Can be when image for
     * some reason is not available for news/article/etc.
     *
     * @return reference to the bitmap. Never null.
     */
    public static BitmapReference getNotAvailableThumbnailImage() {
        return notAvailableThumbnailImageRef;
    }

    /**
     * Gets image with "Image loading" phrase written in it.
     *
     * @return reference to the bitmap. Never null.
     */
    public static BitmapReference getLoadingImage() {
        return loadingImageRef;
    }

    /**
     * Gets image with "Turned off" phrase written in it.
     *
     * @return reference to the bitmap. Never null.
     */
    public static StrongBitmapReference getTurnedOffImagesThumbnailImageRef() {
        return turnedOffImagesThumbnailImageRef;
    }

    //    /**
//     * Creates bitmap in cache. It means it will store bitmap in the memory
//     * cache and save it to the external drive cache if it's available.
//     *
//     * @param url
//     *            is the URL where image can be downloaded.
//     * @param bitmap
//     *            is the bitmap that should be saved.
//     * @return reference to the BitmapReference instance. Should be used later
//     *         on to retrieve image from the cache.
//     */
//    public BitmapReference create(String url, Bitmap bitmap) {
//        Log.d(LentaConstants.LoggerAnyTag, "Create bitmap in disk and/or memory cache for image URL: " + url);
//
//        String imageKey;
//        try {
//            imageKey = URLHelper.getImageId(url);
//        } catch (MalformedURLException e) {
//            Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + url);
//            return getNotAvailableImage();
//        }
//
//        CachedLazyLoadBitmapReference imageRef = bitmapCache.get(imageKey);
//
//        if (imageRef != null) {
//            imageRef.setBitmap(bitmap);
//
//            Log.d(LentaConstants.LoggerAnyTag, "Create bitmap: found reference in the cache, set bitmap to this reference. Cache size: " + bitmapCache.size());
//        } else {
//            imageRef = new CachedLazyLoadBitmapReference(imageKey, url, bitmap);
//
//            try {
//                //createBitmapOnDisk(imageKey, bitmap);
//
//                synchronized(bitmapCache) {
//                    bitmapCache.put(imageKey, imageRef);
//                }
//                Log.d(LentaConstants.LoggerAnyTag, "Put bitmap to the cache. Cache size: " + bitmapCache.size());
//            } finally {
//                /**
//                 * When CachedLazyLoadBitmapReference is created it supposes that we are
//                 * the one user of the bitmap. So we need to release bitmap before
//                 * return lazy load reference to it.
//                 */
//                //imageRef.releaseBitmap();
//            }
//
//            Log.d(LentaConstants.LoggerAnyTag, "Create bitmap: no old references in the cache found, created new reference. Cache size: " + bitmapCache.size());
//        }
//
//        notifyDataChanged(imageRef);
//        return imageRef;
//    }

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

//    private boolean isBitmapInMemory(String imageKey) {
//        Log.d(LentaConstants.LoggerAnyTag, "Check bitmap in memory cache for image key: " + imageKey);
//
//        CachedLazyLoadBitmapReference bitmap;
//        synchronized(bitmapCache) {
//            bitmap = bitmapCache.get(imageKey);
//        }
//
//        return bitmap != null && bitmap.isBitmapInMemory();
//    }

    private static Bitmap createNotAvailableBitmap() {
//        Bitmap result = Bitmap.createBitmap(420, 280, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(result);
//
//        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setTextSize(30);
//
//        float text1Width = paint.measureText(imageNotAvailableText1);
//        float text2Width = paint.measureText(imageNotAvailableText2);
//
//        canvas.drawText(imageNotAvailableText1, (420 - text1Width) / 2, 70, paint);
//        canvas.drawText(imageNotAvailableText2, (420 - text2Width) / 2, 100, paint);
//        return result;

        // for now to save memory this image is not used

        return null;
    }

    private static Bitmap createNotAvailableThumbnailBitmap() {
        Bitmap result = Bitmap.createBitmap(165, 110, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(15);

        float text1Width = paint.measureText(imageNotAvailableText1);
        float text2Width = paint.measureText(imageNotAvailableText2);

        canvas.drawText(imageNotAvailableText1, (165 - text1Width) / 2, 25, paint);
        canvas.drawText(imageNotAvailableText2, (165 - text2Width) / 2, 45, paint);
        return result;
    }

    private static Bitmap createLoadingBitmap() {
//        Bitmap result = Bitmap.createBitmap(420, 280, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(result);
//
//        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setTextSize(30);
//
//        float text1Width = paint.measureText(imageLoadingText1);
//        float text2Width = paint.measureText(imageLoadingText2);
//
//        canvas.drawText(imageLoadingText1, (420 - text1Width) / 2, 70, paint);
//        canvas.drawText(imageLoadingText2, (420 - text2Width) / 2, 100, paint);
//        return result;

        // for now to save memory this image is not used

        return null;
    }

    private static Bitmap createLoadingThumbnailBitmap() {
        Bitmap result = Bitmap.createBitmap(165, 110, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(15);

        float text1Width = paint.measureText(imageLoadingText1);
        float text2Width = paint.measureText(imageLoadingText2);

        canvas.drawText(imageLoadingText1, (165 - text1Width) / 2, 25, paint);
        canvas.drawText(imageLoadingText2, (165 - text2Width) / 2, 45, paint);
        return result;
    }

    private static Bitmap createTurnedOffThumbnailBitmap() {
        Bitmap result = Bitmap.createBitmap(165, 110, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(15);

        float text1Width = paint.measureText(imageTurnedOffText1);
        float text2Width = paint.measureText(imageTurnedOffText2);

        canvas.drawText(imageTurnedOffText1, (165 - text1Width) / 2, 25, paint);
        canvas.drawText(imageTurnedOffText2, (165 - text2Width) / 2, 45, paint);
        return result;
    }

//    private boolean createBitmapOnDisk(String key, Bitmap bitmap) {
//        ParcelFileDescriptor.AutoCloseOutputStream output = null;
//        try {
//            ParcelFileDescriptor fileDescriptor = contentResolver
//                    .openFileDescriptor(
//                            LentaProvider.CONTENT_URI_CACHED_IMAGE.buildUpon().appendPath(key).build(), "wt");
//            output = new ParcelFileDescriptor.AutoCloseOutputStream(fileDescriptor);
//
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
//
//            return true;
//        } catch (FileNotFoundException e) {
//            Log.d(LentaConstants.LoggerAnyTag, "Cannot find on the external drive bitmap with id: " + key, e);
//            return false;
//        } finally {
//            if (output != null) {
//                try {
//                    output.close();
//                } catch (IOException e) {
//                    Log.e(LentaConstants.LoggerAnyTag, "Error occured during flushing and closing output stream.", e);
//                }
//            }
//        }
//    }
//
//    private Bitmap readBitmapFromDisk(String key) {
//        ParcelFileDescriptor.AutoCloseInputStream input = null;
//        try {
//            ParcelFileDescriptor fileDescriptor = contentResolver
//                    .openFileDescriptor(
//                            LentaProvider.CONTENT_URI_CACHED_IMAGE.buildUpon().appendPath(key).build(), "r");
//            input = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
//
//            return BitmapFactory.decodeStream(input);
//        } catch (FileNotFoundException e) {
//            Log.d(LentaConstants.LoggerAnyTag, "Cannot find on the external drive bitmap with id: " + key, e);
//            return null;
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

    @Override
    public void registerContentObserver(Observer<BitmapReference> observer) {
//        synchronized (observersSync) {
//            observers.add(observer);
//        }
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
//        synchronized (observersSync) {
//            observers.remove(observer);
//        }
//
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

//    private void notifyDataChanged(BitmapReference bitmap) {
//        synchronized (observersSync) {
//            for (Observer<BitmapReference> observer : observers) {
//                observer.onDataChanged(false, bitmap);
//            }
//        }
//    }

    public class CachedLazyLoadBitmapReference implements BitmapReference {
        private volatile int bitmapReferences;
        private volatile boolean cached;
        private volatile Bitmap bitmap;
        private final String cacheKey;
        private final String url;
        private boolean thumbnail;

        private class BitmapGetResult {
            private Bitmap bitmap;
            private Exception exception;

            private BitmapGetResult(Bitmap bitmap) {
                this.bitmap = bitmap;
            }

            private BitmapGetResult(Exception exception) {
                this.exception = exception;
            }

            public Bitmap getBitmap() {
                return bitmap;
            }

            public Exception getException() {
                return exception;
            }
        }

        private class BitmapLoadTask extends AsyncTask<Callback, Void, BitmapGetResult> {
            private Callback[] listeners;

            @Override
            protected BitmapGetResult doInBackground(Callback... listeners) {
                this.listeners = listeners;
                try {
                    return new BitmapGetResult(getBitmap());
                } catch (IOException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "IO Error while downloading bitmap, url: " + url, e);
                    return new BitmapGetResult(e);
                } catch (HttpStatusCodeException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "IO Error while downloading bitmap, url: " + url, e);
                    return new BitmapGetResult(e);
                }
            }

            @Override
            protected void onPostExecute(BitmapGetResult result) {
                for (Callback listener : listeners) {
                    if (result.getBitmap() != null)
                        listener.onSuccess(result.getBitmap());
                    else
                        listener.onFailure(result.getException());
                }
            }
        }

        public CachedLazyLoadBitmapReference(String key, String url) {
            this(key, url, null);
        }

        public CachedLazyLoadBitmapReference(String key, String url, boolean thumbnail) {
            this(key, url, null, thumbnail);
        }

        public CachedLazyLoadBitmapReference(String key, String url, Bitmap bitmap) {
            this(key, url, bitmap, false);
        }

        public CachedLazyLoadBitmapReference(String key, String url, Bitmap bitmap, boolean thumbnail) {
            this.bitmap = bitmap;
            this.cacheKey = key;
            this.url = url;
            this.thumbnail = thumbnail;

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

        private void setBitmap(Bitmap bitmap) {
            if (thumbnail) {
                synchronized(thumbnailsBitmapCache) {
                    thumbnailsBitmapCache.remove(cacheKey);
                    this.bitmap = bitmap;
                    thumbnailsBitmapCache.put(cacheKey, this);
                }
            } else {
                synchronized(bitmapCache) {
                    bitmapCache.remove(cacheKey);
                    this.bitmap = bitmap;
                    bitmapCache.put(cacheKey, this);
                }
            }
        }

        private Bitmap downloadBitmap() throws IOException, HttpStatusCodeException {
            return LentaHttpImageDownloader.downloadBitmap(url);
        }

        @Override
        public Bitmap getBitmap() throws IOException, HttpStatusCodeException {
            if (bitmap != null) {
                return seizeBitmap();
            }

            if (thumbnail) {
                // may be full bitmap is in the cache
                BitmapReference fullBitmapRef;
                synchronized(bitmapCache) {
                    fullBitmapRef = bitmapCache.get(cacheKey);
                }

                if (fullBitmapRef != null) {
                    final Bitmap fullBitmap = fullBitmapRef.getBitmapIfCached();

                    if (fullBitmap != null) {
                        // create scaled bitmap
                        setBitmap(Bitmap.createScaledBitmap(fullBitmap, Math.round(fullBitmap.getWidth() / THUMBNAIL_RATIO), Math.round(fullBitmap.getHeight() / THUMBNAIL_RATIO), false));

                        return seizeBitmap();
                    }
                }
            }

            // download and set bitmap
            final Bitmap downloadedBitmap = downloadBitmap();

            if (thumbnail) {
                setBitmap(Bitmap.createScaledBitmap(downloadedBitmap, Math.round(downloadedBitmap.getWidth() / 4.0f), Math.round(downloadedBitmap.getHeight() / 4.0f), false));

                // we have full image, let's put it in the full bitmaps cache
                synchronized (bitmapCache) {
                    CachedLazyLoadBitmapReference fullImageRef;

                    if ((fullImageRef = bitmapCache.get(url)) == null) {
                        fullImageRef = new CachedLazyLoadBitmapReference(cacheKey, url, downloadedBitmap);
                        bitmapCache.put(cacheKey, fullImageRef);
                        fullImageRef.releaseBitmap();
                    } else if (!fullImageRef.isBitmapCached()) {
                        fullImageRef.setBitmap(downloadedBitmap);
                    }
                }
            } else {
                setBitmap(downloadedBitmap);
            }

            return seizeBitmap();
        }

        @Override
        public Bitmap getBitmapIfCached() {
            return seizeBitmap();
        }

        @Override
        public AsyncTask getBitmapAsync(Callback callback) {
            if (bitmap != null) {
                callback.onSuccess(bitmap);

                return null;
            } else {
                AsyncTask<Callback, Void, BitmapGetResult> task = new BitmapLoadTask();
                if (LentaConstants.SDK_VER >= 11)
                    task.executeOnExecutor(downloadImageExecutor, callback);
                else
                    task.execute(callback);

                return task;
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

        public void releaseBitmap() {
            if (--bitmapReferences <= 0 && !isCached()) {
                Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
                recycleBitmap();
            }

            Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
        }

        public boolean isCached() {
            return cached;
        }

        public synchronized boolean isBitmapCached() {
            return bitmap != null;
        }

        private void onRemoveFromCache() {
//            if (bitmapReferences <= 0) {
                recycleBitmap();
//            }

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

        private int bitmapSize() {
            if (bitmap != null) {
                if (LentaConstants.SDK_VER >= 19) {
                    return bitmap.getAllocationByteCount();
                } else if (LentaConstants.SDK_VER >= 12) {
                    return bitmap.getByteCount();
                } else {
                    return bitmap.getWidth() * bitmap.getHeight() * getBytesPerPixel(bitmap.getConfig());
                }
            }

            return 0;
        }

        private Bitmap seizeBitmap() {
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
