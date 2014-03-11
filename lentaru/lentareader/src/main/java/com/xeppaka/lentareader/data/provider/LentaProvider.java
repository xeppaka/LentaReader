package com.xeppaka.lentareader.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.xeppaka.lentareader.data.db.ArticleEntry;
import com.xeppaka.lentareader.data.db.LentaDbHelper;
import com.xeppaka.lentareader.data.db.NewsEntry;

import java.io.File;
import java.io.FileNotFoundException;

public class LentaProvider extends ContentProvider {
	private static final String CONTENT_URI_STRING = "com.xeppaka.lentareader.provider";
	private static final String PATH_NEWS = "news";
	private static final String PATH_NEWS_ID = "news/#";
    private static final String PATH_ARTICLE = "article";
    private static final String PATH_ARTICLE_ID = "article/#";

	private static final int NEWS = 1;
	private static final int NEWS_ID = 2;
    private static final int ARTICLE = 3;
    private static final int ARTICLE_ID = 4;

	private static final String MIME_NEWS_ITEM = "vnd.android.cursor.item/com.xeppaka.lentareader.news";
	private static final String MIME_NEWS_DIR = "vnd.android.cursor.dir/com.xeppaka.lentareader.news";
    private static final String MIME_ARTICLE_ITEM = "vnd.android.cursor.item/com.xeppaka.lentareader.article";
    private static final String MIME_ARTICLE_DIR = "vnd.android.cursor.dir/com.xeppaka.lentareader.article";

	private static final UriMatcher uriMatcher;
	
	public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority(CONTENT_URI_STRING).build();
	public static final Uri CONTENT_URI_NEWS = CONTENT_URI.buildUpon().appendPath(PATH_NEWS).build();
    public static final Uri CONTENT_URI_ARTICLE = CONTENT_URI.buildUpon().appendPath(PATH_ARTICLE).build();

	private LentaDbHelper dbHelper;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(CONTENT_URI_STRING, PATH_NEWS, NEWS);
		uriMatcher.addURI(CONTENT_URI_STRING, PATH_NEWS_ID, NEWS_ID);
        uriMatcher.addURI(CONTENT_URI_STRING, PATH_ARTICLE, ARTICLE);
        uriMatcher.addURI(CONTENT_URI_STRING, PATH_ARTICLE_ID, ARTICLE_ID);
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return MIME_NEWS_DIR;
		case NEWS_ID:
			return MIME_NEWS_ITEM;
		case ARTICLE:
			return MIME_ARTICLE_DIR;
		case ARTICLE_ID:
			return MIME_ARTICLE_ITEM;
		}
		
		return null;
	}

	/**
	 * this method added in API level 11
	 */
