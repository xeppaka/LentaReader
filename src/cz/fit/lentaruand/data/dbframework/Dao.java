package cz.fit.lentaruand.data.db.unfinishedStupidity;

public interface Dao<T extends DataObject> {
	long create(T object);
	T read(String id);
	void update(T object);
	void delete(T object);
}
