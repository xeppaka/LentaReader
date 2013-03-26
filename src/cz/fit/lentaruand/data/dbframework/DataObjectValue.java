package cz.fit.lentaruand.data.db.unfinishedStupidity;

public interface DataObjectValue<T> {
	T getValue();
	String toSqlValue();
	void setConverter(DataConverter<T> converter);
}
