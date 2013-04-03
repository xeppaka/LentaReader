package cz.fit.lentaruand.data.dbframework;

/**
 * 
 * @author kacpa01
 */
public interface ColumnDefinition<E extends Enum<E>> {
	/**
	 * Gets the name of the column.
	 * @return column name.
	 */
	String getName();
	
	/**
	 * Get the type of the column. This type can be used in SQL statements for defining tables.
	 * @return column SQL type.
	 */
	String getSqlType();

	/**
	 * Get type of the column.
	 * @return Value from the Enum specified in generic.
	 */
	E getType();
	
	/**
	 * States if that column is a key.
	 * @return
	 */
	boolean isKey();
	
	/**
	 * All column definitions should be comparable. That's why they all contain equals method.
	 * @param other is other instance of ColumnDefinition.
	 * @return true if this instance is equal to other instance of ColumnDefinition, false otherwise.
	 */
	boolean equals(Object other);
	
	/**
	 * Calculates hash code of current ColumnDefinition instance. 
	 * @return calculated hash code.
	 */
	int hashCode();
}
