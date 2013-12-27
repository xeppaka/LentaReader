package com.xeppaka.lentareader.data.dao.daoobjects;

import android.graphics.Bitmap;
import android.os.AsyncTask;

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
	public AsyncTask<Callback, Void, Bitmap> getBitmapAsync(BitmapReference.Callback callback) {
		callback.onSuccess(bitmap);
        return null;
	}

	@Override
	public void releaseBitmap() {
		return;
	}
}
