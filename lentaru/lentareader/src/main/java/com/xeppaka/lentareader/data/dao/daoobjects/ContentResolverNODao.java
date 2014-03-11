package com.xeppaka.lentareader.data.dao.daoobjects;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.db.NewsEntry;
import com.xeppaka.lentareader.data.db.NewsObjectEntry;
import com.xeppaka.lentareader.data.db.SQLiteType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ContentResolverNODao<T extends NewsObject> extends ContentResolverDao<T> implements NODao<T> {

    private final static String[] projectionImage = { BaseColumns._ID, NewsObjectEntry.COLUMN_NAME_IMAGELINK };
    private final static ContentValues clearUpdatedFromLatestFlagValues;
    private final static ContentValues setUpdatedFromLatestFlagValues;
    private final static ContentValues clearUpdatedInBackgroundFlagValues;
    private final static ContentValues clearRecentFlagValues;

    private final static String sortOrder = NewsObjectEntry.COLUMN_NAME_PUBDATE + " DESC";

    static {
        clearUpdatedFromLatestFlagValues = new ContentValues();
        clearUpdatedFromLatestFlagValues.put(NewsEntry.COLUMN_NAME_UPDATED_FROM_LATEST, 0);
        setUpdatedFromLatestFlagValues = new ContentValues();
        setUpdatedFromLatestFlagValues.put(NewsEntry.COLUMN_NAME_UPDATED_FROM_LATEST, 1);
        clearUpdatedInBackgroundFlagValues = new ContentValues();
        clearUpdatedInBackgroundFlagValues.put(NewsEntry.COLUMN_NAME_UPDATED_IN_BACKGROUND, 0);
        clearRecentFlagValues = new ContentValues();
        clearRecentFlagValues.put(NewsEntry.COLUMN_NAME_RECENT, 0);
    }

	public ContentResolverNODao(ContentResolver cr) {
		super(cr);
	}

	@Override
	public List<T> read(Rubrics rubric) {
        return readWithProjection(rubric, getProjectionAll());
	}

    @Override
    public List<T> readBrief(Rubrics rubric) {
        return readWithProjection(rubric, getProjectionBrief());
    }

    private List<T> readWithProjection(Rubrics rubric, String[] projection) {
        Cursor cur;

        if (rubric != Rubrics.LATEST) {
            String where = getWhereFromSQLiteType(SQLiteType.TEXT);
            String[] whereArgs = { rubric.name() };

            cur = getContentResolver().query(getContentProviderUri(),
                    projection, String.format(where, getRubricColumnName()), whereArgs, NewsObjectEntry.COLUMN_NAME_PUBDATE + " desc");
        } else {
            cur = getContentResolver().query(getContentProviderUri(),
                    projection, null, null, NewsObjectEntry.COLUMN_NAME_PUBDATE + " desc");
        }

        if (cur == null) {
            return Collections.EMPTY_LIST;
        }

        try {
            List<T> result = new ArrayList<T>();

            if (cur.moveToFirst()) {
                do {
                    result.add(createDataObject(cur));
                } while (cur.moveToNext());
            }

            return result;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    @Override
    public T readLatest(Rubrics rubric) {
        Cursor cur;

        if (rubric != Rubrics.LATEST) {
            String where = getWhereFromSQLiteType(SQLiteType.TEXT, 1);
            String[] whereArgs = { rubric.name() };

            cur = getContentResolver().query(getContentProviderUri(),
                    getProjectionAll(), String.format(where, getRubricColumnName()), whereArgs, getSortOrder() + " limit 1");
        } else {
            cur = getContentResolver().query(getContentProviderUri(),
                    getProjectionAll(), null, null, getSortOrder() + " limit 1");
        }

        if (cur == null) {
            return null;
        }

        try {
            if (cur.moveToFirst()) {
                return createDataObject(cur);
            }

            return null;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    @Override
    public int deleteOlderOrEqual(Rubrics rubric, long date) {
        String[] whereArgs;
        String where;

        if (rubric == Rubrics.LATEST) {
            whereArgs = new String[] { String.valueOf(date) };
            where = NewsEntry.COLUMN_NAME_PUBDATE + " <= ?";
        } else {
            whereArgs = new String[] { rubric.name(), String.valueOf(date) };
            where = String.format(getWhereFromSQLiteType(SQLiteType.TEXT, 1) + " and " + NewsEntry.COLUMN_NAME_PUBDATE + " <= ?", NewsEntry.COLUMN_NAME_RUBRIC);
        }

        final int result = getContentResolver().delete(getContentProviderUri(), where, whereArgs);

        if (result > 0) {
            getContentResolver().notifyChange(getContentProviderUri(), null);
        }

        return result;
    }

    @Override
    public T readLatestWOImage(Rubrics rubric, int limit) {
        Cursor cur;

        if (rubric != Rubrics.LATEST) {
            String where = getWhereFromSQLiteType(SQLiteType.TEXT, 1) + " and " + getWhereFromSQLiteType(SQLiteType.INTEGER, 2);
            String[] whereArgs = { rubric.name(), String.valueOf(0) };

            cur = getContentResolver().query(getContentProviderUri(),
                    getProjectionAll(), String.format(where, getRubricColumnName(), getLatestColumnName()), whereArgs, NewsObjectEntry.COLUMN_NAME_PUBDATE + " desc limit " + limit);
        } else {
            cur = getContentResolver().query(getContentProviderUri(),
                    getProjectionAll(), null, null, NewsObjectEntry.COLUMN_NAME_PUBDATE + " desc limit " + limit);
        }

        if (cur == null) {
            return null;
        }

        try {
            String imageLink;

            if (cur.moveToLast()) {
                do {
                    imageLink = cur.getString(cur.getColumnIndex(NewsObjectEntry.COLUMN_NAME_IMAGELINK));

                    if (imageLink == null || TextUtils.isEmpty(imageLink)) {
                        if (cur.moveToNext()) {
                            return createDataObject(cur);
                        } else {
                            break;
                        }
                    }
                } while(cur.moveToPrevious());

                if (cur.moveToFirst()) {
                    return createDataObject(cur);
                }
            }

            return null;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    @Override
    public boolean hasImage(long id) {
        Uri uri = ContentUris.withAppendedId(getContentProviderUri(), id);
        Cursor cur = null;

        try {
            cur = getContentResolver().query(uri, projectionImage, null, null, null);

            if (cur == null) {
                return false;
            }

            if (cur.moveToFirst()) {
                String imageLink = cur.getString(cur.getColumnIndex(NewsObjectEntry.COLUMN_NAME_IMAGELINK));
                return imageLink != null && !TextUtils.isEmpty(imageLink);
            }

            return false;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    @Override
    public boolean hasImage(String key) {
        String[] whereArgs = { key };
        String where = String.format(getWhereFromSQLiteType(getKeyColumnType()), getKeyColumnName());
        Cursor cur = null;

        try {
            cur = getContentResolver().query(getContentProviderUri(), projectionImage, where, whereArgs, null);

            if (cur == null) {
                return false;
            }

            if (cur.moveToFirst()) {
                String imageLink = cur.getString(cur.getColumnIndex(NewsObjectEntry.COLUMN_NAME_IMAGELINK));
                return imageLink != null && !TextUtils.isEmpty(imageLink);
            }

            return false;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    @Override
    public List<Long> readAllIds(Rubrics rubric) {
        final String[] projectionIdOnly = { BaseColumns._ID };

        Cursor cur;

        if (rubric != Rubrics.LATEST) {
            final String where = getWhereFromSQLiteType(SQLiteType.TEXT, 1);
            final String[] whereArgs = { rubric.name() };

            cur = getContentResolver().query(getContentProviderUri(),
                    projectionIdOnly, String.format(where, getRubricColumnName()), whereArgs, NewsObjectEntry.COLUMN_NAME_PUBDATE + " desc");
        } else {
            cur = getContentResolver().query(getContentProviderUri(),
                    projectionIdOnly, null, null, NewsObjectEntry.COLUMN_NAME_PUBDATE + " desc");
        }

        if (cur == null) {
            return Collections.EMPTY_LIST;
        }

        try {
            List<Long> result = new ArrayList<Long>();

            while (cur.moveToNext()) {
                result.add(cur.getLong(cur.getColumnIndexOrThrow(BaseColumns._ID)));
            }

            return result;
        } finally {
            if (cur != null) {
                cur.close();
            }
        }
    }

    @Override
    public int clearUpdatedFromLatestFlag(Rubrics rubric) {
        if (rubric != Rubrics.LATEST) {
            String where = getWhereFromSQLiteType(SQLiteType.TEXT);
            String[] whereArgs = new String[] { rubric.name() };

            return getContentResolver().update(getContentProviderUri(), clearUpdatedFromLatestFlagValues, String.format(where, getRubricColumnName()), whereArgs);
        } else {
            return getContentResolver().update(getContentProviderUri(), clearUpdatedFromLatestFlagValues, null, null);
        }
    }

    @Override
    public int setUpdatedFromLatestFlag(Rubrics rubric) {
        if (rubric != Rubrics.LATEST) {
            String where = getWhereFromSQLiteType(SQLiteType.TEXT);
            String[] whereArgs = new String[] { rubric.name() };

            return getContentResolver().update(getContentProviderUri(), setUpdatedFromLatestFlagValues, String.format(where, getRubricColumnName()), whereArgs);
        } else {
            return getContentResolver().update(getContentProviderUri(), setUpdatedFromLatestFlagValues, null, null);
        }
    }

    @Override
    public int clearUpdatedInBackgroundFlag() {
        return getContentResolver().update(getContentProviderUri(), clearUpdatedInBackgroundFlagValues, null, null);
    }

    @Override
    public int clearRecentFlag() {
        return getContentResolver().update(getContentProviderUri(), clearRecentFlagValues, null, null);
    }

    //	@Override
//	public Collection<Long> readIdsForRubric(Rubrics rubric) {
//		String where = getWhereFromSQLiteType(SQLiteType.TEXT);
//		String[] whereArgs = { rubric.name() };
//		String[] projectionId = { BaseColumns._ID };
//		
//		Cursor cur = getContentResolver().query(getContentProviderUri(),
//				projectionId, String.format(where, getRubricColumnName()), whereArgs, null);
//
//		try {
//			List<Long> result = new ArrayList<Long>();
//
//			if (cur.moveToFirst()) {
//				do {
//					result.add(cur.getLong(cur.getColumnIndex(BaseColumns._ID)));
//				} while (cur.moveToNext());
//			}
//			
//			return result;
//		} finally {
//			cur.close();
//		}
//	}
//
    protected abstract String[] getProjectionBrief();

    protected String getRubricColumnName() {
        return NewsObjectEntry.COLUMN_NAME_RUBRIC;
    }

    protected String getLatestColumnName() {
        return NewsObjectEntry.COLUMN_NAME_UPDATED_FROM_LATEST;
    }

    protected String getSortOrder() {
        return sortOrder;
    }
}
