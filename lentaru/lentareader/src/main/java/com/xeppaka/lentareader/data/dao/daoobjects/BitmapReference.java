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
        void onFailure(Exception e);
	}
	
	Bitmap getBitmap() throws Exception;
	Bitmap getBitmapIfCached();
	AsyncTask getBitmapAsync(final Callback callback);
	void releaseBitmap();
}
