package com.xeppaka.lentareader.data.dao.daoobjects;

import android.graphics.Bitmap;

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
	void getBitmapAsync(final Callback callback);
	void releaseBitmap();
}
