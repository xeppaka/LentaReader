package cz.fit.lentaruand.data.dao.objects;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.NODao;
import cz.fit.lentaruand.data.db.SQLiteType;

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

		try {
			List<T> result = new ArrayList<T>();

			if (cur.moveToFirst()) {
				do {
					result.add(createDataObject(cur));
				} while (cur.moveToNext());
			}
			
			return result;
		} finally {
			cur.close();
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
