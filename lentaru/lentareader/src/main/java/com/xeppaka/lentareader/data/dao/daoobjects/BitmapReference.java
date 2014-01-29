package com.xeppaka.lentareader.data.dao.daoobjects;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * 
 * 
 * @author kacpa01
 *
 */
public interface BitmapReference {

	public interface LoadListener {
		void onSuccess(Bitmap bitmap);
        void onFailure(Exception e);
	}

    Bitmap getBitmap() throws Exception;
	Bitmap getBitmap(ImageView view) throws Exception;
	Bitmap getBitmapIfCached();
    Bitmap getBitmapIfCached(ImageView view);
	AsyncTask getBitmapAsync(LoadListener loadListener);
    AsyncTask getBitmapAsync(ImageView view, LoadListener loadListener);
	void releaseBitmap();
}
