package cz.fit.lentaruand.data.dao;

import java.sql.Date;

import android.content.ContentValues;
import android.database.Cursor;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.db.NewsEntry;

public class NewsDao extends DefaultDao<News> {
	private static final String[] projectionAll = {
		NewsEntry.COLUMN_NAME_GUID,
		NewsEntry.COLUMN_NAME_TITLE,
		NewsEntry.COLUMN_NAME_LINK,
		NewsEntry.COLUMN_NAME_IMAGELINK,
		NewsEntry.COLUMN_NAME_IMAGECAPTION,
		NewsEntry.COLUMN_NAME_IMAGECREDITS,
		NewsEntry.COLUMN_NAME_PUBDATE,
		NewsEntry.COLUMN_NAME_RUBRIC,
		NewsEntry.COLUMN_NAME_RUBRIC_UPDATE,
		NewsEntry.COLUMN_NAME_BRIEFTEXT,
		NewsEntry.COLUMN_NAME_FULLTEXT
	};
	
	public NewsDao() {
		super(String.class);
	}
	
	@Override
	protected ContentValues prepareContentValues(News news) {
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

	@Override
	protected News createDaoObject(Cursor cur) {
		String guidDb = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_GUID));
		String title = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_TITLE));
		String link = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_LINK));
		String imageLink = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGELINK));
		String imageCaption = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGECAPTION));
		String imageCredits = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGECREDITS));
		Date pubDate = new Date(cur.getLong(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_PUBDATE)));
		Rubrics rubric = Rubrics.valueOf(cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_RUBRIC)));
		boolean rubricUpdateNeed = cur.getInt(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_RUBRIC_UPDATE)) > 0;
		
		String briefText = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_BRIEFTEXT));
		String fullText = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_FULLTEXT));
		
		return new News(guidDb, title, link, briefText, fullText, pubDate, imageLink, imageCaption, imageCredits, rubric, rubricUpdateNeed);
	}

	@Override
	protected String getTableName() {
		return NewsEntry.TABLE_NAME;
	}

	@Override
	protected String getKeyColumnName() {
		return NewsEntry.COLUMN_NAME_GUID;
	}

	@Override
	protected String[] getProjectionAll() {
		return projectionAll;
	}
}
