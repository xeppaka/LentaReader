package com.xeppaka.lentareader.data.dao.async;

import android.os.AsyncTask;

import com.xeppaka.lentareader.data.dao.Dao;

import java.util.Collection;
import java.util.List;

/**
 * Dao interface with extended asynchronous methods for CRUD operations.
 * 
 * @author nnm
 *
 */
public interface AsyncDao<T> extends Dao<T> {
	/**
	 * Listener of creating one object in database.
	 * 
	 * @author nnm
	 *
	 * @param <T>
	 */
	public interface DaoCreateSingleListener<T> {	
		void finished(Long result);
	}

	/**
	 * Listener of creating multiple objects in database.
	 * 
	 * @author nnm
	 *
	 * @param <T>
	 */
	public interface DaoCreateMultiListener<T> {	
		void finished(Collection<Long> result);
	}
	
	/**
	 * Listener of reading single object from database.
	 * 
	 * @author nnm
	 *
	 * @param <T>
	 */
	public interface DaoReadSingleListener<T> {	
		void finished(T result);
	}
	
	/**
	 * Listener of reading multiple objects from database.
	 * 
	 * @author nnm
	 *
	 * @param <T>
	 */
	public interface DaoReadMultiListener<T> {	
		void finished(List<T> result);
	}

	/**
	 * Listener of deleting from database.
	 *
	 * @author nnm
	 */
	public interface DaoDeleteListener {
		void finished(int rowsDeleted);
	}

	/**
	 * Listener of updating objects in database.
	 * 
	 * @author nnm
	 */
	public interface DaoUpdateListener {	
		void finished(int rowsUpdated);
	}
	
	/**
	 * Create new object in the database (row in the table).
	 * 
	 * @param dataObject
	 *            is the news object to create.
	 * @param listener
	 *            is a listener for result.
	 * @return database id for the newly created object.
	 */
	AsyncTask<T, Void, Long> createAsync(T dataObject, DaoCreateSingleListener<T> listener);

	/**
	 * Create new objects in the database (rows in the table).
	 * 
	 * @param dataObjects
	 *            is the news objects to create.
	 * @param listener
	 *            is a listener for results.
	 * @return database ids for the newly created object in the same order as
	 *         objects were.
	 */
    AsyncTask<List<T>, Void, List<Long>> createAsync(List<T> dataObjects, DaoCreateMultiListener<T> listener);
	
	/**
	 * Read all news objects from database.
	 * 
	 * @param listener
	 *            is a listener for result.
	 * @return Collection of the news objects. Null if error occurred while reading
	 *         data from the database.
	 */
    AsyncTask<List<Long>, Void, List<T>> readAsync(DaoReadMultiListener<T> listener);
	
	/**
	 * Read some news object from the database specifying its id.
	 * 
	 * @param id
	 * @param listener
	 *            is a listener for result.
	 * @return News object created from the database. Null if object is not
	 *         found.
	 */
    AsyncTask<Long, Void, T> readAsync(long id, DaoReadSingleListener<T> listener);
	
	/**
	 * Read some news objects from the database specifying its collection of
	 * ids.
	 * 
	 * @param ids
	 * @param listener
	 *            is a listener for result.
	 * @return Collection of News objects created from the database. Not null.
	 *         Could be empty.
	 */
    AsyncTask<List<Long>, Void, List<T>> readAsync(List<Long> ids, DaoReadMultiListener<T> listener);
	
	/**
	 * Update object's information in database.
	 * 
	 * @param dataObject
	 *            is the object which should be updated in the database.
	 * 
	 *            NOTE: all fields of the object are updates, thus newsObject
	 *            argument must contain all fields filled with a proper data.
	 * @param listener
	 *            is a listener for result.
	 */
    AsyncTask<T, Void, Integer> updateAsync(T dataObject, DaoUpdateListener listener);

	/**
	 * Deletes object from the database.
	 * 
	 * @param id
	 * @param listener
	 *            is a listener for result.
	 * @return async task which makes deletion.
	 */
    AsyncTask<Long, Void, Integer> deleteAsync(long id, DaoDeleteListener listener);

    /**
     * Deletes all objects from the database.
     *
     * @param listener
     *            is a listener for result.
     * @return async task which makes deletion.
     */
    AsyncTask<Void, Void, Integer> deleteAsync(DaoDeleteListener listener);
}
