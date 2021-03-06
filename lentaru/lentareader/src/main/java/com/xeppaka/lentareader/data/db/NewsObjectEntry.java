package com.xeppaka.lentareader.data.db;

import android.provider.BaseColumns;

public abstract class NewsObjectEntry implements BaseColumns {
	public static final String COLUMN_NAME_GUID = "guid";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_LINK = "link";
	public static final String COLUMN_NAME_PUBDATE = "pubdate";
	public static final String COLUMN_NAME_IMAGELINK = "imagelink";
    public static final String COLUMN_NAME_IMAGECAPTION = "imagecaption";
    public static final String COLUMN_NAME_IMAGECREDITS = "imagecredits";
	public static final String COLUMN_NAME_RUBRIC = "rubric";
    public static final String COLUMN_NAME_DESCRIPTION = "description";
    public static final String COLUMN_NAME_READ = "read";
    public static final String COLUMN_NAME_UPDATED_FROM_LATEST = "fromlatest";
    public static final String COLUMN_NAME_UPDATED_IN_BACKGROUND = "background";
    public static final String COLUMN_NAME_RECENT = "recent";
    public static final String COLUMN_NAME_BODY = "body";

    public static final String SQL_CREATE_TABLE_COLUMNS = _ID + " INTEGER PRIMARY KEY, " +
			COLUMN_NAME_GUID + " TEXT, " +
			COLUMN_NAME_TITLE + " TEXT, " +
			COLUMN_NAME_LINK + " TEXT, " +
			COLUMN_NAME_IMAGELINK + " TEXT, " +
            COLUMN_NAME_IMAGECAPTION + " TEXT, " +
            COLUMN_NAME_IMAGECREDITS + " TEXT, " +
			COLUMN_NAME_PUBDATE + " INTEGER, " +
			COLUMN_NAME_RUBRIC + " TEXT, " +
            COLUMN_NAME_DESCRIPTION + " TEXT, " +
            COLUMN_NAME_READ + " INTEGER," +
            COLUMN_NAME_UPDATED_FROM_LATEST + " INTEGER," +
            COLUMN_NAME_UPDATED_IN_BACKGROUND + " INTEGER," +
            COLUMN_NAME_RECENT + " INTEGER," +
            COLUMN_NAME_BODY + " TEXT";
}
