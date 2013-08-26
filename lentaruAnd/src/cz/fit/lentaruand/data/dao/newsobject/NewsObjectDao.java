package cz.fit.lentaruand.data.dao.newsobject;

import java.util.Collection;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.Dao;

/**
 * DAO interface that should implement every DAO implementation that represents
 * News, Articles, etc.
 * 
 * @author nnm
 * 
 * @param <T>
 */
public interface NewsObjectDao<T extends NewsObject> extends Dao<T> {
	Collection<T> readForRubric(Rubrics rubric);
}
