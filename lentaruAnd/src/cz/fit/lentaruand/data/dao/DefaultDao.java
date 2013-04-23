package cz.fit.lentaruand.data.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cz.fit.lentaruand.data.db.NewsEntry;
import cz.fit.lentaruand.data.db.SQLiteType;

public abstract class DefaultDao<T extends DaoObject> implements Dao<T> {
	private final Logger logger = Logger.getLogger(DefaultDao.class.getName());
	private final String textKeyWhere;
	private final String intKeyWhere;
	
	private String getWhereFromSQLiteType(SQLiteType type) {
		switch (type) {
		case TEXT:
			return textKeyWhere;
		case INTEGER:
			return intKeyWhere;
		default:
			throw new IllegalArgumentException("Only TEXT and INTEGER are supported as key types in database.");
		}
	}
	
	public DefaultDao() {
			textKeyWhere = "%1s LIKE ?";
			intKeyWhere = "%1s = ?";
	}
	
	@Override
	public T read(SQLiteDatabase db, long id) {
		return read(db, SQLiteType.INTEGER, getIdColumnName(), String.valueOf(id));
	}

	@Override
	public T read(SQLiteDatabase db, String key) {
		return read(db, getKeyColumnType(), getKeyColumnName(), key);
	}
	
	@Override
	public T read(SQLiteDatabase db, SQLiteType keyType, String keyColumnName, String keyValue) {
		String[] whereArgs = { keyValue };
		String where = String.format(getWhereFromSQLiteType(keyType), keyColumnName);
		
		Cursor cur = db.query(
				getTableName(),
				getProjectionAll(),
				where,
				whereArgs, 
				null, 
				null, 
				null
				);
		
		try {
			if (cur.getCount() > 1)
				logger.log(Level.WARNING, "There are more than one dao object in the database table '" + getTableName() + "' with key = '" + keyValue + "'.");
			
			if (cur.moveToFirst())
				return createDaoObject(cur);
		} finally {
			cur.close();
		}
		
		return null;
	}
	
	@Override
	public long create(SQLiteDatabase db, T daoObject) {
		return db.insert(getTableName(), null, prepareContentValues(daoObject));
	}
	
	@Override
	public void delete(SQLiteDatabase db, long id) {
		delete(db, SQLiteType.INTEGER, getIdColumnName(), String.valueOf(id));
	}

	@Override
	public void delete(SQLiteDatabase db, String keyValue) {
		delete(db, getKeyColumnType(), getKeyColumnName(), keyValue);
	}
	
	@Override
	public void delete(SQLiteDatabase db, SQLiteType keyType,
			String keyColumnName, String keyValue) {
		String[] whereArgs = { keyValue };
		String where = String.format(getWhereFromSQLiteType(keyType), keyColumnName);
		
		db.delete(getTableName(), where, whereArgs);
	}

	public void update(SQLiteDatabase db, T daoObject) {
		long id = daoObject.getId();
		
		String[] whereArgs;
		String where;
		
		if (id != DaoObject.ID_NONE) {
			whereArgs = new String[]{ String.valueOf(daoObject.getId()) };
			where = String.format(getWhereFromSQLiteType(SQLiteType.INTEGER), getIdColumnName());
		} else {
			whereArgs = new String[]{ daoObject.getKeyValue() };
			where = String.format(getWhereFromSQLiteType(getKeyColumnType()), getKeyColumnName());
		}
		
		db.update(getTableName(), prepareContentValues(daoObject), where, whereArgs);
	}
	
	public Collection<String> readAllKeys(SQLiteDatabase db) {
		String[] projectionKeyOnly = {	getKeyColumnName() };
			
		Cursor cur = db.query(
				getTableName(), 
				projectionKeyOnly, 
				null,
				null, 
				null, 
				null, 
				null
				);
		
		try {
			Collection<String> result = new ArrayList<String>();
			
			while (cur.moveToNext()) {
				result.add(cur.getString(cur.getColumnIndexOrThrow(getKeyColumnName())));
			}

			return result;
		} finally {
			cur.close();
		}
	}
	
	protected abstract ContentValues prepareContentValues(T newsObject);
	protected abstract T createDaoObject(Cursor cur);
	protected abstract String getTableName();
	protected abstract String getKeyColumnName();
	protected abstract SQLiteType getKeyColumnType();
	protected abstract String getIdColumnName();
	protected abstract String[] getProjectionAll();
}
