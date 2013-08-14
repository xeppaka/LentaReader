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
	private static final int cacheSize = 7 * 1024 * 1024; // 7MB
	private static final LruCache<String, CachedLazyLoadBitmapReference> bitmapCache = new LruCache<String, CachedLazyLoadBitmapReference>(cacheSize) {
		@Override
		protected void entryRemoved(boolean evicted, String key,
				CachedLazyLoadBitmapReference oldValue, CachedLazyLoadBitmapReference newValue) {
			oldValue.onRemoveFromCache();
		}
	};
	
	private final ContentResolver contentResolver;
	private final StrongBitmapReference notAvailableImageRef;
	
	private static final BitmapFactory.Options bitmapThumbnailOptions = new BitmapFactory.Options();
	
	private ImageDao(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
		notAvailableImageRef = new StrongBitmapReference(createNotAvailableBitmap());
		bitmapThumbnailOptions.inSampleSize = 4;
	}
	
	public static ImageDao getInstance(ContentResolver contentResolver) {
		return new ImageDao(contentResolver);
	}
	
	public BitmapReference read(String imageUrl) {
		return read(imageUrl, false);
	}
	
	public BitmapReference read(String imageUrl, boolean cacheOnly) {
		Log.d(LentaConstants.LoggerAnyTag,
				"Trying to load bitmap with from disk or memory cache with URL: "
						+ imageUrl);

		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return readNotAvailableImage();
		}
		
		CachedLazyLoadBitmapReference imageRef = bitmapCache.get(imageKey);

		if (cacheOnly || imageRef != null) {
			Log.d(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
			return imageRef;
		}
		
		imageRef = new CachedLazyLoadBitmapReference(imageKey);
		bitmapCache.put(imageKey, imageRef);
		
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
			imageKey = URLHelper.getThumbnailImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return readNotAvailableImage();
		}
		
		CachedLazyLoadBitmapReference imageRef = bitmapCache.get(imageKey);

		if (cacheOnly || imageRef != null) {
			Log.d(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
			return imageRef;
		}
		
		imageRef = new CachedLazyLoadBitmapReference(imageKey, true);
		bitmapCache.put(imageKey, imageRef);
		
		return imageRef;
	}
	
	public BitmapReference readNotAvailableImage() {
		return notAvailableImageRef;
	}
	
	public BitmapReference create(String imageUrl, Bitmap bitmap) {
		Log.d(LentaConstants.LoggerAnyTag, "Create bitmap in disk and/or memory cache for image URL: " + imageUrl);
		
		String imageKey;
		try {
			imageKey = URLHelper.getImageId(imageUrl);
		} catch (MalformedURLException e) {
			Log.e(LentaConstants.LoggerAnyTag, "Error getting key for image URL: " + imageUrl);
			return readNotAvailableImage();
		}
		
		CachedLazyLoadBitmapReference imageRef = new CachedLazyLoadBitmapReference(imageKey, bitmap);
		bitmapCache.put(imageKey, imageRef);
		Log.d(LentaConstants.LoggerAnyTag, "Put bitmap to cache. Cache size: " + bitmapCache.size());
		
		createBitmapOnDisk(imageKey, bitmap);
		
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
		
		CachedLazyLoadBitmapReference bitmap = bitmapCache.get(imageKey);
		
		if (bitmap != null) {
			return bitmap.isBitmapInMemory();
		}
		
		return false;
	}
	
	private Bitmap createNotAvailableBitmap() {
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
		private int bitmapReferences;
		private Bitmap bitmap;
		private boolean recycled;
		private boolean cached;
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
		
		public CachedLazyLoadBitmapReference(String key, boolean isThumbnail) {
			this(key, null, isThumbnail);
		}
		
		public CachedLazyLoadBitmapReference(String key, Bitmap bitmap, boolean thumbnail) {
			this.bitmap = bitmap;
			this.cacheKey = key;
			this.recycled = false;
			this.cached = true;
			this.thumbnail = thumbnail;
		}
		
		@Override
		public synchronized Bitmap getBitmap() {
			if (bitmap == null) {
				bitmap = readBitmapFromDisk(cacheKey, thumbnail);
				
				if (bitmap == null) {
					return notAvailableImageRef.getBitmap();
				} else {
					Log.d(LentaConstants.LoggerAnyTag, "Loaded from disk. Bitmap size: " + bitmap.getByteCount() + " bytes.");
					/*
					 * bitmap == null -> when we put this reference in the
					 * cache last time, it returned size 0. put it again with
					 * bitmap != null -> it will report correct size and size of
					 * cache will be recalculated.
					 */
					bitmapCache.put(cacheKey, this);
				}
			}
			
			++bitmapReferences;
			return bitmap;
		}
		
		/*
		 * Note: <b>MUST be called from UI thread.</b> 
		 * (non-Javadoc)
		 * @see cz.fit.lentaruand.data.dao.BitmapReference#getBitmapAsync(cz.fit.lentaruand.data.dao.BitmapReference.BitmapLoadListener)
		 */
		@Override
		public synchronized void getBitmapAsync(BitmapLoadListener listener) {
			if (bitmap != null && bitmap != notAvailableImageRef.getBitmap()) {
				++bitmapReferences;
				listener.onBitmapLoaded(bitmap);
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
		
		private synchronized void recycleBitmap() {
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
			
			recycled = true;
		}
	}
}
