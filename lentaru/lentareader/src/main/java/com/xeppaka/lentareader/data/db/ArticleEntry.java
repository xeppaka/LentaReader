package com.xeppaka.lentareader.data.db;

public abstract class ArticleEntry extends NewsObjectEntry {
	private ArticleEntry() {}

	public static final String TABLE_NAME = "Article";
	
	public static final String COLUMN_NAME_SECOND_TITLE = "secondtitle";
	public static final String COLUMN_NAME_AUTHOR = "author";

	public static final String SQL_CREATE_TABLE_COLUMNS = NewsObjectEntry.SQL_CREATE_TABLE_COLUMNS + ", " +
			COLUMN_NAME_AUTHOR + " TEXT, " +
			COLUMN_NAME_SECOND_TITLE + " TEXT";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + SQL_CREATE_TABLE_COLUMNS + ")";
	public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
