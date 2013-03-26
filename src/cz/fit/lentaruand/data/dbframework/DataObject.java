package cz.fit.lentaruand.data.dbframework;

import java.util.Map;

public interface DataObject {
	Map<ColumnDefinition, DataObjectValue<?>> getValues();
	void setValue(ColumnDefinition column, DataObjectValue<?> value);
}
