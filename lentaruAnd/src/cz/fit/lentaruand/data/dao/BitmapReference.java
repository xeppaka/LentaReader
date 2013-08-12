package cz.fit.lentaruand.data.dao;

import android.graphics.Bitmap;

public interface BitmapReference {
	Bitmap getBitmap();
	void releaseBitmap();
}
