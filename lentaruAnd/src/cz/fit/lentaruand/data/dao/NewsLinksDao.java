package cz.fit.lentaruand.data.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cz.fit.lentaruand.data.Link;
import cz.fit.lentaruand.data.db.NewsLinksEntry;
import cz.fit.lentaruand.data.db.SQLiteType;

public class NewsLinksDao extends DefaultDao<Link> {
	private static final String[] projectionAll = {
		NewsLinksEntry._ID,
		NewsLinksEntry.COLUMN_NAME_NEWS_ID,
		NewsLinksEntry.COLUMN_NAME_TITLE,
		NewsLinksEntry.COLUMN_NAME_URL,
		NewsLinksEntry.COLUMN_NAME_DATE
	};

	@Override
	protected ContentValues prepareContentValues(Link link) {
		ContentValues values = new ContentValues();

		values.put(NewsLinksEntry.COLUMN_NAME_NEWS_ID, link.getNewsId());
		if (link.getTitle() == null)
			values.putNull(NewsLinksEntry.COLUMN_NAME_TITLE);
		else
			values.put(NewsLinksEntry.COLUMN_NAME_TITLE, link.getTitle());
		
		values.put(NewsLinksEntry.COLUMN_NAME_URL, link.getUrl());
		
		if (link.getDate() == null)
			values.putNull(NewsLinksEntry.COLUMN_NAME_DATE);
		else
			values.put(NewsLinksEntry.COLUMN_NAME_DATE, link.getDate().getTime());
		
		return values;
	}

	@Override
	protected Link createDaoObject(Cursor cur) {
		long id = cur.getInt(cur.getColumnIndexOrThrow(NewsLinksEntry._ID));
		long newsId = cur.getInt(cur.getColumnIndexOrThrow(NewsLinksEntry.COLUMN_NAME_NEWS_ID));
		String title = cur.getString(cur.getColumnIndexOrThrow(NewsLinksEntry.COLUMN_NAME_TITLE));
		String url = cur.getString(cur.getColumnIndexOrThrow(NewsLinksEntry.COLUMN_NAME_URL));
		Date date = new Date(cur.getLong(cur.getColumnIndexOrThrow(NewsLinksEntry.COLUMN_NAME_DATE)));
		
		return new Link(id, newsId, url, title, date);
	}

	@Override
	protected String getTableName() {
		return NewsLinksEntry.TABLE_NAME;
	}

	@Override
	protected String getIdColumnName() {
		return NewsLinksEntry._ID;
	}

	@Override
	protected String getKeyColumnName() {
		return NewsLinksEntry._ID;
	}

	@Override
	protected SQLiteType getKeyColumnType() {
		return SQLiteType.INTEGER;
	}

	@Override
	protected String[] getProjectionAll() {
		return projectionAll;
	}

	public Collection<Link> readForNews(SQLiteDatabase db, long newsId) {
		List<Link> result = new ArrayList<Link>();
		
		String[] keyWhereArgs = { String.valueOf(newsId) };
		String keyWhere = NewsLinksEntry.COLUMN_NAME_NEWS_ID + " = ?";
		
		Cursor cur = db.query(
				getTableName(), 
				getProjectionAll(), 
				keyWhere,
				keyWhereArgs, 
				null, 
				null, 
				null
				);
		
		try {
			if (cur.moveToFirst()) {
				do {
					result.add(createDaoObject(cur));
				} while (cur.moveToNext());
			}
			
			Collections.sort(result);
			
			return result;
		} finally {
			cur.close();
		}
	}
}