//	@Override
//	public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
//		switch (uriMatcher.match(uri)) {
//		case CACHED_IMAGE:
//			return new String[]{MIME_CACHED_IMAGE_DIR};
//		case CACHED_IMAGE_ID:
//			return new String[]{MIME_CACHED_IMAGE_ITEM};
//		}
//		
//		return null;
//	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (uri == null) {
			throw new NullPointerException();
		}
		
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return deleteFromTable(NewsEntry.TABLE_NAME, selection, selectionArgs);
		case NEWS_ID: {
			String idSelection = NewsEntry._ID + " = ?";
			String[] idSelectionArgs = {String.valueOf(ContentUris.parseId(uri))};
			return deleteFromTable(NewsEntry.TABLE_NAME, idSelection, idSelectionArgs);
			}
		case ARTICLE:
            return deleteFromTable(ArticleEntry.TABLE_NAME, selection, selectionArgs);
		case ARTICLE_ID: {
            String idSelection = ArticleEntry._ID + " = ?";
            String[] idSelectionArgs = {String.valueOf(ContentUris.parseId(uri))};
            return deleteFromTable(ArticleEntry.TABLE_NAME, idSelection, idSelectionArgs);
            }
		}
		
		return 0;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return insertIntoTable(NewsEntry.TABLE_NAME, uri, values);
		case ARTICLE:
            return insertIntoTable(ArticleEntry.TABLE_NAME, uri, values);
		}

		return null;
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = new LentaDbHelper(context);

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (uriMatcher.match(uri)) {
		case NEWS:
			return readFromTable(NewsEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
		case NEWS_ID: {
			String idSelection = NewsEntry._ID + " = ?";
			String[] idSelectionArgs = {uri.getLastPathSegment()};
			return readFromTable(NewsEntry.TABLE_NAME, projection, idSelection, idSelectionArgs, sortOrder);
			}
		case ARTICLE:
            return readFromTable(ArticleEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
		case ARTICLE_ID: {
            String idSelection = ArticleEntry._ID + " = ?";
            String[] idSelectionArgs = {uri.getLastPathSegment()};
            return readFromTable(ArticleEntry.TABLE_NAME, projection, idSelection, idSelectionArgs, sortOrder);
        }
		}
		
		return null;
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
		case ARTICLE:
            return updateTable(ArticleEntry.TABLE_NAME, uri, values, selection, selectionArgs);
		case ARTICLE_ID: {
            String idSelection = ArticleEntry._ID + " = ?";
            String[] idSelectionArgs = {uri.getLastPathSegment()};
            return updateTable(ArticleEntry.TABLE_NAME, uri, values, idSelection, idSelectionArgs);
        }
		}
		
		return 0;
	}
	
	private Cursor readFromTable(String tableName, String[] projection, String selection,
                                 String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(tableName, projection, selection,
				selectionArgs, null, null, sortOrder);
	}
	
	private int deleteFromTable(String tableName, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.delete(tableName, selection, selectionArgs);
	}
	
	private Uri insertIntoTable(String tableName, Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = db.insert(tableName, null, values);
		
		return ContentUris.withAppendedId(uri, id);
	}
	
	private int updateTable(String tableName, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.update(tableName, values, selection, selectionArgs);
	}

	private int deleteImage(String name) {
		File cache = getContext().getExternalCacheDir();
		
		if (cache == null || !cache.isDirectory()) {
			return 0;
		}

		File fileToDelete = new File(cache, name);
		
		if (fileToDelete.delete()) {
			return 1;
		}
		
		return 0;
	}
	
	private int deleteAllImages() {
		File cache = getContext().getExternalCacheDir();
		
		if (cache == null || !cache.isDirectory()) {
			return 0;
		}
		
		File[] filesToDelete = cache.listFiles();
		
		if (filesToDelete == null) {
			return 0;
		}
		
		int filesDeleted = 0;
		
		for (File fileToDelete : filesToDelete) {
			if (fileToDelete.delete()) {
				++filesDeleted;
			}
		}
		
		return filesDeleted;
	}
	
	private int getIntModeFromStringMode(String mode) {
		if (mode == null || mode.isEmpty()) {
			return 0;
		}
		
		int intMode = 0;
		
		if (mode.indexOf('r') >= 0) {
			intMode |= ParcelFileDescriptor.MODE_READ_ONLY;
		}
		
		if (mode.indexOf('w') >= 0) {
			intMode |= ParcelFileDescriptor.MODE_WRITE_ONLY;
		}
		
		if (mode.indexOf('t') >= 0) {
			intMode |= ParcelFileDescriptor.MODE_CREATE;
		}
		
		return intMode;
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		if (mode == null || mode.isEmpty()) {
			throw new IllegalArgumentException("mode is null or empty.");
		}
		
		String fileName = uri.getLastPathSegment();
		
		if (fileName == null || fileName.isEmpty()) {
			throw new FileNotFoundException();
		}
		
//		String externalDriveState = Environment.getExternalStorageState();
		
//		if (!Environment.MEDIA_MOUNTED.equals(externalDriveState) || !(mode.indexOf('w') < 0 && Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalDriveState))) {
//			throw new FileNotFoundException();
//		}

		File cache = getContext().getExternalCacheDir();
		
		if (cache == null) {
			throw new FileNotFoundException("Could not able to open cache directory.");
		}
		
		File file = new File(cache, fileName);
		
//		if (file == null || !file.exists()) {
//			throw new FileNotFoundException("File " + cache + "/" + fileName + " is not found.");
//		}
		
		return ParcelFileDescriptor.open(file, getIntModeFromStringMode(mode));
	}
}
