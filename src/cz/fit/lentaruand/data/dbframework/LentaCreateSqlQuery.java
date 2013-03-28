package cz.fit.lentaruand.data.dbframework;

import android.database.sqlite.SQLiteDatabase;

public class LentaCreateSqlQuery implements SqlQuery<Long> {
	private String fullQuery;
	
	public LentaCreateSqlQuery(String fullQuery) {
		this.fullQuery = fullQuery;
	}
	
	@Override
	public Long execute(SQLiteDatabase db) {
		return db.insert(table, nullColumnHack, values)
	}
}
