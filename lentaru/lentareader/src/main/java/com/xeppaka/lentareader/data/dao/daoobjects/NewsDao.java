package com.xeppaka.lentareader.data.dao.daoobjects;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.EmptyBody;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.decorators.AsyncNODaoDecorator;
import com.xeppaka.lentareader.data.db.NewsEntry;
import com.xeppaka.lentareader.data.db.SQLiteType;
import com.xeppaka.lentareader.data.provider.LentaProvider;
import com.xeppaka.lentareader.parser.convertednews.ConvertedBodyParser;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public final class NewsDao {
    private static final ConvertedBodyParser bodyParser = new ConvertedBodyParser();

    public final static AsyncNODao<News> getInstance(ContentResolver contentResolver) {
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
                NewsEntry.COLUMN_NAME_READ,
                NewsEntry.COLUMN_NAME_UPDATED_FROM_LATEST,
                NewsEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND,
                NewsEntry.COLUMN_NAME_RECENT,
                NewsEntry.COLUMN_NAME_BODY
		};

        private static final String[] projectionBrief = {
                NewsEntry._ID,
                NewsEntry.COLUMN_NAME_GUID,
                NewsEntry.COLUMN_NAME_TITLE,
                NewsEntry.COLUMN_NAME_LINK,
                NewsEntry.COLUMN_NAME_PUBDATE,
                NewsEntry.COLUMN_NAME_IMAGELINK,
                NewsEntry.COLUMN_NAME_READ,
                NewsEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND,
                NewsEntry.COLUMN_NAME_RECENT
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
            values.put(NewsEntry.COLUMN_NAME_READ, news.isRead());
            values.put(NewsEntry.COLUMN_NAME_UPDATED_FROM_LATEST, news.isUpdatedFromLatest());
            values.put(NewsEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND, news.isUpdatedInBackground());
            values.put(NewsEntry.COLUMN_NAME_RECENT, news.isRecent());

			if (news.getBody() == null)
				values.putNull(NewsEntry.COLUMN_NAME_BODY);
			else
				values.put(NewsEntry.COLUMN_NAME_BODY, news.getBody().toXml());
			
			return values;
		}
		
		@Override
		protected News createDataObject(Cursor cur) {
            int colIndex;

            long id = (colIndex = cur.getColumnIndex(NewsEntry._ID)) >= 0 ? cur.getLong(colIndex) : -1;
            String guid = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_GUID)) >= 0 ? cur.getString(colIndex) : null;
            String title = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_TITLE)) >= 0 ? cur.getString(colIndex) : null;
            String link = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_LINK)) >= 0 ? cur.getString(colIndex) : null;
            String imageLink = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_IMAGELINK)) >= 0 ? cur.getString(colIndex) : null;
            String imageCaption = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_IMAGECAPTION)) >= 0 ? cur.getString(colIndex) : null;
            String imageCredits = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_IMAGECREDITS)) >= 0 ? cur.getString(colIndex) : null;
            Date pubDate = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_PUBDATE)) >= 0 ? new Date(cur.getLong(colIndex)) : null;
            Rubrics rubric = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_RUBRIC)) >= 0 ? Rubrics.valueOf(cur.getString(colIndex)) : null;
            String description = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_DESCRIPTION)) >= 0 ? cur.getString(colIndex) : null;
            boolean read = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_READ)) >= 0 && cur.getInt(colIndex) > 0;
            boolean updatedFromLatest = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_UPDATED_FROM_LATEST)) >= 0 && cur.getInt(colIndex) > 0;
            boolean updatedInBackground = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND)) >= 0 && cur.getInt(colIndex) > 0;
            boolean recent = (colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_RECENT)) >= 0 && cur.getInt(colIndex) > 0;

            Body body;

            try {
                colIndex = cur.getColumnIndex(NewsEntry.COLUMN_NAME_BODY);

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

            return new News(id, guid, title, link, pubDate, imageLink, imageCaption, imageCredits, rubric, description,
                    read, updatedFromLatest, updatedInBackground, recent, body);
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

        @Override
		public List<News> read() {
//			List<News> news = super.read();
//
//			Collections.sort(news);
//
//			return news;
            throw new UnsupportedOperationException("Should not be used from NewsDao.");
		}

//        @Override
//        public List<News> readBrief(Rubrics rubric) {
//            List<News> news = super.readBrief(rubric);
//
//            Collections.sort(news);
//
//            return news;
//        }

        @Override
		public List<News> readForParentObject(long parentId) {
			return null;
		}

//		@Override
//		public List<News> read(Rubrics rubric) {
//			List<News> news = super.read(rubric);
//
//			Collections.sort(news);
//
//			return news;
//		}
	}
}
