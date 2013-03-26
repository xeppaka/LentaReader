package cz.fit.lentaruand.data.dbframework;

import android.database.sqlite.SQLiteDatabase;

public class LentaCreateSqlQuery implements SqlQuery {
	private String fullQuery;
	
	public LentaCreateSqlQuery(String fullQuery) {
		this.fullQuery = fullQuery;
	}
	
	@Override
	public void execute(SQLiteDatabase db) {
		db.execSQL(fullQuery);
	}
}
