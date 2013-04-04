package cz.fit.lentaruand.data.dao;

import java.sql.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cz.fit.lentaruand.data.Article;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.exceptions.InconsistentDatastoreException;
import cz.fit.lentaruand.data.db.ArticleEntry;
import cz.fit.lentaruand.data.db.NewsEntry;

public class ArticleDao {
	private static final String[] projection = {
		ArticleEntry.COLUMN_NAME_GUID,
		ArticleEntry.COLUMN_NAME_TITLE,
		ArticleEntry.COLUMN_NAME_LINK,
		ArticleEntry.COLUMN_NAME_AUTHOR,
		ArticleEntry.COLUMN_NAME_IMAGELINK,
		ArticleEntry.COLUMN_NAME_IMAGECAPTION,
		ArticleEntry.COLUMN_NAME_IMAGECREDITS,
		ArticleEntry.COLUMN_NAME_PUBDATE,
		ArticleEntry.COLUMN_NAME_RUBRIC,
		ArticleEntry.COLUMN_NAME_RUBRIC_UPDATE,
		ArticleEntry.COLUMN_NAME_BRIEFTEXT,
		ArticleEntry.COLUMN_NAME_FULLTEXT
	};
	
	private static final String guidWhere = ArticleEntry.COLUMN_NAME_GUID + " LIKE ?";
	
	public static Article read(SQLiteDatabase db, String guid) {
		String[] guidWhereArgs = { guid };
		
		Cursor cur = db.query(
				ArticleEntry.TABLE_NAME, 
				projection, 
				guidWhere,
				guidWhereArgs, 
				null, 
				null, 
				null
				);
		
		try {
			if (cur.getCount() > 1)
				throw new InconsistentDatastoreException("There are more than one article in the database with guid = '" + guid + "'.");
			
			cur.moveToFirst();
			
			String guidDb = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_GUID));
			String title = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_TITLE));
			String link = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_LINK));
			String author = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_AUTHOR));
			String imageLink = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_IMAGELINK));
			String imageCaption = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_IMAGECAPTION));
			String imageCredits = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_IMAGECREDITS));
			Date pubDate = new Date(cur.getLong(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_PUBDATE)));
			Rubrics rubric = Rubrics.valueOf(cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_RUBRIC)));
			boolean rubricUpdateNeed = cur.getInt(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_RUBRIC_UPDATE)) > 0;
			
			String briefText = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_BRIEFTEXT));
			String fullText = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_FULLTEXT));
			
			return new Article(guidDb, title, link, author, briefText, fullText, pubDate, imageLink, imageCaption, imageCredits, rubric, rubricUpdateNeed);
		} finally {
			cur.close();
		}
	}
	
	public static long create(SQLiteDatabase db, News news) {
		return db.insert(NewsEntry.TABLE_NAME, null, prepareContentValues(news));
	}
	
	public static void delete(SQLiteDatabase db, String guid) {
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
		
		if (news.getImageCaption() == null)
			values.putNull(NewsEntry.COLUMN_NAME_IMAGECAPTION);
		else
			values.put(NewsEntry.COLUMN_NAME_IMAGECAPTION, news.getImageCaption());
		
		if (news.getImageCredits() == null)
			values.putNull(NewsEntry.COLUMN_NAME_IMAGECREDITS);
		else
			values.put(NewsEntry.COLUMN_NAME_IMAGECREDITS, news.getImageCredits());
		
		values.put(NewsEntry.COLUMN_NAME_PUBDATE, news.getPubDate().getTime());
		values.put(NewsEntry.COLUMN_NAME_RUBRIC, news.getRubric().name());
		values.put(NewsEntry.COLUMN_NAME_BRIEFTEXT, news.getBriefText());
		values.put(NewsEntry.COLUMN_NAME_RUBRIC_UPDATE, news.isRubricUpdateNeed() ? 1 : 0);
		
		if (news.getFullText() == null)
			values.putNull(NewsEntry.COLUMN_NAME_FULLTEXT);
		else
			values.put(NewsEntry.COLUMN_NAME_FULLTEXT, news.getFullText());
		
		return values;
	}
}
