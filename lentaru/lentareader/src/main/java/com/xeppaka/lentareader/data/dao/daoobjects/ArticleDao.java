package com.xeppaka.lentareader.data.dao.daoobjects;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.EmptyBody;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.decorators.AsyncNODaoDecorator;
import com.xeppaka.lentareader.data.db.ArticleEntry;
import com.xeppaka.lentareader.data.db.SQLiteType;
import com.xeppaka.lentareader.data.provider.LentaProvider;
import com.xeppaka.lentareader.parser.convertednews.ConvertedBodyParser;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public final class ArticleDao {
    private static final ConvertedBodyParser bodyParser = new ConvertedBodyParser();

    public final static AsyncNODao<Article> getInstance(ContentResolver contentResolver) {
        return new AsyncNODaoDecorator<Article>(new ContentResolverArticleDao(contentResolver));
    }

    private ArticleDao() {
    }

    private static class ContentResolverArticleDao extends ContentResolverNODao<Article> {
        private static final String[] projectionAll = {
                ArticleEntry._ID,
                ArticleEntry.COLUMN_NAME_GUID,
                ArticleEntry.COLUMN_NAME_TITLE,
                ArticleEntry.COLUMN_NAME_LINK,
                ArticleEntry.COLUMN_NAME_IMAGELINK,
                ArticleEntry.COLUMN_NAME_IMAGECAPTION,
                ArticleEntry.COLUMN_NAME_IMAGECREDITS,
                ArticleEntry.COLUMN_NAME_PUBDATE,
                ArticleEntry.COLUMN_NAME_RUBRIC,
                ArticleEntry.COLUMN_NAME_DESCRIPTION,
                ArticleEntry.COLUMN_NAME_AUTHOR,
                ArticleEntry.COLUMN_NAME_SECOND_TITLE,
                ArticleEntry.COLUMN_NAME_READ,
                ArticleEntry.COLUMN_NAME_UPDATED_FROM_LATEST,
                ArticleEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND,
                ArticleEntry.COLUMN_NAME_RECENT,
                ArticleEntry.COLUMN_NAME_BODY
        };

        private static final String[] projectionBrief = {
                ArticleEntry._ID,
                ArticleEntry.COLUMN_NAME_GUID,
                ArticleEntry.COLUMN_NAME_TITLE,
                ArticleEntry.COLUMN_NAME_LINK,
                ArticleEntry.COLUMN_NAME_IMAGELINK,
                ArticleEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND,
                ArticleEntry.COLUMN_NAME_RECENT
        };

        public ContentResolverArticleDao(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected ContentValues prepareContentValues(Article article) {
            ContentValues values = new ContentValues();

            values.put(ArticleEntry.COLUMN_NAME_GUID, article.getGuid());
            values.put(ArticleEntry.COLUMN_NAME_TITLE, article.getTitle());
            values.put(ArticleEntry.COLUMN_NAME_LINK, article.getLink());

            if (article.getImageLink() == null)
                values.putNull(ArticleEntry.COLUMN_NAME_IMAGELINK);
            else
                values.put(ArticleEntry.COLUMN_NAME_IMAGELINK, article.getImageLink());

            if (article.getImageCaption() == null)
                values.putNull(ArticleEntry.COLUMN_NAME_IMAGECAPTION);
            else
                values.put(ArticleEntry.COLUMN_NAME_IMAGECAPTION, article.getImageCaption());

            if (article.getImageCredits() == null)
                values.putNull(ArticleEntry.COLUMN_NAME_IMAGECREDITS);
            else
                values.put(ArticleEntry.COLUMN_NAME_IMAGECREDITS, article.getImageCredits());

            values.put(ArticleEntry.COLUMN_NAME_PUBDATE, article.getPubDate().getTime());
            values.put(ArticleEntry.COLUMN_NAME_RUBRIC, article.getRubric().name());
            values.put(ArticleEntry.COLUMN_NAME_DESCRIPTION, article.getDescription());
            values.put(ArticleEntry.COLUMN_NAME_READ, article.isRead());
            values.put(ArticleEntry.COLUMN_NAME_UPDATED_FROM_LATEST, article.isUpdatedFromLatest());
            values.put(ArticleEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND, article.isUpdatedInBackground());
            values.put(ArticleEntry.COLUMN_NAME_RECENT, article.isRecent());
            values.put(ArticleEntry.COLUMN_NAME_AUTHOR, article.getAuthor());
            values.put(ArticleEntry.COLUMN_NAME_SECOND_TITLE, article.getSecondTitle());

            if (article.getBody() == null)
                values.putNull(ArticleEntry.COLUMN_NAME_BODY);
            else
                values.put(ArticleEntry.COLUMN_NAME_BODY, article.getBody().toXml());

            return values;
        }

        @Override
        protected Article createDataObject(Cursor cur) {
            int colIndex;

            long id = (colIndex = cur.getColumnIndex(ArticleEntry._ID)) > 0 ? cur.getLong(colIndex) : -1;
            String guid = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_GUID)) > 0 ? cur.getString(colIndex) : null;
            String title = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_TITLE)) > 0 ? cur.getString(colIndex) : null;
            String link = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_TITLE)) > 0 ? cur.getString(colIndex) : null;
            String imageLink = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_IMAGELINK)) > 0 ? cur.getString(colIndex) : null;
            String imageCaption = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_IMAGECAPTION)) > 0 ? cur.getString(colIndex) : null;
            String imageCredits = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_IMAGECREDITS)) > 0 ? cur.getString(colIndex) : null;
            Date pubDate = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_PUBDATE)) > 0 ? new Date(cur.getLong(colIndex)) : null;
            Rubrics rubric = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_RUBRIC)) > 0 ? Rubrics.valueOf(cur.getString(colIndex)) : null;
            String description = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_DESCRIPTION)) > 0 ? cur.getString(colIndex) : null;
            String secondTitle = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_SECOND_TITLE)) > 0 ? cur.getString(colIndex) : null;
            String author = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_AUTHOR)) > 0 ? cur.getString(colIndex) : null;
            boolean read = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_READ)) > 0 && cur.getInt(colIndex) > 0;
            boolean updatedFromLatest = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_UPDATED_FROM_LATEST)) > 0 && cur.getInt(colIndex) > 0;
            boolean updatedInBackground = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND)) > 0 && cur.getInt(colIndex) > 0;
            boolean recent = (colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_RECENT)) > 0 && cur.getInt(colIndex) > 0;

            Body body;

            try {
                colIndex = cur.getColumnIndex(ArticleEntry.COLUMN_NAME_BODY);

                if (colIndex >= 0) {
                    body = bodyParser.parse(cur.getString(colIndex));
                } else {
                    body = EmptyBody.getInstance();
                }
            } catch (XmlPullParserException e) {
                Log.e(LentaConstants.LoggerAnyTag, "Error occured while parsing body of article with id = " + id, e);
                body = EmptyBody.getInstance();
            } catch (IOException e) {
                Log.e(LentaConstants.LoggerAnyTag, "Error occured while parsing body of article with id = " + id, e);
                body = EmptyBody.getInstance();
            }

            return new Article(id, guid, title, link, pubDate, imageLink, imageCaption, imageCredits, rubric, description,
                    secondTitle, author, read, updatedFromLatest, updatedInBackground, recent, body);
        }

        @Override
        protected Uri getContentProviderUri() {
            return LentaProvider.CONTENT_URI_ARTICLE;
        }

        @Override
        protected String getIdColumnName() {
            return ArticleEntry._ID;
        }

        @Override
        protected String getKeyColumnName() {
            return ArticleEntry.COLUMN_NAME_GUID;
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
        public List<Article> read() {
//			List<News> news = super.read();
//
//			Collections.sort(news);
//
//			return news;
            throw new UnsupportedOperationException("Should not be used from ArticleDao.");
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
        public List<Article> readForParentObject(long parentId) {
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
