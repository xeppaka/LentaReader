package com.xeppaka.lentareader.data.dao.async;

import android.os.AsyncTask;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;

import java.util.List;

public interface AsyncNODao<T extends NewsObject> extends AsyncDao<T>, NODao<T> {
	AsyncTask<Rubrics, Void, List<T>> readAsync(Rubrics rubric, DaoReadMultiListener<T> listener);
    AsyncTask<Rubrics, Void, T> readLatestAsync(Rubrics rubric, DaoReadSingleListener<T> listener);
    AsyncTask readLatestWOImageAsync(Rubrics rubric, int limit, DaoReadSingleListener<T> listener);
    AsyncTask<Rubrics, Void, Integer> clearLatestFlagAsync(Rubrics rubric, DaoUpdateListener listener);
    AsyncTask<Rubrics, Void, Integer> setLatestFlagAsync(Rubrics rubric, DaoUpdateListener listener);
    AsyncTask deleteOlderOrEqualAsync(Rubrics rubric, long date, AsyncDao.DaoDeleteListener listener);
}
