package com.xeppaka.lentareader.data.dao.daoobjects;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.xeppaka.lentareader.async.AsyncListener;

/**
 * 
 * 
 * @author kacpa01
 *
 */
public interface BitmapReference {
    Bitmap getBitmap() throws Exception;
	Bitmap getBitmap(ImageView view) throws Exception;
	Bitmap getBitmapIfCached();
    Bitmap getBitmapIfCached(ImageView view);
	AsyncTask getBitmapAsync(AsyncListener<Bitmap> listener);
    AsyncTask getBitmapAsync(ImageView view, AsyncListener<Bitmap> listener);
	void releaseBitmap();
    void releaseImageView(ImageView view);

    Drawable getDrawable() throws Exception;
    Drawable getDrawable(ImageView view) throws Exception;
    Drawable getDrawableIfCached();
    Drawable getDrawableIfCached(ImageView view);
    AsyncTask getDrawableAsync(AsyncListener<Drawable> listener);
    AsyncTask getDrawableAsync(ImageView view, AsyncListener<Drawable> listener);
}
