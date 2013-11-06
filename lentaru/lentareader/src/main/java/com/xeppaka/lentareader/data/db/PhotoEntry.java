package com.xeppaka.lentareader.data.db;


public abstract class PhotoEntry extends NewsObjectEntry {
	private PhotoEntry() {}

	public static final String TABLE_NAME = "Photo";
	
	public static final String COLUMN_NAME_SECOND_TITLE = "secondtitle";

	public static final String SQL_CREATE_TABLE_COLUMNS = NewsObjectEntry.SQL_CREATE_TABLE_COLUMNS + ", " +
			COLUMN_NAME_SECOND_TITLE + " TEXT";

	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + SQL_CREATE_TABLE_COLUMNS + ")";
	public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
