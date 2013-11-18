package com.xeppaka.lentareader.data.dao.objects;

import android.content.ContentResolver;
import android.database.Cursor;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.db.SQLiteType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ContentResolverNODao<T extends NewsObject> extends ContentResolverDao<T> implements NODao<T> {
	
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
