package com.xeppaka.lentareader.data.dao;

import java.util.Collection;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;

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
