package cz.fit.lentaruand.data.db;

import android.provider.BaseColumns;

public abstract class ArticleLinksEntry implements BaseColumns {
	private ArticleLinksEntry() {}

	public static final String TABLE_NAME = "ArticleLinks";
	
	public static final String COLUMN_NAME_ARTICLE_ID = "articleid";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_URL = "url";
	public static final String COLUMN_NAME_DATE = "date";
	
	public static final String SQL_CREATE_TABLE_COLUMNS = _ID + " INTEGER PRIMARY KEY, " +
			COLUMN_NAME_ARTICLE_ID + " INTEGER, " +
			COLUMN_NAME_TITLE + " TEXT, " +
			COLUMN_NAME_URL + " TEXT, " +
			COLUMN_NAME_DATE + " TEXT, FOREIGN KEY(" + COLUMN_NAME_ARTICLE_ID + 
				") REFERENCES " + TABLE_NAME + "(" + _ID + ") ON DELETE CASCADE";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + SQL_CREATE_TABLE_COLUMNS + ")";
	public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
