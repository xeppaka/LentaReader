package com.xeppaka.lentareader.data.dao.async;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;

public interface AsyncNODao<T extends NewsObject> extends AsyncDao<T>, NODao<T> {
	void readAsync(Rubrics rubric, DaoReadMultiListener<T> listener);
    void readLatestWOImageAsync(Rubrics rubric, int limit, DaoReadSingleListener<T> listener);
    void clearLatestFlagAsync(Rubrics rubric, DaoUpdateListener listener);
}
