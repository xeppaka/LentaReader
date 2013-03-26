package cz.fit.lentaruand.data.db.unfinishedStupidity;

import java.util.Collection;

import android.database.sqlite.SQLiteDatabase;

public interface SqlQueryWithResult<T extends DataObject> {
	Collection<T> executeWithResult(SQLiteDatabase db);
}
