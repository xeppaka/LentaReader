package com.xeppaka.lentareader.data.db;

import android.provider.BaseColumns;

public abstract class PhotoImageEntry implements BaseColumns {
	private PhotoImageEntry() {}

	public static final String TABLE_NAME = "PhotoImage";
	
	public static final String COLUMN_NAME_PHOTO_ID = "photoid";
	public static final String COLUMN_NAME_INDEX = "idx";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_URL = "url";
	public static final String COLUMN_NAME_CREDITS = "credits";
	public static final String COLUMN_NAME_DESCRIPTION = "description";
	
	public static final String SQL_CREATE_TABLE_COLUMNS = _ID + " INTEGER PRIMARY KEY, " +
			COLUMN_NAME_PHOTO_ID + " INTEGER, " +
			COLUMN_NAME_INDEX + " INTEGER, " +
			COLUMN_NAME_TITLE + " TEXT, " +
			COLUMN_NAME_URL + " TEXT, " +
			COLUMN_NAME_CREDITS + " TEXT, " +
			COLUMN_NAME_DESCRIPTION + " TEXT, FOREIGN KEY(" + COLUMN_NAME_PHOTO_ID + 
				") REFERENCES " + PhotoEntry.TABLE_NAME + "(" + PhotoEntry._ID + ") ON DELETE CASCADE";
	
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + SQL_CREATE_TABLE_COLUMNS + ")";
	public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}
