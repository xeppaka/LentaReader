package cz.fit.lentaruand.data.dao;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.ParcelFileDescriptor;
import android.support.v4.util.LruCache;
import android.util.Log;
import cz.fit.lentaruand.data.provider.LentaProvider;
import cz.fit.lentaruand.utils.LentaConstants;

public class ImageDao {
	private static final int cacheSize = 7 * 1024 * 1024; // 7MB
	private static final LruCache<String, CachedLazyLoadBitmapReference> imageCache = new LruCache<String, CachedLazyLoadBitmapReference>(cacheSize) {
		@Override
		protected int sizeOf(String key, CachedLazyLoadBitmapReference value) {
			return value.bitmapSize();
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
				CachedLazyLoadBitmapReference oldValue, CachedLazyLoadBitmapReference newValue) {
			oldValue.onRemoveFromCache();
		}
	};
	
	private final ContentResolver contentResolver;
	private final StrongBitmapReference notAvailableImageRef;
	
	private ImageDao(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
		notAvailableImageRef = new StrongBitmapReference(createNotAvailableBitmap());
	}
	
	public static ImageDao getInstance(ContentResolver contentResolver) {
		return new ImageDao(contentResolver);
	}
	
	public BitmapReference read(String key) {
		Log.i(LentaConstants.LoggerAnyTag, "Load bitmap for key: " + key);
		
		CachedLazyLoadBitmapReference imageRef = imageCache.get(key);
		
		if (imageRef != null) {
			Log.i(LentaConstants.LoggerAnyTag, "Found in cache. Bitmap size: " + imageRef.bitmapSize());
			return imageRef;
		}
		
		imageRef = new CachedLazyLoadBitmapReference(key);
		imageCache.put(key, imageRef);
		
		return imageRef;
	}
	
	public BitmapReference readNotAvailableImage() {
		return notAvailableImageRef;
	}
	
	public BitmapReference create(String key, Bitmap bitmap) {
		Log.i(LentaConstants.LoggerAnyTag, "Create bitmap for key: " + key);
		
		CachedLazyLoadBitmapReference imageRef = new CachedLazyLoadBitmapReference(key, bitmap);
		imageCache.put(key, imageRef);
		
		Log.i(LentaConstants.LoggerAnyTag, "Put bitmap to cache. Cache size: " + imageCache.size());
		
		createBitmapOnDisk(key, bitmap);
		
		return imageRef;
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
	
	private Bitmap readBitmapFromDisk(String key) {
		ParcelFileDescriptor.AutoCloseInputStream input = null;
		try {
			ParcelFileDescriptor fileDescriptor = contentResolver
					.openFileDescriptor(
							LentaProvider.CONTENT_URI_CACHED_IMAGE.buildUpon().appendPath(key).build(), "r");
			input = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
			
			return BitmapFactory.decodeStream(input);
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
		
		public CachedLazyLoadBitmapReference(String key) {
			this(key, null);
		}
		
		public CachedLazyLoadBitmapReference(String key, Bitmap bitmap) {
			this.bitmap = bitmap;
			this.cacheKey = key;
			this.recycled = false;
			this.cached = true;
		}
		
		@Override
		public synchronized Bitmap getBitmap() {
			if (bitmap == null || bitmap == notAvailableImageRef.getBitmap()) {
				bitmap = readBitmapFromDisk(cacheKey);
				
				if (bitmap == null) {
					bitmap = notAvailableImageRef.getBitmap();
				} else {
					Log.i(LentaConstants.LoggerAnyTag, "Loaded from disk. Bitmap size: " + bitmap.getByteCount() + " bytes.");
				}
			}
			
			++bitmapReferences;
			return bitmap;
		}
		
		public synchronized void releaseBitmap() {
			if (--bitmapReferences <= 0 && !isCached()) {
				recycleBitmap();
			}
		}

		public synchronized boolean isRecycled() {
			return recycled;
		}

		public synchronized boolean isCached() {
			return cached;
		}
		
		private void onRemoveFromCache() {
			if (bitmapReferences <= 0) {
				recycleBitmap();
				bitmap = null;
			}
			
			cached = false;
		}
		
		private int bitmapSize() {
			if (bitmap != null) {
				return bitmap.getByteCount();
			}
			
			return 0;
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
