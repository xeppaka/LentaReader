package com.xeppaka.lentareader.async;

import android.graphics.Bitmap;

/**
 * Created by nnm on 3/1/14.
 */
public interface AsyncListener<T> {
    void onSuccess(T value);
    void onFailure(Exception e);
}
