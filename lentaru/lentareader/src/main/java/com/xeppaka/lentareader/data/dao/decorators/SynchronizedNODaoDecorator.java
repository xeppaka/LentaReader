package com.xeppaka.lentareader.data.dao.decorators;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;

import java.util.List;

public class SynchronizedNODaoDecorator<T extends NewsObject> extends SynchronizedDaoDecorator<T> implements NODao<T> {
	protected NODao<T> decoratedDao;

	public SynchronizedNODaoDecorator(NODao<T> decoratedDao, Object sync) {
		super(decoratedDao, sync);
		
		this.decoratedDao = decoratedDao;
	}

	@Override
	public List<T> read(Rubrics rubric) {
		synchronized (getSync()) {
			return decoratedDao.read(rubric);
		}
	}

    @Override
    public boolean hasImage(long id) {
        synchronized (getSync()) {
            return decoratedDao.hasImage(id);
        }
    }

    @Override
    public boolean hasImage(String key) {
        synchronized (getSync()) {
            return decoratedDao.hasImage(key);
        }
    }

    @Override
    public T readLatestWOImage(Rubrics rubric, int limit) {
        synchronized (getSync()) {
            return decoratedDao.readLatestWOImage(rubric, limit);
        }
    }

    @Override
    public int clearLatestFlag(Rubrics rubric) {
        synchronized (getSync()) {
            return decoratedDao.clearLatestFlag(rubric);
        }
    }

    @Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
