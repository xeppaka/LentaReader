package com.xeppaka.lentareader.data.dao.daoobjects;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.EmptyBody;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.decorators.AsyncNODaoDecorator;
import com.xeppaka.lentareader.data.dao.decorators.CachedNODaoDecorator;
import com.xeppaka.lentareader.data.dao.decorators.SynchronizedNODaoDecorator;
import com.xeppaka.lentareader.data.db.NewsEntry;
import com.xeppaka.lentareader.data.db.SQLiteType;
import com.xeppaka.lentareader.data.provider.LentaProvider;
import com.xeppaka.lentareader.parser.convertednews.ConvertedBodyParser;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

public final class NewsDao {
	private static final int CACHE_MAX_OBJECTS = LentaConstants.DAO_CACHE_MAX_OBJECTS;
	
//	private static final LruCache<Long, News> cacheId = new LruCache<Long, News>(CACHE_MAX_OBJECTS);
//	private static final Object sync = new Object();

    private static final ConvertedBodyParser bodyParser = new ConvertedBodyParser();

    public final static AsyncNODao<News> getInstance(ContentResolver contentResolver) {
		// return new AsyncNODaoDecorator<News>(new SynchronizedNODaoDecorator<News>(new CachedNODaoDecorator<News>(new ContentResolverNewsDao(contentResolver), cacheId), sync));
        return new AsyncNODaoDecorator<News>(new ContentResolverNewsDao(contentResolver));
	}

	private NewsDao() {
	}
	
	private static class ContentResolverNewsDao extends ContentResolverNODao<News> {
		private static final String[] projectionAll = {
			NewsEntry._ID,
			NewsEntry.COLUMN_NAME_GUID,
			NewsEntry.COLUMN_NAME_TITLE,
			NewsEntry.COLUMN_NAME_LINK,
			NewsEntry.COLUMN_NAME_IMAGELINK,
			NewsEntry.COLUMN_NAME_IMAGECAPTION,
			NewsEntry.COLUMN_NAME_IMAGECREDITS,
			NewsEntry.COLUMN_NAME_PUBDATE,
			NewsEntry.COLUMN_NAME_RUBRIC,
			NewsEntry.COLUMN_NAME_DESCRIPTION,
            NewsEntry.COLUMN_NAME_LATEST_NEWS,
            NewsEntry.COLUMN_NAME_READ,
			NewsEntry.COLUMN_NAME_BODY
		};

        private static final String[] projectionBrief = {
                NewsEntry._ID,
                NewsEntry.COLUMN_NAME_GUID,
                NewsEntry.COLUMN_NAME_TITLE,
                NewsEntry.COLUMN_NAME_LINK,
                NewsEntry.COLUMN_NAME_IMAGELINK,
                NewsEntry.COLUMN_NAME_IMAGECAPTION,
                NewsEntry.COLUMN_NAME_IMAGECREDITS,
                NewsEntry.COLUMN_NAME_PUBDATE,
                NewsEntry.COLUMN_NAME_RUBRIC,
                NewsEntry.COLUMN_NAME_DESCRIPTION,
                NewsEntry.COLUMN_NAME_LATEST_NEWS,
                NewsEntry.COLUMN_NAME_READ
        };
		
		public ContentResolverNewsDao(ContentResolver cr) {
			super(cr);
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
			values.put(NewsEntry.COLUMN_NAME_DESCRIPTION, news.getDescription());
            values.put(NewsEntry.COLUMN_NAME_LATEST_NEWS, news.isLatest());
            values.put(NewsEntry.COLUMN_NAME_READ, news.isRead());

			if (news.getBody() == null)
				values.putNull(NewsEntry.COLUMN_NAME_BODY);
			else
				values.put(NewsEntry.COLUMN_NAME_BODY, news.getBody().toXml());
			
			return values;
		}
		
		@Override
		protected News createDataObject(Cursor cur) {
			long id = cur.getLong(cur.getColumnIndexOrThrow(NewsEntry._ID));
			String guidDb = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_GUID));
			String title = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_TITLE));
			String link = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_LINK));
			String imageLink = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGELINK));
			String imageCaption = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGECAPTION));
			String imageCredits = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGECREDITS));
			Date pubDate = new Date(cur.getLong(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_PUBDATE)));
			Rubrics rubric = Rubrics.valueOf(cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_RUBRIC)));
            boolean latest = cur.getInt(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_LATEST_NEWS)) > 0;
            boolean read = cur.getInt(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_READ)) > 0;
			String description = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_DESCRIPTION));
            Body body;

            try {
                final int colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_BODY);

                if (colIndex >= 0) {
    			    body = bodyParser.parse(cur.getString(colIndex));
                } else {
                    body = EmptyBody.getInstance();
                }
            } catch (XmlPullParserException e) {
                Log.e(LentaConstants.LoggerAnyTag, "Error occured while parsing body of news with id = " + id, e);
                body = EmptyBody.getInstance();
            } catch (IOException e) {
                Log.e(LentaConstants.LoggerAnyTag, "Error occured while parsing body of news with id = " + id, e);
                body = EmptyBody.getInstance();
            }

            return new News(id, guidDb, title, link, pubDate, imageLink, imageCaption, imageCredits, rubric, description, latest, read, body);
		}
	
		@Override
		protected Uri getContentProviderUri() {
			return LentaProvider.CONTENT_URI_NEWS;
		}
	
		@Override
		protected String getIdColumnName() {
			return NewsEntry._ID;
		}
	
		@Override
		protected String getKeyColumnName() {
			return NewsEntry.COLUMN_NAME_GUID;
		}
		
		@Override
		protected SQLiteType getKeyColumnType() {
			return SQLiteType.TEXT;
		}
	
		@Override
		protected String[] getProjectionAll() {
			return projectionAll;
		}

        @Override
        protected String[] getProjectionBrief() {
            return projectionBrief;
        }

//        @Override
//		public List<News> read() {
//			List<News> news;
//            news = super.read();
//
////				for (News n : news) {
////					readOtherNewsParts(n);
////				}
//
//			Collections.sort(news);
//
//			return news;
//		}
//
//		@Override
//		public News read(long id) {
//            News news = super.read(id);
//
//            if (news == null)
//                return news;
//
//            //readOtherNewsParts(news);
//
//            return news;
//		}
//
//		@Override
//		public News read(String key) {
//            News news = super.read(key);
//
//            if (news == null)
//                return news;
//
//            //readOtherNewsParts(news);
//
//            return news;
//		}
//
//		@Override
//		public News read(SQLiteType keyType,
//				String keyColumnName, String keyValue) {
//            News news = super.read(keyType, keyColumnName, keyValue);
//
//            if (news == null)
//                return news;
//
//            //readOtherNewsParts(news);
//
//            return news;
//		}
//
//		@Override
//		public int update(News news) {
//            int result = super.update(news);
//
//            //updateOtherNewsParts(news);
//
//            return result;
//		}
//
		@Override
		public List<News> readForParentObject(long parentId) {
			return null;
		}
//
//		@Override
//		public List<News> read(Rubrics rubric) {
//			List<News> news = super.read(rubric);
//
////			for (News n : news) {
////				readOtherNewsParts(n);
////			}
//
//			Collections.sort(news);
//
//			return news;
//		}

	}
}
