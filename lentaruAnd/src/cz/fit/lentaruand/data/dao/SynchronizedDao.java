package cz.fit.lentaruand.data.dao;

import java.util.Collection;

import cz.fit.lentaruand.data.DatabaseObject;
import cz.fit.lentaruand.data.db.SQLiteType;

public class SynchronizedDao<T extends DatabaseObject> implements AsyncDao<T> {
	private AsyncDao<T> decoratedDao;
	private Object sync;

	public SynchronizedDao(AsyncDao<T> decoratedDao, Object sync) {
		if (decoratedDao == null) {
			throw new NullPointerException("decoratedDao is null.");
		}

		if (sync == null) {
			throw new NullPointerException("sync is null.");
		}
		
		this.decoratedDao = decoratedDao;
		this.sync = sync;
	}

	@Override
	public long create(T dataObject) {
		synchronized (sync) {
			return decoratedDao.create(dataObject);
		}
	}

	@Override
	public Collection<Long> create(Collection<T> dataObjects) {
		synchronized (sync) {
			return decoratedDao.create(dataObjects);
		}
	}

	@Override
	public Collection<T> read() {
		synchronized (sync) {
			return decoratedDao.read();
		}
	}

	@Override
	public T read(long id) {
		synchronized (sync) {
			return decoratedDao.read(id);
		}
	}

	@Override
	public Collection<T> read(Collection<Long> ids) {
		synchronized (sync) {
			return decoratedDao.read(ids);
		}
	}

	@Override
	public T read(String key) {
		synchronized (sync) {
			return decoratedDao.read(key);
		}
	}

	@Override
	public T read(SQLiteType keyType, String keyColumnName, String keyValue) {
		synchronized (sync) {
			return decoratedDao.read(keyType, keyColumnName, keyValue);
		}
	}

	@Override
	public boolean exist(long id) {
		synchronized (sync) {
			return decoratedDao.exist(id);
		}
	}

	@Override
	public boolean exist(String key) {
		synchronized (sync) {
			return decoratedDao.exist(key);
		}
	}

	@Override
	public int update(T dataObject) {
		synchronized (sync) {
			return decoratedDao.update(dataObject);
		}
	}

	@Override
	public int delete(long id) {
		synchronized (sync) {
			return decoratedDao.delete(id);
		}
	}

	@Override
	public int delete(String key) {
		synchronized (sync) {
			return decoratedDao.delete(key);
		}
	}

	@Override
	public int delete(SQLiteType keyType, String keyColumnName, String keyValue) {
		synchronized (sync) {
			return decoratedDao.delete(keyType, keyColumnName, keyValue);
		}
	}

	@Override
	public Collection<Long> readAllIds() {
		synchronized (sync) {
			return decoratedDao.readAllIds();
		}
	}

	@Override
	public Collection<String> readAllKeys() {
		synchronized (sync) {
			return decoratedDao.readAllKeys();
		}
	}

	@Override
	public Collection<T> readForParentObject(long parentId) {
		synchronized (sync) {
			return decoratedDao.readForParentObject(parentId);
		}
	}

	@Override
	public void registerContentObserver(
			cz.fit.lentaruand.data.dao.Dao.Observer<T> observer) {
		decoratedDao.registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(
			cz.fit.lentaruand.data.dao.Dao.Observer<T> observer) {
		decoratedDao.unregisterContentObserver(observer);
	}

	@Override
	public void createAsync(
			T dataObject,
			cz.fit.lentaruand.data.dao.AsyncDao.DaoCreateSingleListener<T> listener) {
		decoratedDao.createAsync(dataObject, listener);
	}

	@Override
	public void createAsync(
			Collection<T> dataObjects,
			cz.fit.lentaruand.data.dao.AsyncDao.DaoCreateMultiListener<T> listener) {
		decoratedDao.createAsync(dataObjects, listener);
	}

	@Override
	public void readAsync(
			cz.fit.lentaruand.data.dao.AsyncDao.DaoReadMultiListener<T> listener) {
		decoratedDao.readAsync(listener);
	}

	@Override
	public void readAsync(
			long id,
			cz.fit.lentaruand.data.dao.AsyncDao.DaoReadSingleListener<T> listener) {
		decoratedDao.readAsync(id, listener);
	}

	@Override
	public void readAsync(Collection<Long> ids,
			cz.fit.lentaruand.data.dao.AsyncDao.DaoReadMultiListener<T> listener) {
		decoratedDao.readAsync(ids, listener);
	}

	@Override
	public void updateAsync(T dataObject,
			cz.fit.lentaruand.data.dao.AsyncDao.DaoUpdateListener listener) {
		decoratedDao.updateAsync(dataObject, listener);
	}

	@Override
	public void deleteAsync(long id,
			cz.fit.lentaruand.data.dao.AsyncDao.DaoDeleteListener listener) {
		decoratedDao.deleteAsync(id, listener);
	}
}
