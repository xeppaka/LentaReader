package cz.fit.lentaruand.data.dao;

import java.util.ArrayList;
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
class CachedDao<T extends DatabaseObject> implements Dao<T> {
	private final Dao<T> underlinedDao;
	private final LruCache<Long, T> cacheId;
	private final LruCache<String, T> cacheKey;
	
	/**
	 * used only in {@link CachedDao#read(Collection)} to not create temporary
	 * object for every method invocation.
	 */
	protected final Collection<Long> missed = new ArrayList<Long>();

	private Dao<T> getUnderlinedDao() {
		return underlinedDao;
	}
	
	private LruCache<Long, T> getLruCacheId() {
		return cacheId;
	}
	
	private LruCache<String, T> getLruCacheKey() {
		return cacheKey;
	}
	
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
		getUnderlinedDao().registerContentObserver(observer);
	}

	@Override
	public void unregisterContentObserver(Dao.Observer<T> observer) {
		getUnderlinedDao().unregisterContentObserver(observer);
	}

	@Override
	public synchronized long create(T dataObject) {
		long newId = getUnderlinedDao().create(dataObject);
		
		getLruCacheId().put(newId, dataObject);

		String key = dataObject.getKeyValue();
		
		if (key != null) {
			getLruCacheKey().put(key, dataObject);
		}
		
		return newId;
	}

	@Override
	public synchronized Collection<Long> create(Collection<T> dataObjects) {
		return getUnderlinedDao().create(dataObjects);
	}

	@Override
	public synchronized Collection<T> read() {
		return getUnderlinedDao().read();
	}

	@Override
	public synchronized T read(long id) {
		T dataObject = getLruCacheId().get(id);
		
		if (dataObject != null) {
			return dataObject;
		}
		
		return getUnderlinedDao().read(id);
	}

	@Override
	public synchronized Collection<T> read(Collection<Long> ids) {
		missed.clear();
		Collection<T> result = new ArrayList<T>();
		
		for (Long id : ids) {
			T dataObject = getLruCacheId().get(id);
			
			if (dataObject == null) {
				missed.add(id);
			} else {
				result.add(dataObject);
			}
		}
		
		if (!missed.isEmpty()) {
			result.addAll(getUnderlinedDao().read(missed));
		}
		
		return result;
	}

	@Override
	public synchronized T read(String key) {
		T dataObject = getLruCacheKey().get(key);
		
		if (dataObject != null) {
			return dataObject;
		}
		
		return getUnderlinedDao().read(key);
	}

	@Override
	public synchronized T read(SQLiteType keyType, String keyColumnName, String keyValue) {
		return getUnderlinedDao().read(keyType, keyColumnName, keyValue);
	}

	@Override
	public Collection<T> readForParentObject(long parentId) {
		// TODO: consider if cache can be used in that case.
		return getUnderlinedDao().readForParentObject(parentId);
	}

	@Override
	public synchronized int update(T dataObject) {
		getLruCacheId().put(dataObject.getId(), dataObject);
		
		String key = dataObject.getKeyValue();
		if (key != null) {		
			getLruCacheKey().put(key, dataObject);
		}
		
		return getUnderlinedDao().update(dataObject);
	}

	@Override
	public synchronized int delete(long id) {
		T dataObject = getLruCacheId().remove(id);
		
		if (dataObject != null) {
			String key = dataObject.getKeyValue();
			
			if (key != null) {
				getLruCacheKey().remove(key);
			}
		}
		
		return getUnderlinedDao().delete(id);
	}

	@Override
	public synchronized int delete(String key) {
		int result = getUnderlinedDao().delete(key);
		
		T dataObject = getLruCacheKey().remove(key);
		
		if (dataObject != null) {
			getLruCacheId().remove(dataObject.getId());
		}
		
		return result;
	}

	@Override
	public synchronized int delete(SQLiteType keyType, String keyColumnName, String keyValue) {
		return getUnderlinedDao().delete(keyType, keyColumnName, keyValue);
	}

	@Override
	public Collection<String> readAllKeys() {
		return getUnderlinedDao().readAllKeys();
	}
}
