package cz.fit.lentaruand.data.dao.newsobject;

import java.util.Collection;

import android.support.v4.util.LruCache;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.CachedDao;

public class CachedNewsObjectDao<T extends NewsObject> extends CachedDao<T> implements NewsObjectDao<T> {
	protected NewsObjectDao<T> underlinedDao;
	
	public CachedNewsObjectDao(NewsObjectDao<T> underlinedDao, LruCache<Long, T> cacheId) {
		super(underlinedDao, cacheId);
		
		this.underlinedDao = underlinedDao;
	}

	@Override
	public Collection<T> readForRubric(Rubrics rubric) {
		Collection<T> result = getUnderlinedDao().readForRubric(rubric);
		
		for (T object : result) {
			getLruCacheId().put(object.getId(), object);
		}
		
		return result;
	}

	@Override
	protected NewsObjectDao<T> getUnderlinedDao() {
		return underlinedDao;
	}
}
