package cz.fit.lentaruand.data.dao.objects;

import android.graphics.Bitmap;

/**
 * 
 * 
 * @author kacpa01
 *
 */
public interface BitmapReference {
	
	/**
	 * 
	 * 
	 * @author kacpa01
	 *
	 */
	public interface BitmapLoadListener {	
		void onBitmapLoaded(final Bitmap bitmap);
	}
	
	Bitmap getBitmap();
	Bitmap getBitmapIfCached();
	void getBitmapAsync(final BitmapLoadListener listener);
	void releaseBitmap();
}
