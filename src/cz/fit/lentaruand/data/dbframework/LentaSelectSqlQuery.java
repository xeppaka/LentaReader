package cz.fit.lentaruand.data.dbframework;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class LentaSelectSqlQuery<T extends DataObject> implements SqlQueryWithResult<T> {
	private String tableName;
	private String[] columns;
	private String selection;
	private String[] selectionArgs;
	private String groupBy;
	private String having;
	private String orderBy;
	private Class<T> clazz;
	
	public LentaSelectSqlQuery(Class<T> clazz) {
	}
	
	@Override
	public Collection<T> executeWithResult(SQLiteDatabase db) {
		Cursor c = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		if (c.moveToFirst()) {
			boolean next = true;
			Collection<T> result = new ArrayList<T>();
			
			while (next) {
				try {
					Constructor<T> cntr = clazz.getConstructor();
					T object = cntr.newInstance();
					object.setValue(c.get, value)
				} catch (SecurityException e) {
					e.printStackTrace();
					continue;
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					continue;
				} catch (InstantiationException e) {
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					continue;
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					continue;
				}
				
				next = c.moveToNext();
			}
			
			return result;
		}
		
		return Collections.emptyList();
	}
}
