package cz.fit.lentaruand.data.dao;

import java.util.Collection;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;

/**
 * DAO interface that should implement every DAO implementation that represents
 * News, Articles, etc.
 * 
 * @author nnm
 * 
 * @param <T>
 */
public interface NODao<T extends NewsObject> extends Dao<T> {
	Collection<T> readForRubric(Rubrics rubric);
}
