package com.xeppaka.lentareader.data.dao.async;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;

public interface AsyncNODao<T extends NewsObject> extends AsyncDao<T>, NODao<T> {
	void readForRubricAsync(Rubrics rubric, DaoReadMultiListener<T> listener);
}
