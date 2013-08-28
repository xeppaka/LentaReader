package cz.fit.lentaruand.data.dao.objects;

import java.sql.Date;
import java.util.Collection;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import cz.fit.lentaruand.data.Article;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.db.ArticleEntry;
import cz.fit.lentaruand.data.db.NewsEntry;
import cz.fit.lentaruand.data.db.SQLiteType;

public class ArticleDao extends ContentResolverDao<Article> {
	private static final String[] projectionAll = {
		ArticleEntry._ID,
		ArticleEntry.COLUMN_NAME_GUID,
		ArticleEntry.COLUMN_NAME_TITLE,
		ArticleEntry.COLUMN_NAME_SECOND_TITLE,
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
	
	public ArticleDao(ContentResolver cr) {
		super(cr);
	}

	@Override
	protected ContentValues prepareContentValues(Article article) {
		ContentValues values = new ContentValues();
		
		values.put(ArticleEntry.COLUMN_NAME_GUID, article.getGuid());
		values.put(ArticleEntry.COLUMN_NAME_TITLE, article.getTitle());
		values.put(ArticleEntry.COLUMN_NAME_SECOND_TITLE, article.getSecondTitle());
		values.put(ArticleEntry.COLUMN_NAME_AUTHOR, article.getAuthor());
		values.put(ArticleEntry.COLUMN_NAME_LINK, article.getLink());
		
		if (article.getImageLink() == null)
			values.putNull(NewsEntry.COLUMN_NAME_IMAGELINK);
		else
			values.put(NewsEntry.COLUMN_NAME_IMAGELINK, article.getImageLink());
		
		if (article.getImageCaption() == null)
			values.putNull(NewsEntry.COLUMN_NAME_IMAGECAPTION);
		else
			values.put(NewsEntry.COLUMN_NAME_IMAGECAPTION, article.getImageCaption());
		
		if (article.getImageCredits() == null)
			values.putNull(NewsEntry.COLUMN_NAME_IMAGECREDITS);
		else
			values.put(NewsEntry.COLUMN_NAME_IMAGECREDITS, article.getImageCredits());
		
		values.put(NewsEntry.COLUMN_NAME_PUBDATE, article.getPubDate().getTime());
		values.put(NewsEntry.COLUMN_NAME_RUBRIC, article.getRubric().name());
		values.put(NewsEntry.COLUMN_NAME_BRIEFTEXT, article.getBriefText());
		values.put(NewsEntry.COLUMN_NAME_RUBRIC_UPDATE, article.isRubricUpdateNeed() ? 1 : 0);
		
		if (article.getFullText() == null)
			values.putNull(NewsEntry.COLUMN_NAME_FULLTEXT);
		else
			values.put(NewsEntry.COLUMN_NAME_FULLTEXT, article.getFullText());
		
		return values;
	}

	@Override
	protected Article createDataObject(Cursor cur) {
		long id = cur.getLong(cur.getColumnIndexOrThrow(ArticleEntry._ID));
		String guidDb = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_GUID));
		String title = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_TITLE));
		String secondTitle = cur.getString(cur.getColumnIndexOrThrow(ArticleEntry.COLUMN_NAME_SECOND_TITLE));
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
		
		return new Article(id, guidDb, title, secondTitle, author, link, briefText, fullText, pubDate, imageLink, imageCaption, imageCredits, null, rubric, rubricUpdateNeed);
	}

	@Override
	protected Uri getContentProviderUri() {
		// TODO: return proper Uri
		return null;
	}

	@Override
	protected String getKeyColumnName() {
		return ArticleEntry.COLUMN_NAME_GUID;
	}

	@Override
	protected String getIdColumnName() {
		return ArticleEntry._ID;
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
	public Collection<Article> readForParentObject(long parentId) {
		return null;
	}
}