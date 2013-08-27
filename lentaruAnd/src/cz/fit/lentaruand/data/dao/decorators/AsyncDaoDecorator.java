package cz.fit.lentaruand.data.dao.decorators;

import java.util.Collection;

import android.os.AsyncTask;
import cz.fit.lentaruand.data.DatabaseObject;
import cz.fit.lentaruand.data.dao.Dao;
import cz.fit.lentaruand.data.dao.async.AsyncDao;
import cz.fit.lentaruand.data.db.SQLiteType;

public class AsyncDaoDecorator<T extends DatabaseObject> implements AsyncDao<T> {
	private Dao<T> decoratedDao;
	
	public AsyncDaoDecorator(Dao<T> decoratedDao) {
		if (decoratedDao == null) {
			throw new NullPointerException("decoratedDao is null.");
		}
		
		this.decoratedDao = decoratedDao;
	}

	protected class AsyncCreateSingleTask extends AsyncTask<T, Void, Long> {
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
	
	protected class AsyncCreateMultiTask extends AsyncTask<Collection<T>, Void, Collection<Long>> {
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
	
	protected class AsyncReadSingleTask extends AsyncTask<Long, Void, T> {
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
	
	protected class AsyncDeleteSingleTask extends AsyncTask<Long, Void, Integer> {
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
	
	protected class AsyncReadMultiTask extends AsyncTask<Collection<Long>, Void, Collection<T>> {
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
	
	protected class AsyncUpdateSingleTask extends AsyncTask<T, Void, Integer> {
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

	protected Dao<T> getDecoratedDao() {
		return decoratedDao;
	}

	@Override
	public void registerContentObserver(
			cz.fit.lentaruand.data.dao.Dao.Observer<T> observer) {
		getDecoratedDao().registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(
			cz.fit.lentaruand.data.dao.Dao.Observer<T> observer) {
		getDecoratedDao().unregisterContentObserver(observer);
	}

	@Override
	public long create(T dataObject) {
		return getDecoratedDao().create(dataObject);
	}

	@Override
	public Collection<Long> create(Collection<T> dataObjects) {
		return getDecoratedDao().create(dataObjects);
	}

	@Override
	public Collection<T> read() {
		return getDecoratedDao().read();
	}

	@Override
	public T read(long id) {
		return getDecoratedDao().read(id);
	}

	@Override
	public Collection<T> read(Collection<Long> ids) {
		return getDecoratedDao().read(ids);
	}

	@Override
	public T read(String key) {
		return getDecoratedDao().read(key);
	}

	@Override
	public T read(SQLiteType keyType, String keyColumnName, String keyValue) {
		return getDecoratedDao().read(keyType, keyColumnName, keyValue);
	}

	@Override
	public boolean exist(long id) {
		return getDecoratedDao().exist(id);
	}

	@Override
	public boolean exist(String key) {
		return getDecoratedDao().exist(key);
	}

	@Override
	public int update(T dataObject) {
		return getDecoratedDao().update(dataObject);
	}

	@Override
	public int delete(long id) {
		return getDecoratedDao().delete(id);
	}

	@Override
	public int delete(String key) {
		return getDecoratedDao().delete(key);
	}

	@Override
	public int delete(SQLiteType keyType, String keyColumnName, String keyValue) {
		return getDecoratedDao().delete(keyType, keyColumnName, keyValue);
	}

	@Override
	public Collection<Long> readAllIds() {
		return getDecoratedDao().readAllIds();
	}

	@Override
	public Collection<String> readAllKeys() {
		return getDecoratedDao().readAllKeys();
	}

	@Override
	public Collection<T> readForParentObject(long parentId) {
		return getDecoratedDao().readForParentObject(parentId);
	}
}
