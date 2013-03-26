package cz.fit.lentaruand.data.dbframework;

import java.util.Collection;

public interface TableDefinition {
	Collection<ColumnDefinition> getColumns();
	String getTableName();
}
