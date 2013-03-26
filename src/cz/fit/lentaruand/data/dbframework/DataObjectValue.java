package cz.fit.lentaruand.data.dbframework;

public interface DataObjectValue<T> {
	T getValue();
	String toSqlValue();
	void setConverter(DataConverter<T> converter);
}
