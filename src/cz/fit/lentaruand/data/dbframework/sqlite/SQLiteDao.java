package cz.fit.lentaruand.data.dbframework.sqlite;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import cz.fit.lentaruand.data.dbframework.ColumnDefinition;
import cz.fit.lentaruand.data.dbframework.Dao;
import cz.fit.lentaruand.data.dbframework.DataObjectValue;

public abstract class SQLiteDao<T extends SQLiteDataObject> implements Dao<T> {
	private SQLiteDatabase db;
	private SQLiteTableDefinition table;
	private final String[] columnsAll;
	
	private create
	
	public SQLiteDao(SQLiteDatabase db, SQLiteTableDefinition table) {
		this.db = db;
		this.table = table;
		
		Collection<SQLiteColumnDefinition> columns = table.getColumns();
		columnsAll = new String[columns.size()];
		
		int i = 0;
		for (ColumnDefinition column : columns) {
			columnsAll[i] = column.getName();
			++i;
		}
	}

	@Override
	public long create(T object) {
		try {
			return createOrThrow(object);
		} catch (SQLException e) {
			return -1;
		}
	}
	
	@Override
	public long createOrThrow(T object) {
		Map<ColumnDefinition, DataObjectValue<?>> values = object.getValues();
		
		ContentValues content = new ContentValues();
		Set<Entry<ColumnDefinition, DataObjectValue<?>>> entries = values.entrySet();
		
		for (Entry<ColumnDefinition, DataObjectValue<?>> entry : entries) {
			if (!entry.getKey().isKey())
				content.put(entry.getKey().getName(), entry.getValue().toSqlString());
		}
		
		return db.insertOrThrow(table.getTableName(), null, content);
	}

	@Override
	public T read(String id, Class<T> clazz) {
		
	}

	@Override
	public T readOrThrow(String id, Class<T> clazz) throws InstantiationException, IllegalAccessException {
		Cursor c = db.query(table.getTableName(), columnsAll, "id = " + id, null, null, null, null);
		
		try {
			T object = clazz.newInstance();
			if (c.moveToFirst()) {
				int colIndex = -1;
				
				Collection<ColumnDefinition> columns = table.getColumns();
				for (ColumnDefinition column : columns) {
					colIndex = c.getColumnIndexOrThrow(column.getName());
					
				}
			}
		} finally {
			if (c != null)
				c.close();
		}
		
		return null;
	}

	@Override
	public void update(T object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(T object) {
		// TODO Auto-generated method stub
		
	}
}
