package com.xeppaka.lentareader.data.dao;

/**
 * Created by nnm on 11/30/13.
 */
public interface DaoObservable<T> {
    interface Observer<T> {
        void onDataChanged(boolean selfChange, T dataObject);
        void onDataChanged(boolean selfChange);
    }

    void registerContentObserver(Observer<T> observer);
    void unregisterContentObserver(Observer<T> observer);
}
