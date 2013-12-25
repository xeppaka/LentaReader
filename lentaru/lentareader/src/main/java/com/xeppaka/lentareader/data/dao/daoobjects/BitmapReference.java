package com.xeppaka.lentareader.data.dao.daoobjects;

import android.graphics.Bitmap;
import android.os.AsyncTask;

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
	public interface Callback {
		void onSuccess(final Bitmap bitmap);
        void onFailure();
	}
	
	Bitmap getBitmap();
	Bitmap getBitmapIfCached();
	AsyncTask<Callback, Void, Bitmap> getBitmapAsync(final Callback callback);
	void releaseBitmap();
}
