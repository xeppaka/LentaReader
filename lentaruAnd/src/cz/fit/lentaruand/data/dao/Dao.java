package cz.fit.lentaruand.data.dao;

import java.util.Collection;

import android.content.ContentResolver;
import cz.fit.lentaruand.data.db.SQLiteType;

/**
 * DAO object by default must support CRUD operations.
 * 
 * @author PK
 * 
 * @param <T>
 *            is any class which can be saved into the database.
 */
public interface Dao<T> {
	
	interface Observer<T> {	
		void onDataChanged(boolean selfChange, T dataObject);
		void onDataChanged(boolean selfChange, Collection<T> dataObjects);
	}
	
	void registerContentObserver(Observer<T> observer);
	void unregisterContentObserver(Observer<T> observer);
	
	/**
	 * Create new object in the database (row in the table).
	 * 
	 * @param dataObject
	 *            is the news object to create.
	 * @return database id for the newly created object.
	 */
	long create(T dataObject);

	/**
	 * Create new objects in the database (rows in the table).
	 * 
	 * @param dataObjects
	 *            is the news objects to create.
	 * @return database ids for the newly created object in the same order as
	 *         objects were.
	 */
	Collection<Long> create(Collection<T> dataObjects);
	
	/**
	 * Read all news objects from database.
	 * 
	 * @return Collection of the news objects. Null if error occurred while reading
	 *         data from the database.
	 */
	Collection<T> read();
	
	/**
	 * Read some news object from the database specifying its id.
	 * 
	 * @param id
	 *            id the id returned by
	 *            {@link ContentResolver#create(android.database.sqlite.ContentResolver.CursorFactory)}
	 *            method. Only one object can be identified by id.
	 * @return News object created from the database. Null if object is not
	 *         found.
	 */
	T read(long id);
	
	/**
	 * Read some news object from the database specifying its key.
	 * 
	 * @param key
	 *            is the key of the object to read. If more than one object is
	 *            identified by a key, first one should be returned (and
	 *            additional info is written to log probably).
	 * @return News object created from the database. Null if object is not
	 *         found.
	 */
	T read(String key);

	/**
	 * Read some news object from the database specifying its key.
	 * 
	 * @param keyType
	 *            is the SQLite type of the key.
	 * @param keyColumnName
	 * 			  is key column name.
	 * @param keyValue
	 *            is the key of the object to read. If more than one object is
	 *            identified by a key, first one should be returned (and
	 *            additional info is written to log probably).
	 * @return News object created from the database. Null if object is not
	 *         found.
	 */
	T read(SQLiteType keyType, String keyColumnName, String keyValue);
	
	/**
	 * Update object's information in database.
	 * 
	 * @param dataObject
	 *            is the object which should be updated in the database.
	 * 
	 *            NOTE: all fields of the object are updates, thus newsObject
	 *            argument must contain all fields filled with a proper data.
	 */
	void update(T dataObject);

	/**
	 * Deletes object from the database.
	 * 
	 * @param id
	 *            id the id returned by
	 *            {@link ContentResolver#create(android.database.sqlite.ContentResolver.CursorFactory)}
	 *            method. Only one object can be identified by id.
	 * @return number of rows deleted.
	 */
	int delete(long id);
	
	/**
	 * Deletes object from the database.
	 * 
	 * @param key
	 *            is a key of the object to delete (should unambiguously
	 *            identify object).
	 * @return number of rows deleted.
	 */
	int delete(String key);

	/**
	 * Deletes object from the database.
	 * 
	 * @param keyType
	 *            is the SQLite type of the key.
	 * @param keyColumnName
	 * 			  is key column name.
	 * @param keyValue
	 *            is the key of the object to read. If more than one object is
	 *            identified by a key, first one should be returned (and
	 *            additional info is written to log probably).
	 * @return number of rows deleted.
	 */
	int delete(SQLiteType keyType, String keyColumnName, String keyValue);
	
	/**
	 * Reads all object keys from database.
	 * 
	 * @return Collection of keys. Not null. Could be empty.
	 */
	Collection<String> readAllKeys();
}
