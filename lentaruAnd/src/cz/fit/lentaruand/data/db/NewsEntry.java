package cz.fit.lentaruand.data.db;


public abstract class NewsEntry extends NewsObjectEntry {
	private NewsEntry() {}

	public static final String TABLE_NAME = "News";
	
	public static final String COLUMN_NAME_BRIEFTEXT = "brieftext";
	public static final String COLUMN_NAME_FULLTEXT = "fulltext";
	public static final String COLUMN_NAME_IMAGECAPTION = "imagecaption";
	public static final String COLUMN_NAME_IMAGECREDITS = "imagecredits";
	
	public static final String SQL_CREATE_TABLE_COLUMNS = NewsObjectEntry.SQL_CREATE_TABLE_COLUMNS + ", " +
			COLUMN_NAME_IMAGECAPTION + " TEXT, " +
			COLUMN_NAME_IMAGECREDITS + " TEXT, " +
			COLUMN_NAME_BRIEFTEXT + " TEXT, " +
			COLUMN_NAME_FULLTEXT + " TEXT";
	public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + SQL_CREATE_TABLE_COLUMNS + ")";
	public static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}