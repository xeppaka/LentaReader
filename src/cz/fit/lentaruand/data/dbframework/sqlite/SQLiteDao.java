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
import cz.fit.lentaruand.data.dbframework.DataObject;
import cz.fit.lentaruand.data.dbframework.DataObjectValue;
import cz.fit.lentaruand.data.dbframework.TableDefinition;

public abstract class SQLiteDao implements Dao<DataObject<SQLiteDataType>, SQLiteDataType> {
	private SQLiteDatabase db;
	private TableDefinition<SQLiteDataType> table;
	private final String[] columnsAll;
	
	private DataObjectValue<?> createDataObjectValue(Cursor c, SQLiteDataType type, int column) {
		switch (type) {
		case INTEGER:
			DataObjectValue<Integer> intRes = new SQLiteDataObjectValue<Integer>();
			intRes.setValue(c.getInt(column));
			return intRes;
		case REAL:
			DataObjectValue<Float> floatRes = new SQLiteDataObjectValue<Float>();
			floatRes.setValue(c.getFloat(column));
			return floatRes;
		case TEXT:
			DataObjectValue<String> stringRes = new SQLiteDataObjectValue<String>();
			stringRes.setValue(c.getString(column));
			return stringRes;
		case BLOB:
			break;
		}
		
		return null;
	}
	
	public SQLiteDao(SQLiteDatabase db, TableDefinition<SQLiteDataType> table) {
		this.db = db;
		this.table = table;
		
		Collection<ColumnDefinition<SQLiteDataType>> columns = table.getColumns();
		columnsAll = new String[columns.size()];
		
		int i = 0;
		for (ColumnDefinition<SQLiteDataType> column : columns) {
			columnsAll[i] = column.getName();
			++i;
		}
	}

	@Override
	public long create(DataObject<SQLiteDataType> object) {
		try {
			return createOrThrow(object);
		} catch (SQLException e) {
			return -1;
		}
	}
	
	@Override
	public long createOrThrow(DataObject<SQLiteDataType> object) {
		Map<ColumnDefinition<SQLiteDataType>, DataObjectValue<?>> values = object.getValues();
		
		ContentValues content = new ContentValues();
		Set<Entry<ColumnDefinition<SQLiteDataType>, DataObjectValue<?>>> entries = values.entrySet();
		
		for (Entry<ColumnDefinition<SQLiteDataType>, DataObjectValue<?>> entry : entries) {
			if (!entry.getKey().isKey())
				content.put(entry.getKey().getName(), entry.getValue().toSqlString());
		}
		
		return db.insertOrThrow(table.getTableName(), null, content);
	}

	@Override
	public DataObject<SQLiteDataType> read(String id, Class<DataObject<SQLiteDataType>> clazz) {
		try {
			return readOrThrow(id, clazz);
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public DataObject<SQLiteDataType> readOrThrow(String id, Class<DataObject<SQLiteDataType>> clazz) throws InstantiationException, IllegalAccessException {
		Cursor c = db.query(table.getTableName(), columnsAll, "id = " + id, null, null, null, null);
		
		try {
			DataObject<SQLiteDataType> object = clazz.newInstance();
			if (c.moveToFirst()) {
				int colIndex = -1;
				
				Collection<ColumnDefinition<SQLiteDataType>> columns = table.getColumns();
				for (ColumnDefinition<SQLiteDataType> column : columns) {
					colIndex = c.getColumnIndexOrThrow(column.getName());
					SQLiteDataType colType = column.getType();
					object.setValue(column, createDataObjectValue(c, colType, colIndex));
				}
			}
			
			return object;
		} finally {
			if (c != null)
				c.close();
		}
	}

	@Override
	public void update(DataObject<SQLiteDataType> object) {
	}

	@Override
	public void delete(DataObject<SQLiteDataType> object) {
	}
}
