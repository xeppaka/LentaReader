package cz.fit.lentaruand.data.db.unfinishedStupidity;

import java.util.Map;


public abstract class SimpleDao<T extends DataObject> implements Dao<T> {

	@Override
	public long create(T object) {
		Map<ColumnDefinition, DataObjectValue<?>> values = object.getValues();
		
	}

	@Override
	public T read(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(T object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(T object) {
		// TODO Auto-generated method stub
		
	}
}
