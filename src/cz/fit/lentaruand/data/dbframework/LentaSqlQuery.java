package cz.fit.lentaruand.data.db.unfinishedStupidity;

import android.database.sqlite.SQLiteDatabase;

public class LentaSqlQuery implements SqlQuery {
	private String fullQuery;
	
	public LentaSqlQuery(String fullQuery) {
		this.fullQuery = fullQuery;
	}
	
	@Override
	public void execute(SQLiteDatabase db) {
		db.execSQL(fullQuery);
	}
}
