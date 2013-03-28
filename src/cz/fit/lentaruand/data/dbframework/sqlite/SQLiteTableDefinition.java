package cz.fit.lentaruand.data.dbframework.sqlite;

import cz.fit.lentaruand.data.dbframework.TableDefinition;

public interface SQLiteTableDefinition extends TableDefinition {
	SQLiteColumnDefinition getColumnDefinition(String columnName);
}
