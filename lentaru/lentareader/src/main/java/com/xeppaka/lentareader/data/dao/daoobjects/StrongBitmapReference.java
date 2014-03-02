package com.xeppaka.lentareader.data.dao.daoobjects;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xeppaka.lentareader.async.AsyncListener;

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
	public AsyncTask<AsyncListener<Bitmap>, Void, Bitmap> getBitmapAsync(AsyncListener<Bitmap> listener) {
        listener.onSuccess(bitmap);
        return null;
	}

    @Override
    public Bitmap getBitmap(ImageView view) throws Exception {
        return bitmap;
    }

    @Override
    public Bitmap getBitmapIfCached(ImageView view) {
        return bitmap;
    }

    @Override
    public AsyncTask getBitmapAsync(ImageView view, AsyncListener<Bitmap> listener) {
        listener.onSuccess(bitmap);
        return null;
    }

    @Override
	public void releaseBitmap() {}
}
