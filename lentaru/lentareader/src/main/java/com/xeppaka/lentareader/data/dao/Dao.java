package com.xeppaka.lentareader.data.dao;

import android.database.Cursor;

import com.xeppaka.lentareader.data.db.SQLiteType;

import java.util.List;

/**
 * DAO object by default must support CRUD operations.
 * 
 * @author PK
 * 
 * @param <T>
 *            is any class which can be saved into the database.
 */
public interface Dao<T> extends DaoObservable<T> {
    public static int NO_ID = -1;

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
    List<Long> create(List<T> dataObjects);
	
	/**
	 * Read all news objects from database.
	 * 
	 * @return List of the news objects. Null if error occurred while reading
	 *         data from the database.
	 */
    List<T> read();

    /**
     * Read all news objects from database and return cursor.
     *
     * @return Cursor. It is caller's responsibility to close it.
     */
    Cursor readCursor();
	
	/**
	 * Read some news object from the database specifying its id.
	 * 
	 * @param id
	 *            unique id of the object. Returned by create method.
	 * @return News object created from the database. Null if object is not
	 *         found.
	 */
	T read(long id);

    /**
     * Read some news object from the database specifying its id.
     *
     * @param id
     *            unique id of the object. Returned by create method.
     * @return Cursor. It is caller's responsibility to check if cursor
     *                 is empty and close it.
     */
    Cursor readCursor(long id);

	/**
	 * Read some news objects from the database specifying its list of
	 * ids.
	 * 
	 * @param ids
	 *            collections of ids returned by create method.
	 * @return Collection of News objects created from the database. Not null.
	 *         Could be empty.
	 */
    List<T> read(List<Long> ids);
	
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
	 * Check whether object exists in database.
	 * 
	 * @param id is the primary key of the row.
	 * 
	 * @return true if exist, false otherwise.
	 */
	boolean exist(long id);
	
	/**
	 * Check whether object exists in database.
	 * 
	 * @param key is the unique key of the object.
	 * 
	 * @return true if exist, false otherwise.
	 */
	boolean exist(String key);
	
	/**
	 * Update object's information in database.
	 * 
	 * @param dataObject
	 *            is the object which should be updated in the database.
	 * 
	 *            NOTE: all fields of the object are updates, thus newsObject
	 *            argument must contain all fields filled with a proper data.
	 * @return number of objects updated.
	 */
	int update(T dataObject);

	/**
	 * Deletes object from the database.
	 * 
	 * @param id
	 *            unique id returned by create
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
     * Deletes all rows from the table.
     *
     * @return number of rows deleted.
     */
    int delete();

	/**
	 * Reads all object keys from database.
	 * 
	 * @return Collection of keys. Not null. Could be empty.
	 */
    List<Long> readAllIds();
	
	/**
	 * Reads all object keys from database.
	 * 
	 * @return Collection of keys. Not null. Could be empty.
	 */
    List<String> readAllKeys();
	
	/**
	 * Read all data objects that are connected to the id of the parent object
	 * (foreign key).
	 * 
	 * @param parentId
	 *            is the id of parent object.
	 * @return Collection of objects or null if there is no foreign key for this
	 *         table.
	 */
    List<T> readForParentObject(long parentId);
}
