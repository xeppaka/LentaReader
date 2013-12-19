package com.xeppaka.lentareader.data.dao.decorators;

import android.support.v4.util.LruCache;
import android.util.Log;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CachedNODaoDecorator<T extends NewsObject> extends CachedDaoDecorator<T> implements NODao<T> {
	protected NODao<T> decoratedDao;
	
	public CachedNODaoDecorator(NODao<T> decoratedDao, LruCache<Long, T> cacheId) {
		super(decoratedDao, cacheId);
		
		this.decoratedDao = decoratedDao;
	}

	@Override
	public List<T> readForRubric(Rubrics rubric) {
        List<T> dbResult = getDecoratedDao().readForRubric(rubric);
		
		if (dbResult.isEmpty()) {
			return dbResult;
		}

        List<T> result = new ArrayList<T>(dbResult.size());
		
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
    public List<T> read() {
        List<T> result = super.read();
        Collections.sort(result);

        return result;
    }

    @Override
    public boolean hasImage(long id) {
        return getDecoratedDao().hasImage(id);
    }

    @Override
    public boolean hasImage(String key) {
        return getDecoratedDao().hasImage(key);
    }

    @Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
