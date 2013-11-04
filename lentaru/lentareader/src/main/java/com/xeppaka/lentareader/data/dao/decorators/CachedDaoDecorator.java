package com.xeppaka.lentareader.data.dao.decorators;

import java.util.ArrayList;
import java.util.Collection;

import android.support.v4.util.LruCache;
import com.xeppaka.lentareader.data.DatabaseObject;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.db.SQLiteType;

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
	public Collection<Long> create(Collection<T> dataObjects) {
		Collection<Long> result = getDecoratedDao().create(dataObjects);
		
		for (T dataObject : dataObjects) {
			cacheId.put(dataObject.getId(), dataObject);
		}
		
		return result;
	}

	@Override
	public Collection<T> read() {
		Collection<Long> allIds = getDecoratedDao().readAllIds();
		Collection<T> result = new ArrayList<T>(allIds.size());
		Collection<Long> missed = new ArrayList<Long>();
		
		for (Long id : allIds) {
			T newsObject = cacheId.get(id);
			
			if (newsObject == null) {
				missed.add(id);
			} else {
				result.add(newsObject);
			}
		}
		
		Collection<T> dbResult = getDecoratedDao().read(missed);
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
	public Collection<T> read(Collection<Long> ids) {
		Collection<Long> missed = new ArrayList<Long>();
		Collection<T> result = new ArrayList<T>(ids.size());
		
		for (Long id : ids) {
			T dataObject = getLruCacheId().get(id);
			
			if (dataObject == null) {
				missed.add(id);
			} else {
				result.add(dataObject);
			}
		}
		
		if (!missed.isEmpty()) {
			Collection<T> dbResult = getDecoratedDao().read(missed);
			
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
	public Collection<T> readForParentObject(long parentId) {
		Collection<T> dbResult = getDecoratedDao().readForParentObject(parentId);
		
		if (dbResult.isEmpty()) {
			return dbResult;
		}
		
		Collection<T> result = new ArrayList<T>(dbResult.size());
		
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
	public Collection<Long> readAllIds() {
		return getDecoratedDao().readAllIds();
	}

	@Override
	public Collection<String> readAllKeys() {
		return getDecoratedDao().readAllKeys();
	}
}
