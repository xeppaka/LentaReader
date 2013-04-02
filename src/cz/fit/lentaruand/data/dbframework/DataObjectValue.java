package cz.fit.lentaruand.data.dbframework;

import cz.fit.lentaruand.data.dbframework.DataConverter;

public abstract class DataObjectValue<T> {
	private T value;
	private DataConverter<T> converter;
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	public abstract String toSqlString();
	
	public void setConverter(DataConverter<T> converter) {
		this.converter = converter;
	}
	
	public DataConverter<T> getConverter() {
		return converter;
	}
}
