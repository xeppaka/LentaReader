package cz.fit.lentaruand.data.db;

import android.provider.BaseColumns;

public abstract class NewsEntry implements BaseColumns {
	private NewsEntry() {}

	public static final String TABLE_NAME = "News";
	
	public static final String COLUMN_NAME_GUID = "guid";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_LINK = "link";
	public static final String COLUMN_NAME_BRIEFTEXT = "brieftext";
	public static final String COLUMN_NAME_FULLTEXT = "fulltext";
	public static final String COLUMN_NAME_PUBDATE = "pubdate";
	public static final String COLUMN_NAME_IMAGELINK = "imagelink";
	public static final String COLUMN_NAME_RUBRIC = "rubric";
	public static final String COLUMN_NAME_SUBRUBRIC = "subrubric";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
				_ID + " INTEGER PRIMARY KEY, " +
				COLUMN_NAME_GUID + " TEXT, " +
				COLUMN_NAME_TITLE + " TEXT, " +
				COLUMN_NAME_LINK + " TEXT, " +
				COLUMN_NAME_IMAGELINK + " TEXT, " +
				COLUMN_NAME_PUBDATE + " INTEGER, " +
				COLUMN_NAME_RUBRIC + " INTEGER, " +
				COLUMN_NAME_SUBRUBRIC + " INTEGER, " +
				COLUMN_NAME_BRIEFTEXT + " TEXT, " +
				COLUMN_NAME_FULLTEXT + " TEXT)";
	
	public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
