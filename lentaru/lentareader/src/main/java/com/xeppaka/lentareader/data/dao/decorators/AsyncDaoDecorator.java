package com.xeppaka.lentareader.data.dao.decorators;

import android.os.AsyncTask;

import com.xeppaka.lentareader.data.DatabaseObject;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.db.SQLiteType;

import java.util.List;

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
	
	protected class AsyncCreateMultiTask extends AsyncTask<List<T>, Void, List<Long>> {
		private AsyncDao.DaoCreateMultiListener<T> listener;
		
		public AsyncCreateMultiTask(AsyncDao.DaoCreateMultiListener<T> listener) {
			this.listener = listener;
		}

		@Override
		protected List<Long> doInBackground(List<T>... dataObjects) {
			return create(dataObjects[0]);
		}

		@Override
		protected void onPostExecute(List<Long> result) {
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

    protected class AsyncReadMultiTask extends AsyncTask<List<Long>, Void, List<T>> {
        private AsyncDao.DaoReadMultiListener<T> listener;

        public AsyncReadMultiTask(AsyncDao.DaoReadMultiListener<T> listener) {
            this.listener = listener;
        }

        @Override
        protected List<T> doInBackground(List<Long>... ids) {
            if (ids.length > 0) {
                return read(ids[0]);
            } else {
                return read();
            }
        }

        @Override
        protected void onPostExecute(List<T> result) {
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

    protected class AsyncDeleteAllTask extends AsyncTask<Void, Void, Integer> {
        private AsyncDao.DaoDeleteListener listener;

        public AsyncDeleteAllTask(AsyncDao.DaoDeleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return delete();
        }

        @Override
        protected void onPostExecute(Integer result) {
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
	public AsyncTask<T, Void, Long> createAsync(T dataObject, AsyncDao.DaoCreateSingleListener<T> listener) {
		return new AsyncCreateSingleTask(listener).execute(dataObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AsyncTask<List<T>, Void, List<Long>> createAsync(List<T> dataObjects, AsyncDao.DaoCreateMultiListener<T> listener) {
		return new AsyncCreateMultiTask(listener).execute(dataObjects);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AsyncTask<List<Long>, Void, List<T>> readAsync(AsyncDao.DaoReadMultiListener<T> listener) {
		return new AsyncReadMultiTask(listener).execute();
	}

	@Override
	public AsyncTask<Long, Void, T> readAsync(long id, AsyncDao.DaoReadSingleListener<T> listener) {
		return new AsyncReadSingleTask(listener).execute(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AsyncTask<List<Long>, Void, List<T>> readAsync(List<Long> ids, AsyncDao.DaoReadMultiListener<T> listener) {
		return new AsyncReadMultiTask(listener).execute(ids);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AsyncTask<T, Void, Integer> updateAsync(T dataObject, AsyncDao.DaoUpdateListener listener) {
		return new AsyncUpdateSingleTask(listener).execute(dataObject);
	}

	@Override
	public AsyncTask<Long, Void, Integer> deleteAsync(long id, AsyncDao.DaoDeleteListener listener) {
		return new AsyncDeleteSingleTask(listener).execute(id);
	}

    @Override
    public AsyncTask<Void, Void, Integer> deleteAsync(DaoDeleteListener listener) {
        return new AsyncDeleteAllTask(listener).execute();
    }

    protected Dao<T> getDecoratedDao() {
		return decoratedDao;
	}

    @Override
    public void registerContentObserver(Observer<T> observer) {
        decoratedDao.registerContentObserver(observer);
    }

    @Override
    public void unregisterContentObserver(Observer<T> observer) {
        decoratedDao.unregisterContentObserver(observer);
    }

	@Override
	public long create(T dataObject) {
		return getDecoratedDao().create(dataObject);
	}

	@Override
	public List<Long> create(List<T> dataObjects) {
		return getDecoratedDao().create(dataObjects);
	}

	@Override
	public List<T> read() {
		return getDecoratedDao().read();
	}

	@Override
	public T read(long id) {
		return getDecoratedDao().read(id);
	}

	@Override
	public List<T> read(List<Long> ids) {
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
    public int delete() {
        return getDecoratedDao().delete();
    }

    @Override
	public List<Long> readAllIds() {
		return getDecoratedDao().readAllIds();
	}

	@Override
	public List<String> readAllKeys() {
		return getDecoratedDao().readAllKeys();
	}

	@Override
	public List<T> readForParentObject(long parentId) {
		return getDecoratedDao().readForParentObject(parentId);
	}
}
