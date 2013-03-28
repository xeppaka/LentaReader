package cz.fit.lentaruand.data.dbframework.sqlite;

import java.util.Map;

public interface SQLiteDataObject {
	Map<SQLiteColumnDefinition, DataObjectValue<?>> getValues();
	void setValue(SQLiteColumnDefinition column, DataObjectValue<?> value);
}
