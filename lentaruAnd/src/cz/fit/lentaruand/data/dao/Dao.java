package cz.fit.lentaruand.data.dao;

import cz.fit.lentaruand.data.db.SQLiteType;
import android.database.sqlite.SQLiteDatabase;

/**
 * DAO object by default must support CRUD operations.
 * 
 * @author PK
 * 
 * @param <T>
 *            is any class which can be saved into the database.
 */
public interface Dao<T> {
	/**
	 * Create new object in the database (row in the table).
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
	 * @param dataObject
	 *            is the news object to create.
	 * @return database id for the newly created object.
	 */
	long create(SQLiteDatabase db, T dataObject);

	/**
	 * Read some news object from the database specifying its id.
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
	 * @param id
	 *            id the id returned by
	 *            {@link SQLiteDatabase#create(android.database.sqlite.SQLiteDatabase.CursorFactory)}
	 *            method. Only one object can be identified by id.
	 * @return News object created from the database. Null if object is not
	 *         found.
	 */
	T read(SQLiteDatabase db, long id);
	
	/**
	 * Read some news object from the database specifying its key.
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
	 * @param key
	 *            is the key of the object to read. If more than one object is
	 *            identified by a key, first one should be returned (and
	 *            additional info is written to log probably).
	 * @return News object created from the database. Null if object is not
	 *         found.
	 */
	T read(SQLiteDatabase db, String key);

	/**
	 * Read some news object from the database specifying its key.
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
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
	T read(SQLiteDatabase db, SQLiteType keyType, String keyColumnName, String keyValue);
	
	/**
	 * Update object's information in database.
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
	 * @param dataObject
	 *            is the object which should be updated in the database.
	 * 
	 *            NOTE: all fields of the object are updates, thus newsObject
	 *            argument must contain all fields filled with a proper data.
	 */
	void update(SQLiteDatabase db, T dataObject);

	/**
	 * Deletes object from the database.
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
	 * @param id
	 *            id the id returned by
	 *            {@link SQLiteDatabase#create(android.database.sqlite.SQLiteDatabase.CursorFactory)}
	 *            method. Only one object can be identified by id.
	 */
	void delete(SQLiteDatabase db, long id);
	
	/**
	 * Deletes object from the database.
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
	 * @param key
	 *            is a key of the object to delete (should unambiguously
	 *            identify object).
	 */
	void delete(SQLiteDatabase db, String key);

	/**
	 * Deletes object from the database.
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
	 * @param keyType
	 *            is the SQLite type of the key.
	 * @param keyColumnName
	 * 			  is key column name.
	 * @param keyValue
	 *            is the key of the object to read. If more than one object is
	 *            identified by a key, first one should be returned (and
	 *            additional info is written to log probably).
	 */
	void delete(SQLiteDatabase db, SQLiteType keyType, String keyColumnName, String keyValue);
}
