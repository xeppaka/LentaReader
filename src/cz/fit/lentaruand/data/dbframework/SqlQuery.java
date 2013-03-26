package cz.fit.lentaruand.data.dbframework;

import android.database.sqlite.SQLiteDatabase;

public interface SqlQuery {
	void execute(SQLiteDatabase db);
}
