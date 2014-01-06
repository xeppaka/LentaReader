package com.xeppaka.lentareader.data.dao.async;

import android.os.AsyncTask;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;

import java.util.List;

public interface AsyncNODao<T extends NewsObject> extends AsyncDao<T>, NODao<T> {
	AsyncTask<Rubrics, Void, List<T>> readAsync(Rubrics rubric, DaoReadMultiListener<T> listener);
    AsyncTask<Rubrics, Void, T> readLatestAsync(Rubrics rubric, DaoReadSingleListener<T> listener);
    void readLatestWOImageAsync(Rubrics rubric, int limit, DaoReadSingleListener<T> listener);
    void clearLatestFlagAsync(Rubrics rubric, DaoUpdateListener listener);
}
