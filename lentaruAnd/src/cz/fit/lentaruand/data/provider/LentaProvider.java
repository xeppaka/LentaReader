package cz.fit.lentaruand.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import cz.fit.lentaruand.data.db.LentaDbHelper;
import cz.fit.lentaruand.data.db.NewsEntry;
import cz.fit.lentaruand.data.db.NewsLinksEntry;

public class LentaProvider extends ContentProvider {
	private static final String CONTENT_URI_STRING = "cz.fit.lentaruand.provider";
	private static final String PATH_NEWS = "news";
	private static final String PATH_NEWS_ID = "news/#";
	private static final String PATH_LINKS = "links";
	private static final String PATH_LINKS_ID = "links/#";
	
	private static final int NEWS = 1;
	private static final int NEWS_ID = 2;
	private static final int LINKS = 3;
	private static final int LINKS_ID = 4;
	
	private static final String MIME_NEWS_ITEM = "vnd.android.cursor.item/cz.fit.lentaruand.news";
	private static final String MIME_NEWS_DIR = "vnd.android.cursor.dir/cz.fit.lentaruand.news";
	private static final String MIME_LINKS_ITEM = "vnd.android.cursor.item/cz.fit.lentaruand.links";
	private static final String MIME_LINKS_DIR = "vnd.android.cursor.dir/cz.fit.lentaruand.links";
	
	private static final UriMatcher uriMatcher;
	private LentaDbHelper dbHelper;
	
	public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority(CONTENT_URI_STRING).build();
	public static final Uri CONTENT_URI_NEWS = CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();
	public static final Uri CONTENT_URI_LINKS = CONTENT_URI.buildUpon().appendPath(PATH_LINKS).build();

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(CONTENT_URI_STRING, PATH_NEWS, NEWS);
		uriMatcher.addURI(CONTENT_URI_STRING, PATH_NEWS_ID, NEWS_ID);
		uriMatcher.addURI(CONTENT_URI_STRING, PATH_LINKS, LINKS);
		uriMatcher.addURI(CONTENT_URI_STRING, PATH_LINKS_ID, LINKS_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return deleteTable(NewsEntry.TABLE_NAME, selection, selectionArgs);
		case NEWS_ID: {
			String idSelection = NewsEntry._ID + " = ?";
			String[] idSelectionArgs = {String.valueOf(ContentUris.parseId(uri))};
			return deleteTable(NewsEntry.TABLE_NAME, idSelection, idSelectionArgs);
			}
		case LINKS:
			return deleteTable(NewsLinksEntry.TABLE_NAME, selection, selectionArgs);
		case LINKS_ID: {
			String idSelection = NewsLinksEntry._ID + " = ?";
			String[] idSelectionArgs = {String.valueOf(ContentUris.parseId(uri))};
			return deleteTable(NewsLinksEntry.TABLE_NAME, idSelection, idSelectionArgs);
			}
		}
		
		throw new IllegalArgumentException("Unsupported uri type: " + uri);
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return MIME_NEWS_DIR;
		case NEWS_ID:
			return MIME_NEWS_ITEM;
		case LINKS:
			return MIME_LINKS_DIR;
		case LINKS_ID:
			return MIME_LINKS_ITEM;
		}
		
		throw new IllegalArgumentException("Unsupported uri type: " + uri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return insertTable(NewsEntry.TABLE_NAME, uri, values);
		case LINKS:
			return insertTable(NewsLinksEntry.TABLE_NAME, uri, values);
		}

		throw new IllegalArgumentException("Unsupported uri type: " + uri);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new LentaDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return readTable(NewsEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
		case NEWS_ID: {
			String idSelection = NewsEntry._ID + " = ?";
			String[] idSelectionArgs = {uri.getLastPathSegment()};
			return readTable(NewsEntry.TABLE_NAME, projection, idSelection, idSelectionArgs, sortOrder);
			}
		case LINKS:
			return readTable(NewsLinksEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
		case LINKS_ID: {
			String idSelection = NewsLinksEntry._ID + " = ?";
			String[] idSelectionArgs = {uri.getLastPathSegment()};
			return readTable(NewsLinksEntry.TABLE_NAME, projection, idSelection, idSelectionArgs, sortOrder);
			}
		}
		
		throw new IllegalArgumentException("Unsupported uri type: " + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return updateTable(NewsEntry.TABLE_NAME, uri, values, selection, selectionArgs);
		case NEWS_ID: {
			String idSelection = NewsEntry._ID + " = ?";
			String[] idSelectionArgs = {uri.getLastPathSegment()};
			return updateTable(NewsEntry.TABLE_NAME, uri, values, idSelection, idSelectionArgs);
			}
		case LINKS:
			return updateTable(NewsLinksEntry.TABLE_NAME, uri, values, selection, selectionArgs);
		case LINKS_ID: {
			String idSelection = NewsEntry._ID + " = ?";
			String[] idSelectionArgs = {uri.getLastPathSegment()};
			return updateTable(NewsLinksEntry.TABLE_NAME, uri, values, idSelection, idSelectionArgs);
			}
		}
		
		throw new IllegalArgumentException("Unsupported uri type: " + uri);
	}
	
	private Cursor readTable(String tableName, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(tableName, projection, selection,
				selectionArgs, null, null, sortOrder);
	}
	
	private int deleteTable(String tableName, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.delete(tableName, selection, selectionArgs);
	}
	
	private Uri insertTable(String tableName, Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = db.insert(tableName, null, values);
		
		return ContentUris.withAppendedId(uri, id);
	}
	
	private int updateTable(String tableName, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.update(tableName, values, selection, selectionArgs);
	}
	
//	/******* NEWS *******/
//	private Cursor readNews(String[] projection, String selection,
//			String[] selectionArgs, String sortOrder) {
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		return db.query(NewsEntry.TABLE_NAME, projection, selection,
//				selectionArgs, null, null, sortOrder);
//	}
//
//	private int deleteNews(String selection, String[] selectionArgs) {
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		return db.delete(NewsEntry.TABLE_NAME, selection, selectionArgs);
//	}
//	
//	private Uri insertNews(Uri uri, ContentValues values) {
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		long id = db.insert(NewsEntry.TABLE_NAME, null, values);
//		
//		return ContentUris.withAppendedId(uri, id);
//	}
//	
//	private int updateNews(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		return db.update(NewsEntry.TABLE_NAME, values, selection, selectionArgs);
//	}
//	
//	/******* LINKS *******/
//	private Cursor readLinks(String[] projection, String selection,
//			String[] selectionArgs, String sortOrder) {
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		return db.query(NewsLinksEntry.TABLE_NAME, projection, selection,
//				selectionArgs, null, null, sortOrder);
//	}
//
//	private int deleteLinks(String selection, String[] selectionArgs) {
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		return db.delete(NewsLinksEntry.TABLE_NAME, selection, selectionArgs);
//	}
//	
//	private Uri insertLinks(Uri uri, ContentValues values) {
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		long id = db.insert(NewsEntry.TABLE_NAME, null, values);
//		
//		return ContentUris.withAppendedId(uri, id);
//	}
//	
//	private int updateLinks(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		return db.update(NewsEntry.TABLE_NAME, values, selection, selectionArgs);
//	}
}
