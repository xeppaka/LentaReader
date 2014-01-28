package com.xeppaka.lentareader.data.dao.decorators;

import com.xeppaka.lentareader.data.DatabaseObject;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.db.SQLiteType;

import java.util.List;

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
	public List<Long> create(List<T> dataObjects) {
		synchronized (sync) {
			return getDecoratedDao().create(dataObjects);
		}
	}

	@Override
	public List<T> read() {
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
	public List<T> read(List<Long> ids) {
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
    public int delete() {
        synchronized (sync) {
            return getDecoratedDao().delete();
        }
    }

    @Override
	public List<Long> readAllIds() {
		synchronized (sync) {
			return getDecoratedDao().readAllIds();
		}
	}

	@Override
	public List<String> readAllKeys() {
		synchronized (sync) {
			return getDecoratedDao().readAllKeys();
		}
	}

	@Override
	public List<T> readForParentObject(long parentId) {
		synchronized (sync) {
			return getDecoratedDao().readForParentObject(parentId);
		}
	}

	@Override
	public void registerContentObserver(
			com.xeppaka.lentareader.data.dao.Dao.Observer<T> observer) {
		getDecoratedDao().registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(
			com.xeppaka.lentareader.data.dao.Dao.Observer<T> observer) {
		getDecoratedDao().unregisterContentObserver(observer);
	}
	
	protected Dao<T> getDecoratedDao() {
		return decoratedDao;
	}
	
	protected Object getSync() {
		return sync;
	}
}
