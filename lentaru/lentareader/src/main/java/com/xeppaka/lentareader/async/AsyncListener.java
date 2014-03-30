package com.xeppaka.lentareader.async;

/**
 * Created by nnm on 3/1/14.
 */
public interface AsyncListener<T> {
    void onSuccess(T value);
    void onFailure(Exception e);
}
