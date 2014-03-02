package com.xeppaka.lentareader.data.dao.async;

import android.database.Cursor;
import android.os.AsyncTask;

import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;

import java.util.List;

public interface AsyncNODao<T extends NewsObject> extends AsyncDao<T>, NODao<T> {
    AsyncTask<Rubrics, Void, List<T>> readAsync(Rubrics rubric, AsyncListener<List<T>> listener);
    AsyncTask<Rubrics, Void, List<T>> readBriefAsync(Rubrics rubric, AsyncListener<List<T>> listener);
    AsyncTask<Rubrics, Void, T> readLatestAsync(Rubrics rubric, AsyncListener<T> listener);
    AsyncTask readLatestWOImageAsync(Rubrics rubric, int limit, AsyncListener<T> listener);
    AsyncTask<Rubrics, Void, Integer> clearLatestFlagAsync(Rubrics rubric, AsyncListener<Integer> listener);
    AsyncTask<Rubrics, Void, Integer> setLatestFlagAsync(Rubrics rubric, AsyncListener<Integer> listener);
    AsyncTask deleteOlderOrEqualAsync(Rubrics rubric, long date, AsyncListener<Integer> listener);
    AsyncTask<Rubrics, Void, List<Long>> readAllIdsAsync(Rubrics rubric, AsyncListener<List<Long>> listener);
}
