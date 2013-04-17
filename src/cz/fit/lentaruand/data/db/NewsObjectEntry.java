package cz.fit.lentaruand.data.db;

import android.provider.BaseColumns;

public abstract class NewsObjectEntry implements BaseColumns {
	public static final String COLUMN_NAME_GUID = "guid";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_LINK = "link";
	public static final String COLUMN_NAME_PUBDATE = "pubdate";
	public static final String COLUMN_NAME_IMAGELINK = "imagelink";
	public static final String COLUMN_NAME_RUBRIC = "rubric";
	public static final String COLUMN_NAME_RUBRIC_UPDATE = "rubric_update";
}
