package cz.fit.lentaruand.data.dbframework.sqlite;

import java.util.HashMap;
import java.util.Map;

import cz.fit.lentaruand.data.dbframework.ColumnDefinition;
import cz.fit.lentaruand.data.dbframework.DataObject;
import cz.fit.lentaruand.data.dbframework.DataObjectValue;

public class SQLiteDataObject implements DataObject<SQLiteDataType> {
	private Map<ColumnDefinition<SQLiteDataType>, DataObjectValue<?>> values;
	
	public SQLiteDataObject() {
		values = new HashMap<ColumnDefinition<SQLiteDataType>, DataObjectValue<?>>();
	}
	
	@Override
	public Map<ColumnDefinition<SQLiteDataType>, DataObjectValue<?>> getValues() {
		return values;
	}

	@Override
	public void setValue(ColumnDefinition<SQLiteDataType> column,
			DataObjectValue<?> value) {
		values.put(column, value);
	}
}
