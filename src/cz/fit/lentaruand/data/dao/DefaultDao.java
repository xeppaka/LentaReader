package cz.fit.lentaruand.data.dao;

import java.util.ArrayList;
import java.util.Collection;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cz.fit.lentaruand.data.dao.exceptions.InconsistentDatastoreException;
import cz.fit.lentaruand.data.db.NewsEntry;

public abstract class DefaultDao<T extends DaoObject> implements Dao<T> {
	private final String keyWhere;
	
	public DefaultDao() {
		keyWhere = getKeyColumnName() + " LIKE ?";
	}
	
	@Override
	public T read(SQLiteDatabase db, String key) {
		String[] keyWhereArgs = { key };
		
		Cursor cur = db.query(
				getTableName(), 
				getProjectionAll(), 
				keyWhere,
				keyWhereArgs, 
				null, 
				null, 
				null
				);
		
		try {
			if (cur.getCount() > 1)
				throw new InconsistentDatastoreException("There are more than one dao object in the database with key = '" + key + "'.");
			
			cur.moveToFirst();
			
			return createDaoObject(cur);
		} finally {
			cur.close();
		}
	}
	
	public long create(SQLiteDatabase db, T daoObject) {
		return db.insert(getTableName(), null, prepareContentValues(daoObject));
	}

	public void delete(SQLiteDatabase db, String key) {
		String[] keyWhereArgs = { key };
		
		db.delete(getTableName(), keyWhere, keyWhereArgs);
	}
	
	public void update(SQLiteDatabase db, T daoObject) {
		String[] keyWhereArgs = { daoObject.getKeyValue() };
		
		db.update(NewsEntry.TABLE_NAME, prepareContentValues(daoObject), keyWhere, keyWhereArgs);
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
	protected abstract String[] getProjectionAll();
}
