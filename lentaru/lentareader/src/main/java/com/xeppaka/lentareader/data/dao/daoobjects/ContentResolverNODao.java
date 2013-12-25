package com.xeppaka.lentareader.data.dao.daoobjects;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.db.NewsObjectEntry;
import com.xeppaka.lentareader.data.db.SQLiteType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ContentResolverNODao<T extends NewsObject> extends ContentResolverDao<T> implements NODao<T> {

    private final static String[] projectionImage = { BaseColumns._ID, NewsObjectEntry.COLUMN_NAME_IMAGELINK };

	public ContentResolverNODao(ContentResolver cr) {
		super(cr);
	}

	@Override
	public List<T> readForRubric(Rubrics rubric) {
		String where = getWhereFromSQLiteType(SQLiteType.TEXT);
		String[] whereArgs = { rubric.name() };
		
		Cursor cur = getContentResolver().query(getContentProviderUri(),
				getProjectionAll(), String.format(where, getRubricColumnName()), whereArgs, null);

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
	protected abstract String getRubricColumnName();
}
