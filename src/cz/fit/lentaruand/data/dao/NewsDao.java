package cz.fit.lentaruand.data.dao;

import android.database.sqlite.SQLiteDatabase;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.db.LentaDbHelper;

public class NewsDao {
	public static News getNews(String guid) {
		return null;
	}
	
	public static void createNews(LentaDbHelper dbHelper, News news) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		
	}
}
