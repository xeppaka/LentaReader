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
	
	public CachedDao(Dao<T> underlinedDao, LruCache<Long, T> cacheId) {
		if (underlinedDao == null) {
			throw new IllegalArgumentException("underlinedDao is null.");
		}
		
		if (cacheId == null) {
			throw new IllegalArgumentException("cacheId is null.");
		}
		
		this.underlinedDao = underlinedDao;
		this.cacheId = cacheId;
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
		
		return newId;
	}

	@Override
	public Collection<Long> create(Collection<T> dataObjects) {
		Collection<Long> result = getUnderlinedDao().create(dataObjects);
		
		for (T dataObject : dataObjects) {
			cacheId.put(dataObject.getId(), dataObject);
		}
		
		return result;
	}

	@Override
	public Collection<T> read() {
		Collection<Long> allIds = getUnderlinedDao().readAllIds();
		Collection<T> result = new ArrayList<T>();
		
		missed.clear();
		
		for (Long id : allIds) {
			T newsObject = cacheId.get(id);
			
			if (newsObject == null) {
				missed.add(id);
			} else {
				result.add(newsObject);
			}
		}
		
		result.addAll(getUnderlinedDao().read(missed));
		
		return result;
	}

	@Override
	public T read(long id) {
		T dataObject = getLruCacheId().get(id);
		
		if (dataObject != null) {
			return dataObject;
		}
		
		return getUnderlinedDao().read(id);
	}

	@Override
	public Collection<T> read(Collection<Long> ids) {
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
	public T read(String key) {
		return getUnderlinedDao().read(key);
	}

	@Override
	public T read(SQLiteType keyType, String keyColumnName, String keyValue) {
		return getUnderlinedDao().read(keyType, keyColumnName, keyValue);
	}

	@Override
	public boolean exist(long id) {
		if (getLruCacheId().get(id) != null) {
			return true;
		}
		
		return getUnderlinedDao().exist(id);
	}

	@Override
	public boolean exist(String key) {
		return getUnderlinedDao().exist(key);
	}

	@Override
	public Collection<T> readForParentObject(long parentId) {
		// TODO: consider if cache can be used in that case.
		return getUnderlinedDao().readForParentObject(parentId);
	}

	@Override
	public int update(T dataObject) {
		getLruCacheId().put(dataObject.getId(), dataObject);
		
		return getUnderlinedDao().update(dataObject);
	}

	@Override
	public int delete(long id) {
		getLruCacheId().remove(id);
		
		return getUnderlinedDao().delete(id);
	}

	@Override
	public int delete(String key) {
		throw new UnsupportedOperationException("This operation is very slow by using key. Don't use it or implement if cannot work without it.");
	}

	@Override
	public int delete(SQLiteType keyType, String keyColumnName, String keyValue) {
		return getUnderlinedDao().delete(keyType, keyColumnName, keyValue);
	}

	@Override
	public Collection<Long> readAllIds() {
		return getUnderlinedDao().readAllIds();
	}

	@Override
	public Collection<String> readAllKeys() {
		return getUnderlinedDao().readAllKeys();
	}
}
