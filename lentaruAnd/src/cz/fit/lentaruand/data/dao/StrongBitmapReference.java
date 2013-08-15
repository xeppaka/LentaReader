package cz.fit.lentaruand.data.dao;

import android.graphics.Bitmap;

public class StrongBitmapReference implements BitmapReference {
	private final Bitmap bitmap;
	
	public StrongBitmapReference(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public Bitmap getBitmap() {
		return bitmap;
	}

	@Override
	public void getBitmapAsync(BitmapLoadListener listener) {
		listener.onBitmapLoaded(bitmap);
	}

	@Override
	public void releaseBitmap() {
		return;
	}
}
