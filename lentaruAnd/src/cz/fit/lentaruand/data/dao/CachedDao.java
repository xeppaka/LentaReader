package cz.fit.lentaruand.data.dao;

import java.util.Collection;

import android.support.v4.util.LruCache;
import cz.fit.lentaruand.data.DatabaseObject;
import cz.fit.lentaruand.data.db.SQLiteType;

/**
 * This class is a decorator for the Dao which adds caching abilities into it.
 * 
 * @author nnm
 * 
 * @param <T> is the database object for Dao.
 */
final class CachedDao<T extends DatabaseObject> implements Dao<T> {
	private final Dao<T> underlinedDao;
	private final LruCache<Long, T> cacheId;
	private final LruCache<String, T> cacheKey;
	
	public CachedDao(Dao<T> underlinedDao, LruCache<Long, T> cacheId, LruCache<String, T> cacheKey) {
		if (underlinedDao == null) {
			throw new IllegalArgumentException("underlinedDao is null.");
		}
		
		if (cacheId == null) {
			throw new IllegalArgumentException("cacheId is null.");
		}
		
		if (cacheKey == null) {
			throw new IllegalArgumentException("cacheKey is null.");
		}
		
		this.underlinedDao = underlinedDao;
		this.cacheId = cacheId;
		this.cacheKey = cacheKey;
	}

	@Override
	public void registerContentObserver(Dao.Observer<T> observer) {
		underlinedDao.registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(Dao.Observer<T> observer) {
		underlinedDao.unregisterContentObserver(observer);
	}

	@Override
	public synchronized long create(T dataObject) {
		long newId = underlinedDao.create(dataObject);
		
		cacheId.put(newId, dataObject);

		String key = dataObject.getKeyValue();
		
		if (key != null) {
			cacheKey.put(key, dataObject);
		}
		
		return newId;
	}

	@Override
	public synchronized Collection<Long> create(Collection<T> dataObjects) {
		return underlinedDao.create(dataObjects);
	}

	@Override
	public synchronized Collection<T> read() {
		return underlinedDao.read();
	}

	@Override
	public synchronized T read(long id) {
		T dataObject = cacheId.get(id);
		
		if (dataObject != null) {
			return dataObject;
		}
		
		return underlinedDao.read(id);
	}

	@Override
	public synchronized T read(String key) {
		T dataObject = cacheKey.get(key);
		
		if (dataObject != null) {
			return dataObject;
		}
		
		return underlinedDao.read(key);
	}

	@Override
	public synchronized T read(SQLiteType keyType, String keyColumnName, String keyValue) {
		return underlinedDao.read(keyType, keyColumnName, keyValue);
	}

	@Override
	public synchronized void update(T dataObject) {
		underlinedDao.update(dataObject);
		
		cacheId.put(dataObject.getId(), dataObject);
		
		String key = dataObject.getKeyValue();
		if (key != null) {		
			cacheKey.put(key, dataObject);
		}
	}

	@Override
	public synchronized int delete(long id) {
		int result = underlinedDao.delete(id);
		
		T dataObject = cacheId.remove(id);
		
		if (dataObject != null) {
			String key = dataObject.getKeyValue();
			
			if (key != null) {
				cacheKey.remove(key);
			}
		}
		
		return result;
	}

	@Override
	public synchronized int delete(String key) {
		int result = underlinedDao.delete(key);
		
		T dataObject = cacheKey.remove(key);
		
		if (dataObject != null) {
			cacheId.remove(dataObject.getId());
		}
		
		return result;
	}

	@Override
	public synchronized int delete(SQLiteType keyType, String keyColumnName, String keyValue) {
		return underlinedDao.delete(keyType, keyColumnName, keyValue);
	}

	@Override
	public Collection<String> readAllKeys() {
		return underlinedDao.readAllKeys();
	}
}
