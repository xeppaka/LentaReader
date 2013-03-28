package cz.fit.lentaruand.data.dbframework.sqlite;

import cz.fit.lentaruand.data.dbframework.ColumnDefinition;

public interface SQLiteColumnDefinition extends ColumnDefinition {
	SQLiteDataType getDatatype();
}
