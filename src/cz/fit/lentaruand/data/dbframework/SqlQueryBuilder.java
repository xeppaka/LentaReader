package cz.fit.lentaruand.data.dbframework;

import java.util.Collection;


public class SqlQueryBuilder {
	private TableDefinition tableDefinition;
	private String tableName;
	
	public SqlQueryBuilder forDefinition(TableDefinition tableDefinition) {
		this.tableDefinition = tableDefinition;
		
		return this;
	}

	public SqlQueryBuilder forTable(String tableName) {
		this.tableName = tableName;		
		
		return this;
	}
	
	public <T extends DataObject> SqlQueryWithResult<T> buildSelect() {
		return null;
	}

	public SqlQuery buildUpdate() {
		return null;
	}

	public SqlQuery buildDelete() {
		return null;
	}

	public SqlQuery buildCreate() {
		if (tableDefinition == null)
			throw new IllegalStateException("Trying to build SELECT SQL statement while table definition is not set.");

		final Collection<ColumnDefinition> columns = tableDefinition.getColumns();
		
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(tableDefinition.getTableName())
				.append(" (_ID INTEGER PRIMARY KEY");
		
		for (ColumnDefinition column : columns) {
			sb.append(", ").append(column.getName()).append(" ").append(column.getType());
		}
		
		sb.append(")");
		
		return new LentaCreateSqlQuery(sb.toString());
	}

}
