package com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.dao.DaoObservable;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.StrongBitmapReference;
import com.xeppaka.lentareader.downloader.HttpImageDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private static StrongBitmapReference notAvailableImageRef;
    private static StrongBitmapReference loadingImageRef;

    private static StrongBitmapReference notAvailableThumbnailImageRef;
    private static StrongBitmapReference loadingThumbnailImageRef;

    private static StrongBitmapReference turnedOffImagesThumbnailImageRef;

//    private static final List<Observer> observers = new ArrayList<Observer>();
//    private static final Object observersSync = new Object();

    private static final ThreadPoolExecutor downloadImageExecutor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

    private static ImageDao INSTANCE;

    private float displayDensity;
    private Resources resources;

    private static int full_image_width = 330;
    private static int full_image_height = 220;
    private static int thumbnail_image_width = 90;
    private static int thumbnail_image_height = 60;

    private static int image_loading_color_background;

    private ImageDao(Resources resources) {
        this.displayDensity = resources.getDisplayMetrics().density;
        this.resources = resources;
    }

    public static ImageDao getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("ImageDao is not initialized previously. Use getInstance(Context) first time.");
        }

        return INSTANCE;
    }

    public static ImageDao getInstance(Context context) {
        final Resources resources = context.getResources();

        if (bitmapCache == null || thumbnailsBitmapCache == null) {
            ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            final int memClass = activityManager.getMemoryClass();
            LentaConstants.adjustCacheSizes(memClass);

            bitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(LentaConstants.BITMAP_CACHE_MAX_SIZE_IN_BYTES) {
                @Override
                protected void entryRemoved(boolean evicted, String key,
                                            CachedLazyLoadBitmapReference oldValue, CachedLazyLoadBitmapReference newValue) {
                    if (LentaConstants.DEVELOPER_MODE) {
                        Log.d("entryRemoved", "cache entry for full bitmap removed, key = " + key + ", evicted: " + evicted);
                    }

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
                    if (LentaConstants.DEVELOPER_MODE) {
                        Log.d("entryRemoved", "cache entry for thumbnail bitmap removed, key = " + key + ", evicted: " + evicted);
                    }

                    if ((newValue != oldValue) && evicted) {
                        oldValue.onRemoveFromCache();
                    }
                }

                @Override
                protected int sizeOf(String key, CachedLazyLoadBitmapReference value) {
                    return value.bitmapSize();
                }
            };

            full_image_width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, full_image_width, resources.getDisplayMetrics()));
            full_image_height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, full_image_height, resources.getDisplayMetrics()));
            thumbnail_image_width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thumbnail_image_width, resources.getDisplayMetrics()));
            thumbnail_image_height = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, thumbnail_image_height, resources.getDisplayMetrics()));

            image_loading_color_background = resources.getColor(R.color.image_loading_background);

            notAvailableImageRef = new StrongBitmapReference(createNotAvailableBitmap(), resources);
            loadingImageRef = new StrongBitmapReference(createLoadingBitmap(), resources);

            notAvailableThumbnailImageRef = new StrongBitmapReference(createNotAvailableThumbnailBitmap(), resources);
            loadingThumbnailImageRef = new StrongBitmapReference(createLoadingThumbnailBitmap(), resources);

            turnedOffImagesThumbnailImageRef = new StrongBitmapReference(createTurnedOffThumbnailBitmap(), resources);
        }

        if (INSTANCE == null)
            INSTANCE = new ImageDao(resources);

        return INSTANCE;
    }

    public BitmapReference read(String imageUrl, String imageKey) {
        return read(imageUrl, imageKey, false);
    }

    public BitmapReference read(String imageUrl, ImageKeyCreator keyCreator) {
        return read(imageUrl, keyCreator, false);
    }

    public BitmapReference read(String imageUrl, ImageKeyCreator keyCreator, boolean cacheOnly) {
        String imageKey;
        try {
            imageKey = keyCreator.getImageKey(imageUrl);
        } catch (MalformedURLException e) {
            Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
            return getNotAvailableImage();
        }

        return read(imageUrl, imageKey, cacheOnly);
    }

    public BitmapReference read(String imageUrl, String imageKey, boolean cacheOnly) {
        if (imageUrl == null || TextUtils.isEmpty(imageUrl)) {
            if (LentaConstants.DEVELOPER_MODE) {
                Log.d(LentaConstants.LoggerAnyTag,
                        "ImageDao trying to read image with empty URL");
            }

            return getNotAvailableImage();
        }

        if (LentaConstants.DEVELOPER_MODE) {
            Log.d(LentaConstants.LoggerAnyTag,
                    "ImageDao read bitmap with URL: " + imageUrl);
        }

        CachedLazyLoadBitmapReference imageRef;
        synchronized(bitmapCache) {
            imageRef = bitmapCache.get(imageKey);
        }

        if (cacheOnly || imageRef != null) {
            if (LentaConstants.DEVELOPER_MODE) {
                Log.d(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
            }
            return imageRef;
        }

        imageRef = new CachedLazyLoadBitmapReference(imageKey, imageUrl);
        synchronized(bitmapCache) {
            bitmapCache.put(imageKey, imageRef);
        }

        if (LentaConstants.DEVELOPER_MODE) {
            Log.d(LentaConstants.LoggerAnyTag, "Created empty lazy load reference.");
        }

        return imageRef;
    }

    public BitmapReference readThumbnail(String imageUrl, ImageKeyCreator keyCreator) {
        return readThumbnail(imageUrl, keyCreator, false);
    }

    public BitmapReference readThumbnail(String imageUrl, ImageKeyCreator keyCreator, boolean cacheOnly) {
        final String imageKey;
        try {
            imageKey = keyCreator.getImageKey(imageUrl);
        } catch (MalformedURLException e) {
            Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
            return getNotAvailableThumbnailImage();
        }

        return readThumbnail(imageUrl, imageKey, cacheOnly);
    }

    public BitmapReference readThumbnail(String imageUrl, String imageKey) {
        return readThumbnail(imageUrl, imageKey, false);
    }

    public BitmapReference readThumbnail(String imageUrl, String imageKey, boolean cacheOnly) {
        if (imageUrl == null || TextUtils.isEmpty(imageUrl)) {
            if (LentaConstants.DEVELOPER_MODE) {
                Log.d(LentaConstants.LoggerAnyTag,
                        "ImageDao trying to read image with empty URL");
            }

            return getNotAvailableThumbnailImage();
        }

        if (LentaConstants.DEVELOPER_MODE) {
            Log.d(LentaConstants.LoggerAnyTag,
                    "ImageDao read thumbnail bitmap with URL: " + imageUrl);
            Log.d(LentaConstants.LoggerAnyTag,
                    "Caches sizes: full bitmap cache is " + bitmapCache.size() + ", thumbnail bitmap cache is " + thumbnailsBitmapCache.size());
        }

        CachedLazyLoadBitmapReference imageRef;
        synchronized(thumbnailsBitmapCache) {
            imageRef = thumbnailsBitmapCache.get(imageKey);
        }

        if (cacheOnly || imageRef != null) {
            if (LentaConstants.DEVELOPER_MODE) {
                Log.d(LentaConstants.LoggerAnyTag, "Found thumbnail in cache. Bitmap size: " + imageRef.bitmapSize());
            }
            return imageRef;
        }

        imageRef = new CachedLazyLoadBitmapReference(imageKey, imageUrl, true);
        synchronized(thumbnailsBitmapCache) {
            thumbnailsBitmapCache.put(imageKey, imageRef);
        }

        if (LentaConstants.DEVELOPER_MODE) {
            Log.d(LentaConstants.LoggerAnyTag, "Created empty lazy load reference.");
        }

        return imageRef;
    }

    public void makeSpace() {
        bitmapCache.trimToSize(LentaConstants.BITMAP_CACHE_TRIM_MAX_SIZE_IN_BYTES);
        thumbnailsBitmapCache.trimToSize(LentaConstants.THUMBNAILS_BITMAP_CACHE_TRIM_MAX_SIZE_IN_BYTES);

        System.gc();
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
        Bitmap result = Bitmap.createBitmap(thumbnail_image_width, thumbnail_image_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(13);

        float text1Width = paint.measureText(imageNotAvailableText1);
        float text2Width = paint.measureText(imageNotAvailableText2);

        canvas.drawText(imageNotAvailableText1, (thumbnail_image_width - text1Width) / 2, 25, paint);
        canvas.drawText(imageNotAvailableText2, (thumbnail_image_width - text2Width) / 2, 45, paint);

        paint.setAntiAlias(false);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(0, 0, thumbnail_image_width - 1, thumbnail_image_height - 1, paint);
        return result;
    }

    private static Bitmap createLoadingBitmap() {
        Bitmap result = Bitmap.createBitmap(full_image_width, full_image_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
        paint.setColor(image_loading_color_background);
        paint.setAntiAlias(true);
//        paint.setTextSize(30);

        canvas.drawRect(0, 0, full_image_width, full_image_height, paint);

//        float text1Width = paint.measureText(imageLoadingText1);
//        float text2Width = paint.measureText(imageLoadingText2);
//
//        canvas.drawText(imageLoadingText1, (full_image_width - text1Width) / 2, 70, paint);
//        canvas.drawText(imageLoadingText2, (full_image_width - text2Width) / 2, 100, paint);
        return result;
    }

    private static Bitmap createLoadingThumbnailBitmap() {
        Bitmap result = Bitmap.createBitmap(thumbnail_image_width, thumbnail_image_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
//        paint.setColor(Color.BLACK);
        paint.setColor(image_loading_color_background);
        paint.setAntiAlias(true);
//        paint.setTextSize(13);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(0, 0, thumbnail_image_width, thumbnail_image_height, paint);

//        float text1Width = paint.measureText(imageLoadingText1);
//        float text2Width = paint.measureText(imageLoadingText2);
//
//        canvas.drawText(imageLoadingText1, (thumbnail_image_width - text1Width) / 2, 25, paint);
//        canvas.drawText(imageLoadingText2, (thumbnail_image_width - text2Width) / 2, 45, paint);
        return result;
    }

    private static Bitmap createTurnedOffThumbnailBitmap() {
        Bitmap result = Bitmap.createBitmap(thumbnail_image_width, thumbnail_image_height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(11);

        float text1Width = paint.measureText(imageTurnedOffText1);
        float text2Width = paint.measureText(imageTurnedOffText2);

        canvas.drawText(imageTurnedOffText1, (thumbnail_image_width - text1Width) / 2, 25, paint);
        canvas.drawText(imageTurnedOffText2, (thumbnail_image_width - text2Width) / 2, 45, paint);

        paint.setAntiAlias(false);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawRect(0, 0, thumbnail_image_width - 1, thumbnail_image_height - 1, paint);
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
        private volatile BitmapDrawable drawable;
        private final String cacheKey;
        private final String url;
        private boolean thumbnail;

        private List<WeakReference<ImageView>> views = new ArrayList<WeakReference<ImageView>>();

        private class AsyncGetResult<T> {
            private T value;
            private Exception exception;

            private AsyncGetResult(T value) {
                this.value = value;
            }

            private AsyncGetResult(Exception exception) {
                this.exception = exception;
            }

            public T getValue() {
                return value;
            }

            public Exception getException() {
                return exception;
            }
        }

        private class AsyncGetParameter<T> {
            private ImageView view;
            private AsyncListener<T> listener;

            private AsyncGetParameter(ImageView view, AsyncListener<T> loadListener) {
                this.view = view;
                this.listener = loadListener;
            }

            public ImageView getView() {
                return view;
            }

            public AsyncListener<T> getLoadListener() {
                return listener;
            }
        }

        private class BitmapLoadTask extends AsyncTask<AsyncGetParameter<Bitmap>, Void, AsyncGetResult<Bitmap>> {
            private AsyncListener<Bitmap> listener;
            private ImageView view;

            @Override
            protected AsyncGetResult<Bitmap> doInBackground(AsyncGetParameter<Bitmap>... params) {
                listener = params[0].getLoadListener();
                view = params[0].getView();

                try {
                    return new AsyncGetResult<Bitmap>(getBitmap(view));
                } catch (IOException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "IO Error while downloading bitmap, url: " + url, e);
                    return new AsyncGetResult<Bitmap>(e);
                } catch (HttpStatusCodeException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "IO Error while downloading bitmap, url: " + url, e);
                    return new AsyncGetResult<Bitmap>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncGetResult<Bitmap> result) {
                    if (result.getValue() != null)
                        listener.onSuccess(result.getValue());
                    else
                        listener.onFailure(result.getException());
            }
        }

        private class DrawableLoadTask extends AsyncTask<AsyncGetParameter<Drawable>, Void, AsyncGetResult<Drawable>> {
            private AsyncListener<Drawable> listener;
            private ImageView view;

            @Override
            protected AsyncGetResult<Drawable> doInBackground(AsyncGetParameter<Drawable>... params) {
                listener = params[0].getLoadListener();
                view = params[0].getView();

                try {
                    return new AsyncGetResult<Drawable>(getDrawable(view));
                } catch (IOException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "IO Error while downloading bitmap, url: " + url, e);
                    return new AsyncGetResult<Drawable>(e);
                } catch (HttpStatusCodeException e) {
                    Log.e(LentaConstants.LoggerAnyTag, "IO Error while downloading bitmap, url: " + url, e);
                    return new AsyncGetResult<Drawable>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncGetResult<Drawable> result) {
                if (result.getValue() != null)
                    listener.onSuccess(result.getValue());
                else
                    listener.onFailure(result.getException());
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
            if (LentaConstants.DEVELOPER_MODE) {
                Log.d("setBitmap", "--start--");
            }

            if (thumbnail) {
                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d("setBitmap", "setting bitmap for thumbnail, key = " + cacheKey);
                }

                synchronized(thumbnailsBitmapCache) {
                    thumbnailsBitmapCache.remove(cacheKey);
                    this.bitmap = bitmap;
                    thumbnailsBitmapCache.put(cacheKey, this);
                }
            } else {
                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d("setBitmap", "setting bitmap for full bitmap, key = " + cacheKey);
                }

                synchronized(bitmapCache) {
                    bitmapCache.remove(cacheKey);
                    this.bitmap = bitmap;
                    bitmapCache.put(cacheKey, this);
                }
            }

            if (LentaConstants.DEVELOPER_MODE) {
                Log.d("setBitmap", "--end--");
            }
        }

        private Bitmap downloadBitmap() throws IOException, HttpStatusCodeException {
            return downloadBitmap(false);
        }

        private Bitmap downloadBitmap(boolean retry) throws IOException, HttpStatusCodeException {
            try {
                return HttpImageDownloader.downloadBitmap(url);
            } catch (OutOfMemoryError oome) {
                if (!retry) {
                    makeSpace();
                    return downloadBitmap(true);
                }

                throw oome;
            }
        }

        @Override
        public Bitmap getBitmap() throws Exception {
            return getBitmap(null);
        }

        @Override
        public Bitmap getBitmap(ImageView view) throws IOException, HttpStatusCodeException {
            if (LentaConstants.DEVELOPER_MODE) {
                Log.d("getBitmap", "--start--");
            }

            if (bitmap != null) {
                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d("getBitmap", "found bitmap in the cache, key = " + cacheKey);
                }

                return seizeBitmap(view);
            }

            if (thumbnail) {
                // may be full bitmap is in the cache
                final Bitmap scaledBitmap = createThumbnailFromFullBitmap();

                if (scaledBitmap != null) {
                    if (LentaConstants.DEVELOPER_MODE) {
                        Log.d("getBitmap", "created thumbnail bitmap from full bitmap, key = " + cacheKey);
                    }

                    setBitmap(scaledBitmap);
                    return seizeBitmap(view);
                }
            }

            if (LentaConstants.DEVELOPER_MODE) {
                Log.d("getBitmap", "downloading bitmap...");
            }
            // download and set bitmap
            final Bitmap downloadedBitmap = downloadBitmap();

            if (LentaConstants.DEVELOPER_MODE) {
                Log.d("getBitmap", "downloading bitmap. done.");
            }

            if (downloadedBitmap == null) {
                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d("getBitmap", "downloaded bitmap is null");
                }

                return null;
            }

            downloadedBitmap.setDensity((int)displayDensity);

            if (thumbnail) {
                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d("getBitmap", "create and set thumbnail bitmap from full bitmap");
                }

                final Bitmap scaledBitmap = Bitmap.createScaledBitmap(downloadedBitmap, Math.round(downloadedBitmap.getWidth() / THUMBNAIL_RATIO), Math.round(downloadedBitmap.getHeight() / THUMBNAIL_RATIO), false);
                scaledBitmap.setDensity((int)displayDensity);
                setBitmap(scaledBitmap);

                // we have full image, let's put it in the full bitmaps cache
                synchronized (bitmapCache) {
                    CachedLazyLoadBitmapReference fullImageRef;

                    if ((fullImageRef = bitmapCache.get(cacheKey)) == null) {
                        fullImageRef = new CachedLazyLoadBitmapReference(cacheKey, url, downloadedBitmap);
                        bitmapCache.put(cacheKey, fullImageRef);
                        fullImageRef.releaseBitmap();
                    } else if (!fullImageRef.isBitmapCached()) {
                        fullImageRef.setBitmap(downloadedBitmap);
                    }
                }
            } else {
                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d("getBitmap", "set full bitmap");
                }
                setBitmap(downloadedBitmap);
//
//                // if we downloaded full bitmap, but we don't have thumbnail,
//                // let's create it and put into thumbnail bitmaps cache.
//                synchronized (thumbnailsBitmapCache) {
//                    // TODO: too much work in synchronized
//
//                    CachedLazyLoadBitmapReference thumbnailImageRef;
//
//                    if ((thumbnailImageRef = bitmapCache.get(url)) == null) {
//                        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(downloadedBitmap, Math.round(downloadedBitmap.getWidth() / THUMBNAIL_RATIO), Math.round(downloadedBitmap.getHeight() / THUMBNAIL_RATIO), false);
//                        scaledBitmap.setDensity((int)displayDensity);
//
//                        thumbnailImageRef = new CachedLazyLoadBitmapReference(cacheKey, url, downloadedBitmap);
//                        thumbnailsBitmapCache.put(cacheKey, thumbnailImageRef);
//                        thumbnailImageRef.releaseBitmap();
//                    } else if (!thumbnailImageRef.isBitmapCached()) {
//                        final Bitmap scaledBitmap = Bitmap.createScaledBitmap(downloadedBitmap, Math.round(downloadedBitmap.getWidth() / THUMBNAIL_RATIO), Math.round(downloadedBitmap.getHeight() / THUMBNAIL_RATIO), false);
//                        scaledBitmap.setDensity((int)displayDensity);
//
//                        thumbnailImageRef.setBitmap(downloadedBitmap);
//                    }
//                }
            }

            boolean test = false;
            final Bitmap bbb = seizeBitmap(view);

            if (bbb == null) {
                test = true;
            }

            return bbb;
        }

        @Override
        public Bitmap getBitmapIfCached(ImageView view) {
            if (thumbnail && bitmap == null) {
                // may be full bitmap is in the cache
                final Bitmap scaledBitmap = createThumbnailFromFullBitmap();

                if (scaledBitmap != null) {
                    setBitmap(scaledBitmap);
                    return seizeBitmap(view);
                }
            }

            return seizeBitmap(view);
        }

        private Bitmap createThumbnailFromFullBitmap() {
            BitmapReference fullBitmapRef;

            synchronized(bitmapCache) {
                fullBitmapRef = bitmapCache.get(cacheKey);
            }

            if (fullBitmapRef != null) {
                final Bitmap fullBitmap = fullBitmapRef.getBitmapIfCached();

                if (fullBitmap != null) {
                    // create scaled bitmap
                    final Bitmap scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, Math.round(fullBitmap.getWidth() / THUMBNAIL_RATIO), Math.round(fullBitmap.getHeight() / THUMBNAIL_RATIO), false);
                    scaledBitmap.setDensity((int)displayDensity);

                    return scaledBitmap;
                }
            }

            return null;
        }

        @Override
        public Bitmap getBitmapIfCached() {
            return getBitmapIfCached(null);
        }

        @Override
        public Drawable getDrawable() throws Exception {
            return getDrawable(null);
        }

        @Override
        public Drawable getDrawable(ImageView view) throws IOException, HttpStatusCodeException {
            // this call is needed to seize bitmap correctly
            final Bitmap bitmap = getBitmap(view);

            if (drawable == null) {
                drawable = new BitmapDrawable(resources, bitmap);
            }

            return drawable;
        }

        @Override
        public Drawable getDrawableIfCached() {
            return getDrawableIfCached(null);
        }

        @Override
        public Drawable getDrawableIfCached(ImageView view) {
            // this call is needed to seize bitmap correctly
            final Bitmap bitmap = getBitmapIfCached(view);

            if (bitmap != null && drawable == null) {
                drawable = new BitmapDrawable(resources, bitmap);
            }

            return drawable;
        }

        @Override
        public AsyncTask getBitmapAsync(AsyncListener<Bitmap> listener) {
            return getBitmapAsync(null, listener);
        }

        @Override
        public AsyncTask getBitmapAsync(ImageView view, AsyncListener<Bitmap> listener) {
            final Bitmap localBitmap = getBitmapIfCached(view);

            if (localBitmap != null) {
                listener.onSuccess(seizeBitmap());

                return null;
            } else {
                AsyncTask<AsyncGetParameter<Bitmap>, Void, AsyncGetResult<Bitmap>> task = new BitmapLoadTask();
                if (LentaConstants.SDK_VER >= 11)
                    task.executeOnExecutor(downloadImageExecutor, new AsyncGetParameter<Bitmap>(view, listener));
                else
                    task.execute(new AsyncGetParameter<Bitmap>(view, listener));

                return task;
            }
        }

        @Override
        public AsyncTask getDrawableAsync(AsyncListener<Drawable> listener) {
            return getDrawableAsync(null, listener);
        }

        @Override
        public AsyncTask getDrawableAsync(ImageView view, AsyncListener<Drawable> listener) {
            final Drawable localDrawable = getDrawableIfCached(view);

            if (localDrawable != null) {
                listener.onSuccess(localDrawable);

                return null;
            } else {
                AsyncTask<AsyncGetParameter<Drawable>, Void, AsyncGetResult<Drawable>> task = new DrawableLoadTask();
                if (LentaConstants.SDK_VER >= 11)
                    task.executeOnExecutor(downloadImageExecutor, new AsyncGetParameter<Drawable>(view, listener));
                else
                    task.execute(new AsyncGetParameter<Drawable>(view, listener));

                return task;
            }
        }

        public void releaseImageView(ImageView view) {
            final int count = views.size();

            for (int i = 0; i < count; ++i) {
                final WeakReference<ImageView> ref = views.get(i);
                final ImageView refView = ref.get();

                if (view == refView) {
                    ref.clear();
                }
            }
        }

        public void releaseBitmap() {
            if (bitmap != null) {
                --bitmapReferences;

                if (bitmapReferences == 0 && !isCached()) {
                    if (LentaConstants.DEVELOPER_MODE) {
                        Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
                    }
                    // recycleBitmap();
                }

                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
                }
            } else {
                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): key: " + cacheKey + ", bitmap is null, number of references: " + bitmapReferences);
                }
            }
        }

        public boolean isCached() {
            return cached;
        }

        public synchronized boolean isBitmapCached() {
            return bitmap != null;
        }

        private void onRemoveFromCache() {
            cleanViewsOnRemoveFromCache();

//            if (!thumbnail) {
//            } else {
////            if (bitmapReferences <= 0) {
//                recycleBitmap();
////            }
//
//                cached = false;
//            }
        }

        private void cleanViewsOnRemoveFromCache() {
            final int count = views.size();

            boolean used = false;
            for (int i = 0; i < count; ++i) {
                final WeakReference<ImageView> ref = views.get(i);
                final ImageView view = ref.get();

                if (view != null) {
                    if (view.getDrawable() == drawable) {
                        used = true;
                    } else {
                        ref.clear();
                    }
                }
            }
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    view.setImageDrawable(ImageDao.getLoadingThumbnailImage().getDrawableIfCached());
//                }
//            });

            if (!used) {
                recycleBitmap();
                cached = false;

                views.clear();
            } else {
                if (thumbnail) {
                    synchronized (thumbnailsBitmapCache) {
                        thumbnailsBitmapCache.put(cacheKey, this);
                    }
                } else {
                    synchronized (bitmapCache) {
                        bitmapCache.put(cacheKey, this);
                    }
                }
            }
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
            return seizeBitmap(null);
        }

        private Bitmap seizeBitmap(ImageView view) {
            if (bitmap != null) {
                ++bitmapReferences;

                if (view != null) {
                    views.add(new WeakReference<ImageView>(view));
                }

                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d(LentaConstants.LoggerAnyTag, "seizeBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
                }
            }

            return bitmap;
        }

        private void recycleBitmap() {
            if (bitmap != null) {
                bitmap.recycle();
                drawable = null;
                bitmap = null;
                if (LentaConstants.DEVELOPER_MODE) {
                    Log.d(LentaConstants.LoggerAnyTag, "recycleBitmap(): key: " + cacheKey + ", number of references: " + bitmapReferences);
                }
            }
        }
    }
}
