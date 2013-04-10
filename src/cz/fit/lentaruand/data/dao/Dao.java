package cz.fit.lentaruand.data.dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * DAO object by default must support CRUD operations.
 * 
 * @author PK
 *
 * @param <T> is any class which can be saved into the database.
 */
public interface Dao<T> {
	long create(SQLiteDatabase db, T newsObject);
	T read(SQLiteDatabase db, String key);
	void update(SQLiteDatabase db, T newsObject);
	void delete(SQLiteDatabase db, String key);
}
