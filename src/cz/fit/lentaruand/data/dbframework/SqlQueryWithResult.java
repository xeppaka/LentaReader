package cz.fit.lentaruand.data.dbframework;

import java.util.Collection;

import cz.fit.lentaruand.data.dbframework.sqlite.SQLiteDataObject;

import android.database.sqlite.SQLiteDatabase;

public interface SqlQueryWithResult<T extends SQLiteDataObject> {
	Collection<T> executeWithResult(SQLiteDatabase db);
}
