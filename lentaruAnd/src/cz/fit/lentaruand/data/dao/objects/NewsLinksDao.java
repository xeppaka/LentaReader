package cz.fit.lentaruand.data.dao.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.LruCache;
import cz.fit.lentaruand.data.Link;
import cz.fit.lentaruand.data.dao.async.AsyncDao;
import cz.fit.lentaruand.data.dao.decorators.AsyncDaoDecorator;
import cz.fit.lentaruand.data.dao.decorators.CachedDaoDecorator;
import cz.fit.lentaruand.data.dao.decorators.SynchronizedDaoDecorator;
import cz.fit.lentaruand.data.db.NewsLinksEntry;
import cz.fit.lentaruand.data.db.SQLiteType;
import cz.fit.lentaruand.data.provider.LentaProvider;
import cz.fit.lentaruand.utils.LentaConstants;

public class NewsLinksDao {
	private static final int CACHE_MAX_OBJECTS = LentaConstants.DAO_CACHE_MAX_OBJECTS;
	
	private static final LruCache<Long, Link> cacheId = new LruCache<Long, Link>(CACHE_MAX_OBJECTS);
	private static final Object sync = new Object();
	
	public final static AsyncDao<Link> getInstance(ContentResolver contentResolver) {
		if (contentResolver == null) {
			throw new IllegalArgumentException("contentResolver is null.");
		}
		
		return new AsyncDaoDecorator<Link>(new SynchronizedDaoDecorator<Link>(new CachedDaoDecorator<Link>(new ContentResolverNewsLinksDao(contentResolver), cacheId), sync));
	}

	private NewsLinksDao() {
	}
	
	private static class ContentResolverNewsLinksDao extends ContentResolverDao<Link> {
		private static final String[] projectionAll = {
			NewsLinksEntry._ID,
			NewsLinksEntry.COLUMN_NAME_NEWS_ID,
			NewsLinksEntry.COLUMN_NAME_TITLE,
			NewsLinksEntry.COLUMN_NAME_URL,
			NewsLinksEntry.COLUMN_NAME_DATE
		};
		
		public ContentResolverNewsLinksDao(ContentResolver cr) {
			super(cr);
		}

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
		protected Link createDataObject(Cursor cur) {
			long id = cur.getInt(cur.getColumnIndexOrThrow(NewsLinksEntry._ID));
			long newsId = cur.getInt(cur.getColumnIndexOrThrow(NewsLinksEntry.COLUMN_NAME_NEWS_ID));
			String title = cur.getString(cur.getColumnIndexOrThrow(NewsLinksEntry.COLUMN_NAME_TITLE));
			String url = cur.getString(cur.getColumnIndexOrThrow(NewsLinksEntry.COLUMN_NAME_URL));
			Date date = new Date(cur.getLong(cur.getColumnIndexOrThrow(NewsLinksEntry.COLUMN_NAME_DATE)));
			
			return new Link(id, newsId, url, title, date);
		}
	
		@Override
		protected Uri getContentProviderUri() {
			return LentaProvider.CONTENT_URI_LINKS;
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
		
		public Collection<Link> readForParentObject(long newsId) {
			List<Link> result = new ArrayList<Link>();
			
			String[] keyWhereArgs = { String.valueOf(newsId) };
			String keyWhere = NewsLinksEntry.COLUMN_NAME_NEWS_ID + " = ?";
			
			Cursor cur = getContentResolver().query(
					getContentProviderUri(), 
					getProjectionAll(),
					keyWhere,
					keyWhereArgs, 
					null
					);
			
			try {
				if (cur.moveToFirst()) {
					do {
						result.add(createDataObject(cur));
					} while (cur.moveToNext());
				}
				
				return result;
			} finally {
				cur.close();
			}
		}
	}
}
