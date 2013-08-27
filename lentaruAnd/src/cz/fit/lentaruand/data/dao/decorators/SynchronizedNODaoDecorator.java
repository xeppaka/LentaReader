package cz.fit.lentaruand.data.dao.decorators;

import java.util.Collection;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.NODao;

public class SynchronizedNODaoDecorator<T extends NewsObject> extends SynchronizedDaoDecorator<T> implements NODao<T> {
	protected NODao<T> decoratedDao;

	public SynchronizedNODaoDecorator(NODao<T> decoratedDao, Object sync) {
		super(decoratedDao, sync);
		
		this.decoratedDao = decoratedDao;
	}

	@Override
	public Collection<T> readForRubric(Rubrics rubric) {
		synchronized (getSync()) {
			return decoratedDao.readForRubric(rubric);
		}
	}

	@Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
