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
	public List<T> readForRubric(Rubrics rubric) {
		synchronized (getSync()) {
			return decoratedDao.readForRubric(rubric);
		}
	}

	@Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
