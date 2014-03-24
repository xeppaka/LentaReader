package com.xeppaka.lentareader.data.dao.daoobjects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xeppaka.lentareader.async.AsyncListener;

public class StrongBitmapReference implements BitmapReference {
	private final Bitmap bitmap;
    private final BitmapDrawable drawable;
	
	public StrongBitmapReference(Bitmap bitmap, Resources resources) {
		this.bitmap = bitmap;
        this.drawable = new BitmapDrawable(resources, bitmap);
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
    public Drawable getDrawable() throws Exception {
        return drawable;
    }

    @Override
    public Drawable getDrawable(ImageView view) throws Exception {
        return drawable;
    }

    @Override
    public Drawable getDrawableIfCached() {
        return drawable;
    }

    @Override
    public Drawable getDrawableIfCached(ImageView view) {
        return drawable;
    }

    @Override
    public AsyncTask getDrawableAsync(AsyncListener<Drawable> listener) {
        listener.onSuccess(drawable);
        return null;
    }

    @Override
    public AsyncTask getDrawableAsync(ImageView view, AsyncListener<Drawable> listener) {
        listener.onSuccess(drawable);
        return null;
    }

    @Override
	public void releaseBitmap() {}

    @Override
    public void releaseImageView(ImageView view) {}
}
