package cz.fit.lentaruand.data.dbframework;


public interface Dao<T extends DataObject> {
	long create(T object);
	long createOrThrow(T object);
	T read(String id, Class<T> clazz);
	T readOrThrow(String id, Class<T> clazz) throws InstantiationException, IllegalAccessException;
	void update(T object);
	void delete(T object);
}
