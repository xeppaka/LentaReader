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
    private final static ContentValues clearLatestFlagValues;

    private final static String sortOrder = NewsObjectEntry.COLUMN_NAME_PUBDATE + " DESC";

    static {
        clearLatestFlagValues = new ContentValues();
        clearLatestFlagValues.put(NewsEntry.COLUMN_NAME_LATEST_NEWS, 0);
    }

	public ContentResolverNODao(ContentResolver cr) {
		super(cr);
	}

	@Override
	public List<T> read(Rubrics rubric) {
		Cursor cur;

        if (rubric != Rubrics.LATEST) {
            String where = getWhereFromSQLiteType(SQLiteType.TEXT);
            String[] whereArgs = { rubric.name() };

            cur = getContentResolver().query(getContentProviderUri(),
                    getProjectionAll(), String.format(where, getRubricColumnName()), whereArgs, null);
        } else {
            cur = getContentResolver().query(getContentProviderUri(),
                    getProjectionAll(), null, null, null);
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
    public int clearLatestFlag(Rubrics rubric) {
        String where = getWhereFromSQLiteType(SQLiteType.TEXT);
        String[] whereArgs = { rubric.name() };

        return getContentResolver().update(getContentProviderUri(), clearLatestFlagValues, String.format(where, getRubricColumnName()), whereArgs);
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
	protected String getRubricColumnName() {
        return NewsObjectEntry.COLUMN_NAME_RUBRIC;
    }

    protected String getLatestColumnName() {
        return NewsObjectEntry.COLUMN_NAME_LATEST_NEWS;
    }

    protected String getSortOrder() {
        return sortOrder;
    }
}
