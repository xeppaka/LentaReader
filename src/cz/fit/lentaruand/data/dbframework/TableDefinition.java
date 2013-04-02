package cz.fit.lentaruand.data.dbframework;

import java.util.Collection;

public interface TableDefinition<E extends Enum<E>> {
	Collection<ColumnDefinition<E>> getColumns();
	ColumnDefinition<E> getColumnDefinition(String columnName);
	String getTableName();
}
