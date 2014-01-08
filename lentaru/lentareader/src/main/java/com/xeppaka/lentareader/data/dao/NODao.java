package com.xeppaka.lentareader.data.dao;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;

import java.util.List;

/**
 * DAO interface that should implement every DAO implementation that represents
 * News, Articles, etc.
 * 
 * @author nnm
 * 
 * @param <T>
 */
public interface NODao<T extends NewsObject> extends Dao<T> {
    List<T> read(Rubrics rubric);
    T readLatest(Rubrics rubric);
    T readLatestWOImage(Rubrics rubric, int limit);
    int deleteOlderOrEqual(Rubrics rubric, long date);
    int clearLatestFlag(Rubrics rubric);
    int setLatestFlag(Rubrics rubric);
    boolean hasImage(long id);
    boolean hasImage(String key);
}
