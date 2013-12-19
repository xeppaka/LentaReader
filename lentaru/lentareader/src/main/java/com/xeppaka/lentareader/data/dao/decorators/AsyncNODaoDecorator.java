package com.xeppaka.lentareader.data.dao.decorators;

import android.os.AsyncTask;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;

import java.util.List;

public class AsyncNODaoDecorator<T extends NewsObject> extends AsyncDaoDecorator<T> implements AsyncNODao<T>, NODao<T> {
	private NODao<T> decoratedDao;
	
	protected class AsyncReadMultiForRubricTask extends AsyncTask<Rubrics, Void, List<T>> {
		private AsyncDao.DaoReadMultiListener<T> listener;
		
		public AsyncReadMultiForRubricTask(AsyncDao.DaoReadMultiListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected List<T> doInBackground(Rubrics... rubric) {
			return readForRubric(rubric[0]);
		}

		@Override
		protected void onPostExecute(List<T> result) {
			listener.finished(result);
		}
	}

	public AsyncNODaoDecorator(NODao<T> decoratedDao) {
		super(decoratedDao);
		
		this.decoratedDao = decoratedDao;
	}

    @Override
    public boolean hasImage(long id) {
        return getDecoratedDao().hasImage(id);
    }

    @Override
    public boolean hasImage(String key) {
        return getDecoratedDao().hasImage(key);
    }

    @Override
	public void readForRubricAsync(Rubrics rubric,
			com.xeppaka.lentareader.data.dao.async.AsyncDao.DaoReadMultiListener<T> listener) {
		new AsyncReadMultiForRubricTask(listener).execute();
	}

	@Override
	public List<T> readForRubric(Rubrics rubric) {
		return getDecoratedDao().readForRubric(rubric);
	}

	@Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
