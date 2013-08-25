package cz.fit.lentaruand.data.dao;

import java.util.Collection;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import cz.fit.lentaruand.data.DatabaseObject;


public class AsyncCachedDao<T extends DatabaseObject> extends CachedDao<T> implements AsyncDao<T> {
	public AsyncCachedDao(Dao<T> underlinedDao, LruCache<Long, T> cacheId) {
		super(underlinedDao, cacheId);
	}

	private class AsyncCreateSingleTask extends AsyncTask<T, Void, Long> {
		private AsyncDao.DaoCreateSingleListener<T> listener;
		
		public AsyncCreateSingleTask(AsyncDao.DaoCreateSingleListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected Long doInBackground(T... ids) {
			return create(ids[0]);
		}

		@Override
		protected void onPostExecute(Long result) {
			listener.finished(result);
		}
	}
	
	private class AsyncCreateMultiTask extends AsyncTask<Collection<T>, Void, Collection<Long>> {
		private AsyncDao.DaoCreateMultiListener<T> listener;
		
		public AsyncCreateMultiTask(AsyncDao.DaoCreateMultiListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected Collection<Long> doInBackground(Collection<T>... dataObjects) {
			return create(dataObjects[0]);
		}

		@Override
		protected void onPostExecute(Collection<Long> result) {
			listener.finished(result);
		}
	}
	
	private class AsyncReadSingleTask extends AsyncTask<Long, Void, T> {
		private AsyncDao.DaoReadSingleListener<T> listener;
		
		public AsyncReadSingleTask(AsyncDao.DaoReadSingleListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected T doInBackground(Long... ids) {
			return read(ids[0]);
		}

		@Override
		protected void onPostExecute(T result) {
			listener.finished(result);
		}
	}
	
	private class AsyncDeleteSingleTask extends AsyncTask<Long, Void, Integer> {
		private AsyncDao.DaoDeleteListener listener;
		
		public AsyncDeleteSingleTask(AsyncDao.DaoDeleteListener listener) {
			this.listener = listener;
		}

		@Override
		protected Integer doInBackground(Long... ids) {
			return delete(ids[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			listener.finished(result);
		}
	}
	
	private class AsyncReadMultiTask extends AsyncTask<Collection<Long>, Void, Collection<T>> {
		private AsyncDao.DaoReadMultiListener<T> listener;
		
		public AsyncReadMultiTask(AsyncDao.DaoReadMultiListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected Collection<T> doInBackground(Collection<Long>... ids) {
			if (ids.length > 0) {
				return read(ids[0]);
			} else {
				return read();
			}
		}

		@Override
		protected void onPostExecute(Collection<T> result) {
			listener.finished(result);
		}
	}
	
	private class AsyncUpdateSingleTask extends AsyncTask<T, Void, Integer> {
		private AsyncDao.DaoUpdateListener listener;
		
		public AsyncUpdateSingleTask(AsyncDao.DaoUpdateListener listener) {
			this.listener = listener;
		}

		@Override
		protected Integer doInBackground(T... dataObject) {
			return update(dataObject[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
			listener.finished(result);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized void createAsync(T dataObject, AsyncDao.DaoCreateSingleListener<T> listener) {
		new AsyncCreateSingleTask(listener).execute(dataObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void createAsync(Collection<T> dataObjects, AsyncDao.DaoCreateMultiListener<T> listener) {
		new AsyncCreateMultiTask(listener).execute(dataObjects);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void readAsync(AsyncDao.DaoReadMultiListener<T> listener) {
		new AsyncReadMultiTask(listener).execute();
	}

	@Override
	public synchronized void readAsync(long id, AsyncDao.DaoReadSingleListener<T> listener) {
		new AsyncReadSingleTask(listener).execute(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void readAsync(Collection<Long> ids, AsyncDao.DaoReadMultiListener<T> listener) {
		new AsyncReadMultiTask(listener).execute(ids);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void updateAsync(T dataObject, AsyncDao.DaoUpdateListener listener) {
		new AsyncUpdateSingleTask(listener).execute(dataObject);
	}

	@Override
	public synchronized void deleteAsync(long id, AsyncDao.DaoDeleteListener listener) {
		new AsyncDeleteSingleTask(listener).execute(id);
	}
}
