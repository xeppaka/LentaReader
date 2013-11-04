package com.xeppaka.lentareader.data.dao.decorators;

import java.util.ArrayList;
import java.util.Collection;

import android.support.v4.util.LruCache;
import android.util.Log;
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.utils.LentaConstants;

public class CachedNODaoDecorator<T extends NewsObject> extends CachedDaoDecorator<T> implements NODao<T> {
	protected NODao<T> decoratedDao;
	
	public CachedNODaoDecorator(NODao<T> decoratedDao, LruCache<Long, T> cacheId) {
		super(decoratedDao, cacheId);
		
		this.decoratedDao = decoratedDao;
	}

	@Override
	public Collection<T> readForRubric(Rubrics rubric) {
		Collection<T> dbResult = getDecoratedDao().readForRubric(rubric);
		
		if (dbResult.isEmpty()) {
			return dbResult;
		}
		
		Collection<T> result = new ArrayList<T>(dbResult.size());
		
		for (T object : dbResult) {
			T cachedObject = getLruCacheId().get(object.getId());			
			
			if (cachedObject != null) {
				Log.d(LentaConstants.LoggerServiceTag, "readForRubric: found object in cache with id " + object.getId());
				result.add(cachedObject);
			} else {
				Log.d(LentaConstants.LoggerServiceTag, "readForRubric: not found object in cache with id " + object.getId());
				getLruCacheId().put(object.getId(), object);
				result.add(object);
			}
		}
		
		return result;
	}

	@Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
