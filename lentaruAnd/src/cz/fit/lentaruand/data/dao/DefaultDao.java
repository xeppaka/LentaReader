package cz.fit.lentaruand.data.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import cz.fit.lentaruand.data.DatabaseObject;
import cz.fit.lentaruand.data.db.SQLiteType;
import cz.fit.lentaruand.data.provider.LentaProvider;
import cz.fit.lentaruand.utils.LentaConstants;

public abstract class DefaultDao<T extends DatabaseObject> implements Dao<T> {
	private final static String textKeyWhere;
	private final static String intKeyWhere;
	
	private static String getWhereFromSQLiteType(SQLiteType type) {
		switch (type) {
		case TEXT:
			return textKeyWhere;
		case INTEGER:
			return intKeyWhere;
		default:
			throw new IllegalArgumentException("Only TEXT and INTEGER are supported as key types in database.");
		}
	}
	
	static {
		textKeyWhere = "%1s LIKE ?";
		intKeyWhere = "%1s = ?";
	}
	
	private ContentResolver cr;
	
	public DefaultDao(ContentResolver cr) {
		this.cr = cr;
	}

	@Override
	public Collection<T> read() {
		Cursor cur = cr.query(getContentProviderUri(),
				getProjectionAll(), null, null, null);

		try {
			List<T> result = new ArrayList<T>();

			if (cur.moveToFirst()) {
				do {
					result.add(createDaoObject(cur));
				} while (cur.moveToNext());
			}
			
			return result;
		} finally {
			cur.close();
		}		
	}

	@Override
	public T read(long id) {
		Uri uri = ContentUris.withAppendedId(getContentProviderUri(), id);
		
		Cursor cur = cr.query(
				uri,
				getProjectionAll(),
				null,
				null, 
				null
				);
		
		try {
			if (cur.getCount() > 1) {
				Log.w(LentaConstants.LoggerMainAppTag, "There are more than one data object by using uri '" + uri + "'. Will use the first one from the list.");
			}
			
			if (cur.moveToFirst())
				return createDaoObject(cur);
		} finally {
			cur.close();
		}
		
		return null;
	}

	@Override
	public T read(String key) {
		return read(getKeyColumnType(), getKeyColumnName(), key);
	}
	
	@Override
	public T read(SQLiteType keyType, String keyColumnName, String keyValue) {
		String[] whereArgs = { keyValue };
		String where = String.format(getWhereFromSQLiteType(keyType), keyColumnName);

		Cursor cur = cr.query(
				getContentProviderUri(),
				getProjectionAll(),
				where,
				whereArgs, 
				null
				);
		
		try {
			if (cur.getCount() > 1) {
				Log.w(LentaConstants.LoggerMainAppTag,
						"There are more than one data object by using uri '"
								+ getContentProviderUri()
								+ "' with keyType = '" + keyType.name()
								+ "', keyColumnName = '" + keyColumnName
								+ "', keyValue = '" + keyValue
								+ "'. Will use the first one from the list.");
			}
			
			if (cur.moveToFirst())
				return createDaoObject(cur);
		} finally {
			cur.close();
		}
		
		return null;
	}
	
	@Override
	public long create(T daoObject) {
		Uri uri = cr.insert(getContentProviderUri(), prepareContentValues(daoObject));
		long id = ContentUris.parseId(uri);
		daoObject.setId(id);

		cr.notifyChange(getContentProviderUri(), null);
		
		return id;
	}
	
	@Override
	public int delete(long id) {
		Uri uri = ContentUris.withAppendedId(getContentProviderUri(), id);
		return cr.delete(uri, null, null);
	}

	@Override
	public int delete(String keyValue) {
		return delete(getKeyColumnType(), getKeyColumnName(), keyValue);
	}
	
	@Override
	public int delete(SQLiteType keyType,
			String keyColumnName, String keyValue) {
		String[] whereArgs = { keyValue };
		String where = String.format(getWhereFromSQLiteType(keyType), keyColumnName);
		
		return cr.delete(getContentProviderUri(), where, whereArgs);
	}

	public void update(T daoObject) {
		long id = daoObject.getId();
		
		String[] whereArgs;
		String where;
		
		if (id != DatabaseObject.ID_NONE) {
			cr.update(ContentUris.withAppendedId(getContentProviderUri(), id), prepareContentValues(daoObject), null, null);
		} else {
			whereArgs = new String[]{ daoObject.getKeyValue() };
			where = String.format(getWhereFromSQLiteType(getKeyColumnType()), getKeyColumnName());
			
			cr.update(getContentProviderUri(), prepareContentValues(daoObject), where, whereArgs);
		}
	}
	
	public Collection<String> readAllKeys() {
		String[] projectionKeyOnly = {	getKeyColumnName() };
			
		Cursor cur = cr.query(
				getContentProviderUri(), 
				projectionKeyOnly, 
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
	
	protected ContentResolver getContentResolver() {
		return cr;
	}
	
	public void registerContentObserver(boolean notifyForDescendents, ContentObserver observer) {
		cr.registerContentObserver(LentaProvider.CONTENT_URI_NEWS, notifyForDescendents, observer);
	}
	
	public void unregisterContentObserver(ContentObserver observer) {
		cr.unregisterContentObserver(observer);
	}
	
	protected abstract ContentValues prepareContentValues(T newsObject);
	protected abstract T createDaoObject(Cursor cur);
	protected abstract Uri getContentProviderUri();
	protected abstract String getKeyColumnName();
	protected abstract SQLiteType getKeyColumnType();
	protected abstract String getIdColumnName();
	protected abstract String[] getProjectionAll();
}
