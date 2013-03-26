package cz.fit.lentaruand.data.db.unfinishedStupidity;

import java.util.Map;

public interface DataObject {
	Map<ColumnDefinition, DataObjectValue<?>> getValues();
}
