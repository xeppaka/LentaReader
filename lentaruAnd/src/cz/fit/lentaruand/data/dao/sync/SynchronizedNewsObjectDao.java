package cz.fit.lentaruand.data.dao.sync;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.async.AsyncNewsObjectDao;

public class SynchronizedNewsObjectDao<T extends NewsObject> extends SynchronizedDao<T> implements AsyncNewsObjectDao<T> {
	protected AsyncNewsObjectDao<T> decoratedDao;

	public SynchronizedNewsObjectDao(AsyncNewsObjectDao<T> decoratedDao, Object sync) {
		super(decoratedDao, sync);
	}

	@Override
	public void readAsyncForRubric(Rubrics rubric,
			cz.fit.lentaruand.data.dao.async.AsyncDao.DaoReadMultiListener<T> listener) {
		getUnderlinedDao().readAsyncForRubric(rubric, listener);
	}

	@Override
	protected AsyncNewsObjectDao<T> getUnderlinedDao() {
		return decoratedDao;
	}
}
