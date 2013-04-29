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
import cz.fit.lentaruand.data.db.ArticleLinksEntry;
import cz.fit.lentaruand.data.db.SQLiteType;

public class ArticleLinksDao extends DefaultDao<Link> {
	private static final String[] projectionAll = {
		ArticleLinksEntry._ID,
		ArticleLinksEntry.COLUMN_NAME_ARTICLE_ID,
		ArticleLinksEntry.COLUMN_NAME_TITLE,
		ArticleLinksEntry.COLUMN_NAME_URL,
		ArticleLinksEntry.COLUMN_NAME_DATE
	};

	@Override
	protected ContentValues prepareContentValues(Link link) {
		ContentValues values = new ContentValues();

		values.put(ArticleLinksEntry.COLUMN_NAME_ARTICLE_ID, link.getNewsId());
		if (link.getTitle() == null)
			values.putNull(ArticleLinksEntry.COLUMN_NAME_TITLE);
		else
			values.put(ArticleLinksEntry.COLUMN_NAME_TITLE, link.getTitle());
		
		values.put(ArticleLinksEntry.COLUMN_NAME_URL, link.getUrl());
		
		if (link.getDate() == null)
			values.putNull(ArticleLinksEntry.COLUMN_NAME_DATE);
		else
			values.put(ArticleLinksEntry.COLUMN_NAME_DATE, link.getDate().getTime());
		
		return values;
	}

	@Override
	protected Link createDaoObject(Cursor cur) {
		long id = cur.getInt(cur.getColumnIndexOrThrow(ArticleLinksEntry._ID));
		long articleId = cur.getInt(cur.getColumnIndexOrThrow(ArticleLinksEntry.COLUMN_NAME_ARTICLE_ID));
		String title = cur.getString(cur.getColumnIndexOrThrow(ArticleLinksEntry.COLUMN_NAME_TITLE));
		String url = cur.getString(cur.getColumnIndexOrThrow(ArticleLinksEntry.COLUMN_NAME_URL));
		Date date = new Date(cur.getLong(cur.getColumnIndexOrThrow(ArticleLinksEntry.COLUMN_NAME_DATE)));
		
		return new Link(id, articleId, url, title, date);
	}

	@Override
	protected String getTableName() {
		return ArticleLinksEntry.TABLE_NAME;
	}

	@Override
	protected String getIdColumnName() {
		return ArticleLinksEntry._ID;
	}

	@Override
	protected String getKeyColumnName() {
		return ArticleLinksEntry._ID;
	}

	@Override
	protected SQLiteType getKeyColumnType() {
		return SQLiteType.INTEGER;
	}

	@Override
	protected String[] getProjectionAll() {
		return projectionAll;
	}

	public Collection<Link> readForArticle(SQLiteDatabase db, long articleId) {
		List<Link> result = new ArrayList<Link>();
		
		String[] keyWhereArgs = { String.valueOf(articleId) };
		String keyWhere = ArticleLinksEntry.COLUMN_NAME_ARTICLE_ID + " = ?";
		
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
