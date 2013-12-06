package com.xeppaka.lentareader.data.dao.decorators;

import android.support.v4.util.LruCache;

import com.xeppaka.lentareader.data.DatabaseObject;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.db.SQLiteType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a decorator for the Dao which adds caching abilities into it.
 * 
 * @author nnm
 * 
 * @param <T> is the database object for Dao.
 */
public class CachedDaoDecorator<T extends DatabaseObject> implements Dao<T> {
	private final Dao<T> decoratedDao;
	private final LruCache<Long, T> cacheId;
	
	protected Dao<T> getDecoratedDao() {
		return decoratedDao;
	}
	
	protected LruCache<Long, T> getLruCacheId() {
		return cacheId;
	}
	
	public CachedDaoDecorator(Dao<T> underlinedDao, LruCache<Long, T> cacheId) {
		if (underlinedDao == null) {
			throw new IllegalArgumentException("underlinedDao is null.");
		}
		
		if (cacheId == null) {
			throw new IllegalArgumentException("cacheId is null.");
		}
		
		this.decoratedDao = underlinedDao;
		this.cacheId = cacheId;
	}

	@Override
	public void registerContentObserver(Dao.Observer<T> observer) {
		getDecoratedDao().registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(Dao.Observer<T> observer) {
		getDecoratedDao().unregisterContentObserver(observer);
	}

	@Override
	public synchronized long create(T dataObject) {
		long newId = getDecoratedDao().create(dataObject);
		
		getLruCacheId().put(newId, dataObject);
		
		return newId;
	}

	@Override
	public List<Long> create(List<T> dataObjects) {
        List<Long> result = getDecoratedDao().create(dataObjects);
		
		for (T dataObject : dataObjects) {
			cacheId.put(dataObject.getId(), dataObject);
		}
		
		return result;
	}

	@Override
	public List<T> read() {
        List<Long> allIds = getDecoratedDao().readAllIds();
        List<Long> missed = new ArrayList<Long>();
        List<T> result = new ArrayList<T>(allIds.size());

		for (Long id : allIds) {
			T newsObject = cacheId.get(id);
			
			if (newsObject == null) {
				missed.add(id);
			} else {
				result.add(newsObject);
			}
		}

        List<T> dbResult = getDecoratedDao().read(missed);
		result.addAll(dbResult);
		
		for (T object : dbResult) {
			getLruCacheId().put(object.getId(), object);
		}

		return result;
	}

	@Override
	public T read(long id) {
		T dataObject = getLruCacheId().get(id);
		
		if (dataObject != null) {
			return dataObject;
		}
		
		return getDecoratedDao().read(id);
	}

	@Override
	public List<T> read(List<Long> ids) {
        List<Long> missed = new ArrayList<Long>();
        List<T> result = new ArrayList<T>(ids.size());
		
		for (Long id : ids) {
			T dataObject = getLruCacheId().get(id);
			
			if (dataObject == null) {
				missed.add(id);
			} else {
				result.add(dataObject);
			}
		}
		
		if (!missed.isEmpty()) {
            List<T> dbResult = getDecoratedDao().read(missed);
			
			result.addAll(dbResult);
			
			for (T object : dbResult) {
				getLruCacheId().put(object.getId(), object);
			}
		}
		
		return result;
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
		if (getLruCacheId().get(id) != null) {
			return true;
		}
		
		return getDecoratedDao().exist(id);
	}

	@Override
	public boolean exist(String key) {
		return getDecoratedDao().exist(key);
	}

	@Override
	public List<T> readForParentObject(long parentId) {
        List<T> dbResult = getDecoratedDao().readForParentObject(parentId);
		
		if (dbResult.isEmpty()) {
			return dbResult;
		}

        List<T> result = new ArrayList<T>(dbResult.size());
		
		for (T object : dbResult) {
			T cachedObject = getLruCacheId().get(object.getId());
			
			if (cachedObject != null) {
				result.add(cachedObject);
			} else {
				result.add(object);
				getLruCacheId().put(object.getId(), object);
			}
		}
		
		return result;
	}

	@Override
	public int update(T dataObject) {
		getLruCacheId().put(dataObject.getId(), dataObject);
		
		return getDecoratedDao().update(dataObject);
	}

	@Override
	public int delete(long id) {
		getLruCacheId().remove(id);
		
		return getDecoratedDao().delete(id);
	}

	@Override
	public int delete(String key) {
		throw new UnsupportedOperationException("This operation is very slow by using key. Don't use it or implement if cannot work without it.");
	}

	@Override
	public int delete(SQLiteType keyType, String keyColumnName, String keyValue) {
		return getDecoratedDao().delete(keyType, keyColumnName, keyValue);
	}

	@Override
	public List<Long> readAllIds() {
		return getDecoratedDao().readAllIds();
	}

	@Override
	public List<String> readAllKeys() {
		return getDecoratedDao().readAllKeys();
	}
}
