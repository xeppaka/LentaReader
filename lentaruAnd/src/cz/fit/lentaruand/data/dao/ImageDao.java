package cz.fit.lentaruand.data.dao;

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

public class ImageDao {
	private static final int sampleSizeCoeff = 4;
	private static final int fullBitmapCacheSize = 5 * 1024 * 1024; // 5 MB
	private static final int thumbnailBitmapCacheSize = 3 * 1024 * 1024; // 3 MB
	private static final LruCache<String, CachedLazyLoadBitmapReference> fullBitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(fullBitmapCacheSize) {
		@Override
		protected void entryRemoved(boolean evicted, String key,
				CachedLazyLoadBitmapReference oldValue, CachedLazyLoadBitmapReference newValue) {
			if (newValue != oldValue) {
				oldValue.onRemoveFromCache();
			}
		}

		@Override
		protected int sizeOf(String key, CachedLazyLoadBitmapReference value) {
			return value.bitmapSize();
		}
	};
	
	private static final LruCache<String, CachedLazyLoadBitmapReference> thumbnailBitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(thumbnailBitmapCacheSize) {
		@Override
		protected void entryRemoved(boolean evicted, String key,
				CachedLazyLoadBitmapReference oldValue, CachedLazyLoadBitmapReference newValue) {
			if (newValue != oldValue) {
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
		bitmapThumbnailOptions.inSampleSize = sampleSizeCoeff;
	}
	
	public static ImageDao getInstance(ContentResolver contentResolver) {
		return new ImageDao(contentResolver);
	}
	
	public BitmapReference read(String imageUrl) {
		return read(imageUrl, false);
	}
	
	private BitmapReference read(String imageUrl, boolean cacheOnly) {
		Log.d(LentaConstants.LoggerAnyTag,
				"Trying to load bitmap with from disk or memory cache with URL: "
						+ imageUrl);

		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return getNotAvailableImage();
		}
		
		CachedLazyLoadBitmapReference imageRef = fullBitmapCache.get(imageKey);

		if (cacheOnly || imageRef != null) {
			Log.d(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
			return imageRef;
		}
		
		imageRef = new CachedLazyLoadBitmapReference(imageKey);
		fullBitmapCache.put(imageKey, imageRef);
		
		return imageRef;
	}
	
	public BitmapReference readThumbnail(String imageUrl) {
		return readThumbnail(imageUrl, false);
	}
	
	public BitmapReference readThumbnail(String imageUrl, boolean cacheOnly) {
		Log.d(LentaConstants.LoggerAnyTag,
				"Trying to load thumbnail bitmap with from disk or memory cache with URL: "
						+ imageUrl);

		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return getNotAvailableImage();
		}
		
		CachedLazyLoadBitmapReference imageRef = thumbnailBitmapCache.get(imageKey);

		if (cacheOnly || imageRef != null) {
			Log.d(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
			return imageRef;
		}
		
		imageRef = new CachedLazyLoadBitmapReference(imageKey, true);
		thumbnailBitmapCache.put(imageKey, imageRef);
		
		return imageRef;
	}
	
	public static BitmapReference getNotAvailableImage() {
		return notAvailableImageRef;
	}
	
	/**
	 * 
	 * 
	 * @param imageUrl
	 * @param bitmap
	 * @return
	 */
	public BitmapReference create(String imageUrl, Bitmap bitmap) {
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
			fullBitmapCache.put(imageKey, imageRef);
			Log.d(LentaConstants.LoggerAnyTag, "Put bitmap to cache. Cache size: " + fullBitmapCache.size());
			
			createBitmapOnDisk(imageKey, bitmap);
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
	
	public boolean checkImageInDiskCache(String imageUrl) {
		Log.d(LentaConstants.LoggerAnyTag, "Check bitmap in disk cache for image URL: " + imageUrl);
		
		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return false;
		}
		
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
	
	public boolean isBitmapInMemory(String imageUrl) {
		Log.d(LentaConstants.LoggerAnyTag, "Check bitmap in memory cache for image URL: " + imageUrl);
		
		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return false;
		}
		
		CachedLazyLoadBitmapReference bitmap = fullBitmapCache.get(imageKey);
		
		if (bitmap != null) {
			return bitmap.isBitmapInMemory();
		}
		
		return false;
	}
	
	public boolean isThumbnailBitmapInMemory(String imageUrl) {
		Log.d(LentaConstants.LoggerAnyTag, "Check bitmap in memory cache for image URL: " + imageUrl);
		
		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return false;
		}
		
		CachedLazyLoadBitmapReference bitmap = thumbnailBitmapCache.get(imageKey);
		
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
			Log.e(LentaConstants.LoggerAnyTag, "Error occured during saving bitmap with id: " + key, e);
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
			Log.e(LentaConstants.LoggerAnyTag, "Error occured during reading bitmap with id: " + key, e);
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
		private volatile boolean recycled;
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
			this.recycled = false;
			this.cached = true;
			this.thumbnail = thumbnail;
		}
		
		private Bitmap tryCreateThumbnailFromFullBitmapInCache() {
			CachedLazyLoadBitmapReference fullBitmapRef = fullBitmapCache.get(cacheKey);
			
			if (fullBitmapRef != null) {
				Bitmap fullBitmap = fullBitmapRef.getBitmapIfCached();
				
				if (fullBitmap != null) {
					return Bitmap.createScaledBitmap(
							fullBitmap,
							Math.round(fullBitmap.getWidth() / (float) sampleSizeCoeff),
							Math.round(fullBitmap.getHeight() / (float) sampleSizeCoeff), false);
				}
			}
			
			return null;
		}
		
		@Override
		public synchronized Bitmap getBitmap() {
			if (bitmap == null) {
				if (thumbnail) {
					/**
					 * we can try to create thumbnail from full bitmap if full
					 * bitmap is in the cache
					 */
					bitmap = tryCreateThumbnailFromFullBitmapInCache();
				}
				
				if (bitmap == null) {
					bitmap = readBitmapFromDisk(cacheKey, thumbnail);
					if (bitmap != null) {
						Log.d(LentaConstants.LoggerAnyTag, "Loaded from disk. Bitmap size: " + bitmap.getByteCount() + " bytes.");
					}
				}
				
				/*
				 * bitmap was == null -> when we've put this reference in the
				 * cache last time, it returned size 0. put it again with
				 * bitmap != null -> it will report correct size and size of
				 * cache will be recalculated.
				 */
				if (bitmap != null) {
					if (thumbnail) {
						thumbnailBitmapCache.put(cacheKey, this);
					} else {
						fullBitmapCache.put(cacheKey, this);
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
		public synchronized void getBitmapAsync(BitmapLoadListener listener) {
			if (bitmap != null) {
				listener.onBitmapLoaded(seizeBitmap());
			} else {
				new BitmapLoadTask().execute(listener);
			}
		}

		public synchronized void releaseBitmap() {
			if (bitmap != null && --bitmapReferences <= 0 && !isCached()) {
				recycleBitmap();
			}
		}

		public synchronized boolean isRecycled() {
			return recycled;
		}

		public synchronized boolean isCached() {
			return cached;
		}
		
		public synchronized boolean isBitmapInMemory() {
			return bitmap != null && !recycled;
		}
		
		private synchronized void onRemoveFromCache() {
			if (bitmapReferences <= 0) {
				recycleBitmap();
				bitmap = null;
			}
			
			cached = false;
		}
		
		private synchronized int bitmapSize() {
			if (bitmap != null) {
				return bitmap.getByteCount();
			}
			
			return 0;
		}

		private Bitmap seizeBitmap() {
			if (bitmap != null) {
				++bitmapReferences;
			}
			
			return bitmap;
		}
		
		private void recycleBitmap() {
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
			
			recycled = true;
		}
	}
}
