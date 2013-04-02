package cz.fit.lentaruand.data.dbframework.sqlite;

import cz.fit.lentaruand.data.dbframework.DataObjectValue;

public class SQLiteDataObjectValue<T> extends DataObjectValue<T> {
	@Override
	public String toSqlString() {
		if (getConverter() != null)
			return getConverter().convert(getValue());
		else
			return getValue().toString();
	}
}
