package cz.fit.lentaruand.data.dbframework;

import java.util.Collection;

public interface TableDefinition {
	Collection<ColumnDefinition> getColumns();
	ColumnDefinition getColumnDefinition(String columnName);
	String getTableName();
}
