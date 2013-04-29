package cz.fit.lentaruand.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LentaDbHelper extends SQLiteOpenHelper {

	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "LentaNews.db";
	
	public LentaDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(NewsEntry.SQL_CREATE_TABLE);
		db.execSQL(NewsLinksEntry.SQL_CREATE_TABLE);
		db.execSQL(ArticleEntry.SQL_CREATE_TABLE);
		db.execSQL(ArticleLinksEntry.SQL_CREATE_TABLE);
		db.execSQL(PhotoEntry.SQL_CREATE_TABLE);
		db.execSQL(PhotoImageEntry.SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(PhotoImageEntry.SQL_DELETE_TABLE);
		db.execSQL(PhotoEntry.SQL_DELETE_TABLE);
		db.execSQL(ArticleLinksEntry.SQL_DELETE_TABLE);
		db.execSQL(ArticleEntry.SQL_DELETE_TABLE);
		db.execSQL(NewsLinksEntry.SQL_DELETE_TABLE);
		db.execSQL(NewsEntry.SQL_DELETE_TABLE);
		onCreate(db);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}
	
/*
 * this method is available in the next versions of Android.
 * 
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
 */
}
