package cz.fit.lentaruand.data.db.unfinishedStupidity;

import java.util.Collection;

public interface TableDefinition {
	Collection<ColumnDefinition> getColumns();
	String getTableName();
}
