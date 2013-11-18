package com.xeppaka.lentareader.data.dao.objects;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.xeppaka.lentareader.data.DatabaseObject;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.db.SQLiteType;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class ContentResolverDao<T extends DatabaseObject> implements Dao<T> {
	private final static String textKeyWhere;
	private final static String intKeyWhere;
	private final static String[] projectionId = { BaseColumns._ID };
	
	protected static String getWhereFromSQLiteType(SQLiteType type) {
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
	
	private final ContentResolver cr;
	
	protected ContentResolverDao(ContentResolver cr) {
		if (cr == null) {
			throw new IllegalArgumentException("contentResolver is null.");
		}
		
		this.cr = cr;
	}

	@Override
	public List<T> read() {
		Cursor cur = cr.query(getContentProviderUri(),
				getProjectionAll(), null, null, null);

        if (cur == null) {
            return Collections.EMPTY_LIST;
        }

		try {
			List<T> result = new ArrayList<T>();

			if (cur.moveToFirst()) {
				do {
					result.add(createDataObject(cur));
				} while (cur.moveToNext());
			}
			
			return result;
		} finally {
            if (cur != null) {
			    cur.close();
            }
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

        if (cur == null) {
            return null;
        }

        try {
			if (cur.getCount() > 1) {
				Log.w(LentaConstants.LoggerMainAppTag, "There are more than one data object by using uri '" + uri + "'. Will use the first one from the list.");
			}
			
			if (cur.moveToFirst())
				return createDataObject(cur);
		} finally {
            if (cur != null) {
                cur.close();
            }
		}
		
		return null;
	}

	@Override
	public List<T> read(List<Long> ids) {
		List<T> result = new ArrayList<T>(ids.size());
		
		for (long id : ids) {
			T dataObject = read(id);
			
			if (dataObject != null) {
				result.add(dataObject);
			}
		}
		
		return result;
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
				return createDataObject(cur);
		} finally {
            if (cur != null) {
                cur.close();
            }
		}
		
		return null;
	}
	
	@Override
	public long create(T daoObject) {
		Uri uri = cr.insert(getContentProviderUri(), prepareContentValues(daoObject));
		long id = ContentUris.parseId(uri);
		daoObject.setId(id);

		cr.notifyChange(ContentUris.withAppendedId(getContentProviderUri(), id), null);
		
		return id;
	}
	
	@Override
	public List<Long> create(List<T> dataObjects) {
        List<Long> result = null;
		
		for (T dataObject : dataObjects) {
			Uri uri = cr.insert(getContentProviderUri(), prepareContentValues(dataObject));
			Long id = ContentUris.parseId(uri);
			
			dataObject.setId(id);
			
			if (result == null) {
				result = new ArrayList<Long>();
			}
			
			result.add(id);
		}
		

		cr.notifyChange(getContentProviderUri(), null);
		
		if (result == null) {
			return Collections.emptyList();
		}
		
		return result;
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

	public int update(T daoObject) {
		long id = daoObject.getId();
		
		String[] whereArgs;
		String where;
		
		if (id != DatabaseObject.ID_NONE) {
			return cr.update(ContentUris.withAppendedId(getContentProviderUri(), id), prepareContentValues(daoObject), null, null);
		} else {
			whereArgs = new String[]{ daoObject.getKeyValue() };
			where = String.format(getWhereFromSQLiteType(getKeyColumnType()), getKeyColumnName());
			
			return cr.update(getContentProviderUri(), prepareContentValues(daoObject), where, whereArgs);
		}
	}
	
	@Override
	public boolean exist(long id) {
		Uri uri = ContentUris.withAppendedId(getContentProviderUri(), id);
		Cursor cur = null;
		
		try {
			cur = cr.query(uri, projectionId, null, null, null);
			
			if (cur == null) {
				return false;
			}
			
			return cur.moveToFirst();
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
	}

	@Override
	public boolean exist(String key) {
		String[] whereArgs = { key };
		String where = String.format(getWhereFromSQLiteType(getKeyColumnType()), getKeyColumnName());
		
		Cursor cur = null;
		
		try {
			cur = cr.query(getContentProviderUri(), projectionId, where, whereArgs, null);
			
			if (cur == null) {
				return false;
			}
			
			return cur.moveToFirst();
		} finally {
			if (cur != null) {
				cur.close();
			}
		}
	}
	
	@Override
	public List<String> readAllKeys() {
		String[] projectionKeyOnly = {	getKeyColumnName() };
			
		Cursor cur = cr.query(
				getContentProviderUri(), 
				projectionKeyOnly, 
				null,
				null,
				null
				);

        if (cur == null) {
            return Collections.EMPTY_LIST;
        }

		try {
            List<String> result = new ArrayList<String>();
			
			while (cur.moveToNext()) {
				result.add(cur.getString(cur.getColumnIndexOrThrow(getKeyColumnName())));
			}

			return result;
		} finally {
            if (cur != null) {
			    cur.close();
            }
		}
	}
	
	@Override
	public List<Long> readAllIds() {
		String[] projectionIdOnly = { BaseColumns._ID };
		
		Cursor cur = cr.query(
				getContentProviderUri(), 
				projectionIdOnly,
				null,
				null,
				null
				);

        if (cur == null) {
            return Collections.EMPTY_LIST;
        }
		
		try {
            List<Long> result = new ArrayList<Long>();

			while (cur.moveToNext()) {
				result.add(cur.getLong(cur.getColumnIndexOrThrow(BaseColumns._ID)));
			}

			return result;
		} finally {
            if (cur != null) {
                cur.close();
            }
		}
	}

	protected ContentResolver getContentResolver() {
		return cr;
	}
	
	@Override
	public void registerContentObserver(final Dao.Observer<T> observer) {
		DaoObserver<T> daoObserver;
		
		if (observer instanceof DaoObserver) {
			daoObserver = (DaoObserver<T>)observer;
		} else {
//			daoObserver = new DaoObserver<T>() {
//				@Override
//				public void onDataChanged(boolean selfChange, Collection<T> dataObjects) {
//					observer.onDataChanged(selfChange, dataObjects);
//				}
//				
//				@Override
//				public void onDataChanged(boolean selfChange, T dataObject) {
//					observer.onDataChanged(selfChange, dataObject);
//				}
//			};
			
			throw new IllegalArgumentException("observer is not derived from DaoObserver which is not supported now. You should create observer by extending DaoObserver abstract class.");
		}
		
		daoObserver.setDao(this);
		cr.registerContentObserver(getContentProviderUri(), true, daoObserver.getContentObserver());
	}
	
	@Override
	public void unregisterContentObserver(Dao.Observer<T> observer) {
		DaoObserver<T> daoObserver;
		
		if (observer instanceof DaoObserver) {
			daoObserver = (DaoObserver<T>)observer;
		} else {
			throw new IllegalArgumentException("observer is not derived from DaoObserver which is not supported now. You should create observer by extending DaoObserver abstract class.");
		}
		
		cr.unregisterContentObserver(daoObserver.getContentObserver());
	}
	
	protected abstract ContentValues prepareContentValues(T newsObject);
	protected abstract T createDataObject(Cursor cur);
	protected abstract String getKeyColumnName();
	protected abstract SQLiteType getKeyColumnType();
	protected abstract String getIdColumnName();
	protected abstract String[] getProjectionAll();
	protected abstract Uri getContentProviderUri();
}
