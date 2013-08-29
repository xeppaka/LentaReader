package cz.fit.lentaruand.data.dao.objects;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.v4.util.LruCache;
import android.util.Log;
import cz.fit.lentaruand.data.provider.LentaProvider;
import cz.fit.lentaruand.utils.LentaConstants;
import cz.fit.lentaruand.utils.URLHelper;

/**
 * This class is responsible for maintaining all images in the application that
 * are part of news, articles, etc. When image is downloaded it should be saved
 * in this class by using {@link ImageDao#create(String, Bitmap)}.
 * <p>
 * Users of images do not use {@link Bitmap} class instances directly, instead
 * they use {@link BitmapReference} with getBitmap(), releaseBitmap() methods. 
 * For more info look at {@link BitmapReference}.
 * <p>
 * 
 * @author kacpa01
 * 
 */
public class ImageDao {
	// how many times thumbnail image is smaller than full image
	private static final float SAMPLE_SIZE_COEFF = 4;
	
	private static final LruCache<String, CachedLazyLoadBitmapReference> fullBitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(LentaConstants.BITMAP_CACHE_MAX_SIZE_IN_BYTES) {
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
	
	private static final LruCache<String, CachedLazyLoadBitmapReference> thumbnailBitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(LentaConstants.BITMAP_THUMBNAIL_CACHE_MAX_SIZE_IN_BYTES) {
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
	
	private final ContentResolver contentResolver;
	private final static StrongBitmapReference notAvailableImageRef;
	
	private static final BitmapFactory.Options bitmapThumbnailOptions = new BitmapFactory.Options();
	
	static {
		notAvailableImageRef = new StrongBitmapReference(createNotAvailableBitmap());
	}
	
	private ImageDao(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
		bitmapThumbnailOptions.inSampleSize = (int) SAMPLE_SIZE_COEFF;
	}
	
	public static ImageDao getInstance(ContentResolver contentResolver) {
		return new ImageDao(contentResolver);
	}
	
	public BitmapReference read(String imageUrl) {
		return read(imageUrl, false);
	}
	
	private BitmapReference read(String imageUrl, boolean cacheOnly) {
		Log.d(LentaConstants.LoggerAnyTag,
				"ImageDao read bitmap with URL: "
						+ imageUrl);

		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return getNotAvailableImage();
		}
		
		CachedLazyLoadBitmapReference imageRef;
		synchronized(fullBitmapCache) {
			imageRef = fullBitmapCache.get(imageKey);
		}

		if (cacheOnly || imageRef != null) {
			Log.d(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
			return imageRef;
		}
		
		imageRef = new CachedLazyLoadBitmapReference(imageKey);
		synchronized(fullBitmapCache) {
			fullBitmapCache.put(imageKey, imageRef);
		}
		
		Log.d(LentaConstants.LoggerAnyTag, "Created empty lazy load reference.");
		
		return imageRef;
	}
	
	public BitmapReference readThumbnail(String imageUrl) {
		return readThumbnail(imageUrl, false);
	}
	
	private BitmapReference readThumbnail(String imageUrl, boolean cacheOnly) {
		Log.d(LentaConstants.LoggerAnyTag,
				"ImageDao read thumbnail bitmap with URL: "
						+ imageUrl);

		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return getNotAvailableImage();
		}
		
		CachedLazyLoadBitmapReference imageRef;
		synchronized(thumbnailBitmapCache) {
			imageRef = thumbnailBitmapCache.get(imageKey);
		}

		if (cacheOnly || imageRef != null) {
			Log.d(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
			return imageRef;
		}
		
		imageRef = new CachedLazyLoadBitmapReference(imageKey, true);
		synchronized(thumbnailBitmapCache) {
			thumbnailBitmapCache.put(imageKey, imageRef);
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
	 * <p>
	 * Thumbnail image in the cache is created by default.
	 * 
	 * @param imageUrl
	 *            is the URL where image was downloaded. Used as a key for the
	 *            image.
	 * @param bitmap
	 *            is the to save.
	 * @return reference to the BitmapReference instance. Should be used later
	 *         on to retrieve image from the cache.
	 */
	public BitmapReference create(String imageUrl, Bitmap bitmap) {
		return create(imageUrl, bitmap, true);
	}
	
	/**
	 * Creates bitmap in cache. It means it will store bitmap in the memory
	 * cache and save it to the external drive cache if it's available.
	 * 
	 * @param imageUrl
	 *            is the URL where image was downloaded. Used as a key for the
	 *            image.
	 * @param bitmap
	 *            is the to save.
	 * @param withThumbnail
	 *            indicates if thumbnail should be automatically created and
	 *            pushed into the cache.
	 * @return reference to the BitmapReference instance. Should be used later
	 *         on to retrieve image from the cache.
	 */
	public BitmapReference create(String imageUrl, Bitmap bitmap, boolean withThumbnail) {
		Log.d(LentaConstants.LoggerAnyTag, "Create bitmap in disk and/or memory cache for image URL: " + imageUrl);
		
		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return getNotAvailableImage();
		}
		
		CachedLazyLoadBitmapReference imageRef = new CachedLazyLoadBitmapReference(imageKey, bitmap);
		try {
			createBitmapOnDisk(imageKey, bitmap);
			
			synchronized(fullBitmapCache) {
				fullBitmapCache.put(imageKey, imageRef);
			}
			Log.d(LentaConstants.LoggerAnyTag, "Put bitmap to cache. Cache size: " + fullBitmapCache.size());
			
			if (withThumbnail) {
				CachedLazyLoadBitmapReference thumbnailBitmapRef = new CachedLazyLoadBitmapReference(imageKey, createThumbnailBitmap(bitmap));
				try {
					synchronized(thumbnailBitmapCache) {
						thumbnailBitmapCache.put(imageKey, thumbnailBitmapRef);
					}
				} finally {
					/**
					 * When CachedLazyLoadBitmapReference is created it supposes that we are
					 * the one user of the bitmap. So we need to release bitmap before
					 * return lazy load reference to it.
					 */
					thumbnailBitmapRef.releaseBitmap();
				}
			}
		} finally {
			/**
			 * When CachedLazyLoadBitmapReference is created it supposes that we are
			 * the one user of the bitmap. So we need to release bitmap before
			 * return lazy load reference to it.
			 */
			imageRef.releaseBitmap();
		}
		
		return imageRef;
	}
	
	public boolean imageExist(String imageUrl) {
		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return false;
		}
		
		if (isBitmapInMemory(imageKey) || checkImageInDiskCache(imageKey)) {
			return true;
		}
		
		return false;
	}
	
	private boolean checkImageInDiskCache(String imageKey) {
		Log.d(LentaConstants.LoggerAnyTag, "Check bitmap in disk cache for image key: " + imageKey);
		
		ParcelFileDescriptor.AutoCloseInputStream input = null;
		
		try {
			ParcelFileDescriptor fileDescriptor = contentResolver
					.openFileDescriptor(
							LentaProvider.CONTENT_URI_CACHED_IMAGE.buildUpon().appendPath(imageKey).build(), "r");
			input = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
			
			return true;
		} catch (FileNotFoundException e) {
			return false;
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
	
	private boolean isBitmapInMemory(String imageKey) {
		Log.d(LentaConstants.LoggerAnyTag, "Check bitmap in memory cache for image key: " + imageKey);
		
		CachedLazyLoadBitmapReference bitmap;
		synchronized(fullBitmapCache) {
			bitmap = fullBitmapCache.get(imageKey);
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
	
	private static Bitmap createThumbnailBitmap(Bitmap fullBitmap) {
		return Bitmap.createScaledBitmap(
				fullBitmap,
				Math.round(fullBitmap.getWidth() / SAMPLE_SIZE_COEFF),
				Math.round(fullBitmap.getHeight() / SAMPLE_SIZE_COEFF), false);
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
	
	private Bitmap readBitmapFromDisk(String key, boolean thumbnail) {
		ParcelFileDescriptor.AutoCloseInputStream input = null;
		try {
			ParcelFileDescriptor fileDescriptor = contentResolver
					.openFileDescriptor(
							LentaProvider.CONTENT_URI_CACHED_IMAGE.buildUpon().appendPath(key).build(), "r");
			input = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
			
			if (thumbnail) {
				return BitmapFactory.decodeStream(input, null, bitmapThumbnailOptions);
			} else {	
				return BitmapFactory.decodeStream(input);
			}
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
	
	public class CachedLazyLoadBitmapReference implements BitmapReference {
		private volatile int bitmapReferences;
		private Bitmap bitmap;
		private volatile boolean cached;
		private final String cacheKey;
		private final boolean thumbnail;
		
		private class BitmapLoadTask extends AsyncTask<BitmapLoadListener, Void, Bitmap> {
			private BitmapLoadListener[] listeners;
			
			@Override
			protected Bitmap doInBackground(BitmapLoadListener... listeners) {
				this.listeners = listeners;
				
				return getBitmap();
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				for (BitmapLoadListener listener : listeners) {
					listener.onBitmapLoaded(result);
				}
			}
		}
		
		public CachedLazyLoadBitmapReference(String key) {
			this(key, null);
		}
		
		public CachedLazyLoadBitmapReference(String key, Bitmap bitmap) {
			this(key, bitmap, false);
		}
		
		public CachedLazyLoadBitmapReference(String key, boolean thumbnail) {
			this(key, null, thumbnail);
		}
		
		public CachedLazyLoadBitmapReference(String key, Bitmap bitmap, boolean thumbnail) {
			if (bitmap != null) {
				this.bitmap = bitmap;
				
				/**
				 * This class suppose that the user of the bitmap already has
				 * one reference to the bitmap. Also it allows to not recycle
				 * bitmap immediately after creation if cache is full of
				 * bitmaps.
				 */
				bitmapReferences = 1;
			}
			
			this.cacheKey = key;
			this.cached = true;
			this.thumbnail = thumbnail;
		}
		
		private Bitmap tryCreateThumbnailFromFullBitmapInCache() {
			CachedLazyLoadBitmapReference fullBitmapRef;
			synchronized(fullBitmapCache) {
				fullBitmapRef = fullBitmapCache.get(cacheKey);
			}
			
			if (fullBitmapRef != null) {
				Bitmap fullBitmap = fullBitmapRef.getBitmapIfCached();
				
				if (fullBitmap != null) {
					try {
						return createThumbnailBitmap(fullBitmap);
					} finally {
						fullBitmapRef.releaseBitmap();
					}
				}
			}
			
			return null;
		}
		
		@Override
		public synchronized Bitmap getBitmap() {
			if (bitmap == null) {
				Bitmap loadedBitmap = null;

				if (thumbnail) {
					/**
					 * we can try to create thumbnail from full bitmap if full
					 * bitmap is in the cache
					 */
					loadedBitmap = tryCreateThumbnailFromFullBitmapInCache();
				}
				
				if (loadedBitmap == null) {
					loadedBitmap = readBitmapFromDisk(cacheKey, thumbnail);
					
					if (loadedBitmap != null) {
						Log.d(LentaConstants.LoggerAnyTag, "Loaded from disk. Bitmap size: " + loadedBitmap.getByteCount() + " bytes.");
					}
				}
				
				/*
				 * bitmap was == null -> when we've put this reference in the
				 * cache last time, it returned size 0. put it again with
				 * bitmap != null -> it will report correct size and size of
				 * cache will be recalculated.
				 */
				if (loadedBitmap != null) {
					LruCache<String, CachedLazyLoadBitmapReference> cache;
					
					if (thumbnail) {
						cache = thumbnailBitmapCache;
					} else {
						cache = fullBitmapCache;
					}
					
					synchronized(cache) {
						if (cache.get(cacheKey) == this) {
							CachedLazyLoadBitmapReference imageRef = cache.remove(cacheKey);
							bitmap = loadedBitmap;
							cache.put(cacheKey, imageRef);
						} else {
							bitmap = loadedBitmap;
						}
					}
				}
			}

			return seizeBitmap();
		}

		@Override
		public synchronized Bitmap getBitmapIfCached() {
			return seizeBitmap();
		}

		/*
		 * Note: <b>MUST be called from UI thread.</b> 
		 * (non-Javadoc)
		 * @see cz.fit.lentaruand.data.dao.BitmapReference#getBitmapAsync(cz.fit.lentaruand.data.dao.BitmapReference.BitmapLoadListener)
		 */
		@Override
		public void getBitmapAsync(BitmapLoadListener listener) {
			Bitmap seizedBitmap = seizeBitmap();
			if (seizedBitmap != null) {
				listener.onBitmapLoaded(seizedBitmap);
			} else {
				new BitmapLoadTask().execute(listener);
			}
		}

		public synchronized void releaseBitmap() {
			if (bitmap != null && --bitmapReferences <= 0 && !isCached()) {
				Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): thumbnail: " + String.valueOf(thumbnail) + ", key: " + cacheKey + ", number of references: " + bitmapReferences);
				
				recycleBitmap();
			}
			
			Log.d(LentaConstants.LoggerAnyTag, "releaseBitmap(): thumbnail: " + String.valueOf(thumbnail) + ", key: " + cacheKey + ", number of references: " + bitmapReferences);
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
				Log.d(LentaConstants.LoggerAnyTag, "seizeBitmap(): thumbnail: " + String.valueOf(thumbnail) + ", key: " + cacheKey + ", number of references: " + bitmapReferences);
			}
			
			return bitmap;
		}
		
		private void recycleBitmap() {
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
				Log.d(LentaConstants.LoggerAnyTag, "recycleBitmap(): thumbnail: " + String.valueOf(thumbnail) + ", key: " + cacheKey + ", number of references: " + bitmapReferences);
			}
		}
	}
}
