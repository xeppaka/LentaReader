package com.xeppaka.lentareader.data.dao.decorators;

import java.util.Collection;

import android.os.AsyncTask;
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;

public class AsyncNODaoDecorator<T extends NewsObject> extends AsyncDaoDecorator<T> implements AsyncNODao<T>, NODao<T> {
	private NODao<T> decoratedDao;
	
	protected class AsyncReadMultiForRubricTask extends AsyncTask<Rubrics, Void, Collection<T>> {
		private AsyncDao.DaoReadMultiListener<T> listener;
		
		public AsyncReadMultiForRubricTask(AsyncDao.DaoReadMultiListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected Collection<T> doInBackground(Rubrics... rubric) {
			return readForRubric(rubric[0]);
		}

		@Override
		protected void onPostExecute(Collection<T> result) {
			listener.finished(result);
		}
	}
	
	public AsyncNODaoDecorator(NODao<T> decoratedDao) {
		super(decoratedDao);
		
		this.decoratedDao = decoratedDao;
	}

	@Override
	public void readAsyncForRubric(Rubrics rubric,
			com.xeppaka.lentareader.data.dao.async.AsyncDao.DaoReadMultiListener<T> listener) {
		new AsyncReadMultiForRubricTask(listener).execute();
	}

	@Override
	public Collection<T> readForRubric(Rubrics rubric) {
		return getDecoratedDao().readForRubric(rubric);
	}

	@Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
