package cz.fit.lentaruand.data.dao;

import android.graphics.Bitmap;

public interface BitmapReference {
	public interface BitmapLoadListener {	
		void onBitmapLoaded(final Bitmap bitmap);
	}
	
	Bitmap getBitmap();
	void getBitmapAsync(final BitmapLoadListener listener);
	void releaseBitmap();
}
