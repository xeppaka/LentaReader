package cz.fit.lentaruand.data.dao;

import java.sql.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.exceptions.InconsistentDatastoreException;
import cz.fit.lentaruand.data.db.NewsEntry;

public class NewsDao {
	public static News read(SQLiteDatabase db, String guid) {
		String[] projection = {
				NewsEntry.COLUMN_NAME_GUID,
				NewsEntry.COLUMN_NAME_TITLE,
				NewsEntry.COLUMN_NAME_LINK,
				NewsEntry.COLUMN_NAME_IMAGELINK,
				NewsEntry.COLUMN_NAME_PUBDATE,
				NewsEntry.COLUMN_NAME_RUBRIC,
				NewsEntry.COLUMN_NAME_SUBRUBRIC,
				NewsEntry.COLUMN_NAME_BRIEFTEXT,
				NewsEntry.COLUMN_NAME_FULLTEXT
		};
		
		String where = NewsEntry.COLUMN_NAME_GUID + " LIKE ?";
		
		String[] whereArgs = {
				guid
		};
		
		Cursor cur = db.query(
				NewsEntry.TABLE_NAME, 
				projection, 
				where,
				whereArgs, 
				null, 
				null, 
				null
				);
		
		try {
			if (cur.getCount() > 1)
				throw new InconsistentDatastoreException("There are more than one news in the database with guid = '" + guid + "'.");
			
			cur.moveToFirst();
			
			String guidDb = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_GUID));
			String title = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_TITLE));
			String link = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_LINK));
			String imageLink = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGELINK));
			Date pubDate = new Date(cur.getLong(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_PUBDATE)));
			Rubrics rubric = Rubrics.valueOf(cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_RUBRIC)));
			
			String subRubricStr = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_SUBRUBRIC));
			Rubrics subRubric = null;
			if (subRubricStr != null && !subRubricStr.isEmpty())
				subRubric = Rubrics.valueOf(subRubricStr);
			
			String briefText = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_BRIEFTEXT));
			String fullText = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_FULLTEXT));
			
			return new News(guidDb, title, link, briefText, fullText, pubDate, imageLink, rubric, subRubric);
		} finally {
			if (cur != null)
				cur.close();
		}
	}
	
	public static long create(SQLiteDatabase db, News news) {
		return db.insert(NewsEntry.TABLE_NAME, null, prepareContentValues(news));
	}
	
	public static void remove(SQLiteDatabase db, String guid) {
		String where = NewsEntry.COLUMN_NAME_GUID + " LIKE ?";
		
		String[] whereArgs = {
				guid
		};
		
		db.delete(NewsEntry.TABLE_NAME, where, whereArgs);
	}
	
	public static void update(SQLiteDatabase db, News news) {
		String where = NewsEntry.COLUMN_NAME_GUID + " LIKE ?";
		
		String[] whereArgs = {
				news.getGuid()
		};
		
		db.update(NewsEntry.TABLE_NAME, prepareContentValues(news), where, whereArgs);
	}
	
	private static ContentValues prepareContentValues(News news) {
		ContentValues values = new ContentValues();
		
		values.put(NewsEntry.COLUMN_NAME_GUID, news.getGuid());
		values.put(NewsEntry.COLUMN_NAME_TITLE, news.getTitle());
		values.put(NewsEntry.COLUMN_NAME_LINK, news.getLink());
		
		if (news.getImageLink() == null)
			values.putNull(NewsEntry.COLUMN_NAME_IMAGELINK);
		else
			values.put(NewsEntry.COLUMN_NAME_IMAGELINK, news.getImageLink());
		
		values.put(NewsEntry.COLUMN_NAME_PUBDATE, news.getPubDate().getTime());
		values.put(NewsEntry.COLUMN_NAME_RUBRIC, news.getRubric().name());
		
		if (news.getSubRubric() == null)
			values.putNull(NewsEntry.COLUMN_NAME_SUBRUBRIC);
		else
			values.put(NewsEntry.COLUMN_NAME_SUBRUBRIC, news.getSubRubric().name());
		
		values.put(NewsEntry.COLUMN_NAME_BRIEFTEXT, news.getBriefText());
		
		if (news.getFullText() == null)
			values.putNull(NewsEntry.COLUMN_NAME_FULLTEXT);
		else
			values.put(NewsEntry.COLUMN_NAME_FULLTEXT, news.getFullText());
		
		return values;
	}
}
