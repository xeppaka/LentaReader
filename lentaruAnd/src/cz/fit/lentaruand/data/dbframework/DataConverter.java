package cz.fit.lentaruand.data.dbframework;

public interface DataConverter<T> {
	String convert(T value);
}
