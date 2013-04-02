package cz.fit.lentaruand.data.dbframework;

import java.util.Map;

import cz.fit.lentaruand.data.dbframework.ColumnDefinition;

public interface DataObject<E extends Enum<E>> {
	Map<ColumnDefinition<E>, DataObjectValue<?>> getValues();
	void setValue(ColumnDefinition<E> column, DataObjectValue<?> value);
}
