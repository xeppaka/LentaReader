package cz.fit.lentaruand.data.db.unfinishedStupidity;

import java.util.Collection;

import android.database.sqlite.SQLiteDatabase;


public class LentaSqlQueryWithResult implements SqlQueryWithResult<DataObject> {
	private String fullQuery;
	
	public LentaSqlQueryWithResult(String fullQuery) {
		this.fullQuery = fullQuery;
	}
	
	@Override
	public Collection<DataObject> executeWithResult(SQLiteDatabase db) {
		return null;
	}
}
