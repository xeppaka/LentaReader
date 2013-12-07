package com.xeppaka.lentareader.data.dao.daoobjects;

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
	public Bitmap getBitmapIfCached() {
		return bitmap;
	}
	
	@Override
	public void getBitmapAsync(BitmapReference.Callback callback) {
		callback.onSuccess(bitmap);
	}

	@Override
	public void releaseBitmap() {
		return;
	}
}
