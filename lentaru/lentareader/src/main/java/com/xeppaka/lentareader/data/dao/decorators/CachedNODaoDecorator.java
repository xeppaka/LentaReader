package com.xeppaka.lentareader.data.dao.decorators;

import android.support.v4.util.LruCache;
import android.util.Log;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CachedNODaoDecorator<T extends NewsObject> extends CachedDaoDecorator<T> implements NODao<T> {
	protected NODao<T> decoratedDao;
	
	public CachedNODaoDecorator(NODao<T> decoratedDao, LruCache<Long, T> cacheId) {
		super(decoratedDao, cacheId);
		
		this.decoratedDao = decoratedDao;
	}

	@Override
	public List<T> read(Rubrics rubric) {
        List<T> dbResult = getDecoratedDao().read(rubric);
		
		if (dbResult.isEmpty()) {
			return dbResult;
		}

        List<T> result = new ArrayList<T>(dbResult.size());
		
		for (T object : dbResult) {
			T cachedObject = getLruCacheId().get(object.getId());			
			
			if (cachedObject != null) {
				Log.d(LentaConstants.LoggerServiceTag, "read: found object in cache with id " + object.getId());
				result.add(cachedObject);
			} else {
				Log.d(LentaConstants.LoggerServiceTag, "read: not found object in cache with id " + object.getId());
				getLruCacheId().put(object.getId(), object);
				result.add(object);
			}
		}
		
		return result;
	}

    @Override
    public T readLatest(Rubrics rubric) {
        T result = getDecoratedDao().readLatest(rubric);

        if (result != null) {
            T cached = getLruCacheId().get(result.getId());

            if (cached == null) {
                getLruCacheId().put(result.getId(), result);
            }
        }

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
    public T readLatestWOImage(Rubrics rubric, int limit) {
        return getDecoratedDao().readLatestWOImage(rubric, limit);
    }

    @Override
    public int clearLatestFlag(Rubrics rubric) {
        final int result = getDecoratedDao().clearLatestFlag(rubric);

        Map<Long, T> cacheSnapshot = getLruCacheId().snapshot();
        for (T newsObject : cacheSnapshot.values()) {
            T n = getLruCacheId().get(newsObject.getId());
            if (n != null && ((rubric == Rubrics.LATEST) || (rubric != Rubrics.LATEST && n.getRubric() == rubric))) {
                n.setLatest(false);
            }
        }

        return result;
    }

    @Override
    public int setLatestFlag(Rubrics rubric) {
        final int result = getDecoratedDao().setLatestFlag(rubric);

        Map<Long, T> cacheSnapshot = getLruCacheId().snapshot();
        for (T newsObject : cacheSnapshot.values()) {
            T n = getLruCacheId().get(newsObject.getId());
            if (n != null && ((rubric == Rubrics.LATEST) || (rubric != Rubrics.LATEST && n.getRubric() == rubric))) {
                n.setLatest(true);
            }
        }

        return result;
    }

    @Override
    public int deleteOlderOrEqual(Rubrics rubric, long date) {
        final int result = getDecoratedDao().deleteOlderOrEqual(rubric, date);

        final LruCache<Long, T> cache = getLruCacheId();
        final Map<Long, T> cacheSnapshot = cache.snapshot();
        for (T newsObject : cacheSnapshot.values()) {
            T n = cache.get(newsObject.getId());
            if (n != null && ((rubric == Rubrics.LATEST && n.getPubDate().getTime() <= date) || (rubric != Rubrics.LATEST && n.getRubric() == rubric && n.getPubDate().getTime() <= date))) {
                cache.remove(newsObject.getId());
            }
        }

        return result;
    }

    @Override
	protected NODao<T> getDecoratedDao() {
		return decoratedDao;
	}
}
