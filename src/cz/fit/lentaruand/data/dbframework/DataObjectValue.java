package cz.fit.lentaruand.data.dbframework;

import cz.fit.lentaruand.data.dbframework.DataConverter;

public interface DataObjectValue<T> {
	T getValue();
	String toSqlString();
	void setConverter(DataConverter<T> converter);
}
