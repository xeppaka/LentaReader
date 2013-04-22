package cz.fit.lentaruand.data.dao;

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
	 * @param newsObject
	 *            is the news object to create.
	 * @return database id for the newly created object.
	 */
	long create(SQLiteDatabase db, T newsObject);

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
	 * Update object's information in database.
	 * 
	 * @param db
	 *            is the {@link SQLiteDatabase} instance.
	 * @param newsObject
	 *            is the object which should be updated in the database.
	 * 
	 *            NOTE: all fields of the object are updates, thus newsObject
	 *            argument must contain all fields filled with a proper data.
	 */
	void update(SQLiteDatabase db, T newsObject);

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
}
