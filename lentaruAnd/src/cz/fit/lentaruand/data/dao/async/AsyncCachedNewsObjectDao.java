package cz.fit.lentaruand.data.dao.async;

import java.util.Collection;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.newsobject.NewsObjectDao;

public class AsyncCachedNewsObjectDao<T extends NewsObject> extends AsyncCachedDao<T> implements AsyncNewsObjectDao<T> {
	protected NewsObjectDao<T> underlinedDao;
	
	protected class AsyncReadMultiForRubricTask extends AsyncTask<Rubrics, Void, Collection<T>> {
		private AsyncDao.DaoReadMultiListener<T> listener;
		
		public AsyncReadMultiForRubricTask(AsyncDao.DaoReadMultiListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected Collection<T> doInBackground(Rubrics... rubric) {
			return getUnderlinedDao().readForRubric(rubric[0]);
		}

		@Override
		protected void onPostExecute(Collection<T> result) {
			listener.finished(result);
		}
	}
	
	public AsyncCachedNewsObjectDao(NewsObjectDao<T> underlinedDao, LruCache<Long, T> cacheId) {
		super(underlinedDao, cacheId);
		
		this.underlinedDao = underlinedDao;
	}

	@Override
	public void readAsyncForRubric(Rubrics rubric,
			cz.fit.lentaruand.data.dao.async.AsyncDao.DaoReadMultiListener<T> listener) {
		new AsyncReadMultiForRubricTask(listener).execute();
	}

	@Override
	protected NewsObjectDao<T> getUnderlinedDao() {
		return underlinedDao;
	}
}
