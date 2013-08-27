package cz.fit.lentaruand.data.dao.decorators;

import java.util.Collection;

import cz.fit.lentaruand.data.DatabaseObject;
import cz.fit.lentaruand.data.dao.Dao;
import cz.fit.lentaruand.data.db.SQLiteType;

public class SynchronizedDaoDecorator<T extends DatabaseObject> implements Dao<T> {
	private Dao<T> decoratedDao;
	private Object sync;

	public SynchronizedDaoDecorator(Dao<T> decoratedDao, Object sync) {
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
			return getDecoratedDao().create(dataObject);
		}
	}

	@Override
	public Collection<Long> create(Collection<T> dataObjects) {
		synchronized (sync) {
			return getDecoratedDao().create(dataObjects);
		}
	}

	@Override
	public Collection<T> read() {
		synchronized (sync) {
			return getDecoratedDao().read();
		}
	}

	@Override
	public T read(long id) {
		synchronized (sync) {
			return getDecoratedDao().read(id);
		}
	}

	@Override
	public Collection<T> read(Collection<Long> ids) {
		synchronized (sync) {
			return getDecoratedDao().read(ids);
		}
	}

	@Override
	public T read(String key) {
		synchronized (sync) {
			return getDecoratedDao().read(key);
		}
	}

	@Override
	public T read(SQLiteType keyType, String keyColumnName, String keyValue) {
		synchronized (sync) {
			return getDecoratedDao().read(keyType, keyColumnName, keyValue);
		}
	}

	@Override
	public boolean exist(long id) {
		synchronized (sync) {
			return getDecoratedDao().exist(id);
		}
	}

	@Override
	public boolean exist(String key) {
		synchronized (sync) {
			return getDecoratedDao().exist(key);
		}
	}

	@Override
	public int update(T dataObject) {
		synchronized (sync) {
			return getDecoratedDao().update(dataObject);
		}
	}

	@Override
	public int delete(long id) {
		synchronized (sync) {
			return getDecoratedDao().delete(id);
		}
	}

	@Override
	public int delete(String key) {
		synchronized (sync) {
			return getDecoratedDao().delete(key);
		}
	}

	@Override
	public int delete(SQLiteType keyType, String keyColumnName, String keyValue) {
		synchronized (sync) {
			return getDecoratedDao().delete(keyType, keyColumnName, keyValue);
		}
	}

	@Override
	public Collection<Long> readAllIds() {
		synchronized (sync) {
			return getDecoratedDao().readAllIds();
		}
	}

	@Override
	public Collection<String> readAllKeys() {
		synchronized (sync) {
			return getDecoratedDao().readAllKeys();
		}
	}

	@Override
	public Collection<T> readForParentObject(long parentId) {
		synchronized (sync) {
			return getDecoratedDao().readForParentObject(parentId);
		}
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
	
	protected Dao<T> getDecoratedDao() {
		return decoratedDao;
	}
	
	protected Object getSync() {
		return sync;
	}
}
