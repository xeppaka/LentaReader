package cz.fit.lentaruand.data.dbframework.sqlite;

import cz.fit.lentaruand.data.dbframework.DataConverter;

public interface SQLiteDataObjectValue<T> {
	T getValue();
	String toSqlString();
	void setConverter(DataConverter<T> converter);
}
