package cz.fit.lentaruand.data.dao.newsobject;

import java.util.Collection;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import cz.fit.lentaruand.data.PhotoImage;
import cz.fit.lentaruand.data.dao.ContentResolverDao;
import cz.fit.lentaruand.data.db.PhotoImageEntry;
import cz.fit.lentaruand.data.db.SQLiteType;

public class PhotoImageDao extends ContentResolverDao<PhotoImage> {
	private static final String[] projectionAll = {
		PhotoImageEntry._ID,
		PhotoImageEntry.COLUMN_NAME_INDEX,
		PhotoImageEntry.COLUMN_NAME_PHOTO_ID,
		PhotoImageEntry.COLUMN_NAME_TITLE,
		PhotoImageEntry.COLUMN_NAME_URL,
		PhotoImageEntry.COLUMN_NAME_CREDITS,
		PhotoImageEntry.COLUMN_NAME_DESCRIPTION
	};

	public PhotoImageDao(ContentResolver cr) {
		super(cr);
	}

	@Override
	protected ContentValues prepareContentValues(PhotoImage photoImage) {
		ContentValues values = new ContentValues();

		values.put(PhotoImageEntry.COLUMN_NAME_PHOTO_ID, photoImage.getPhotoId());
		values.put(PhotoImageEntry.COLUMN_NAME_INDEX, photoImage.getIndex());
		
		if (photoImage.getTitle() == null)
			values.putNull(PhotoImageEntry.COLUMN_NAME_TITLE);
		else
			values.put(PhotoImageEntry.COLUMN_NAME_TITLE, photoImage.getTitle());
		
		values.put(PhotoImageEntry.COLUMN_NAME_URL, photoImage.getUrl());
		
		if (photoImage.getCredits() == null)
			values.putNull(PhotoImageEntry.COLUMN_NAME_CREDITS);
		else
			values.put(PhotoImageEntry.COLUMN_NAME_CREDITS, photoImage.getCredits());
		
		if (photoImage.getDescription() == null)
			values.putNull(PhotoImageEntry.COLUMN_NAME_DESCRIPTION);
		else
			values.put(PhotoImageEntry.COLUMN_NAME_DESCRIPTION, photoImage.getDescription());
		
		return values;
	}

	@Override
	protected PhotoImage createDataObject(Cursor cur) {
		long id = cur.getInt(cur.getColumnIndexOrThrow(PhotoImageEntry._ID));
		long photoid = cur.getInt(cur.getColumnIndexOrThrow(PhotoImageEntry.COLUMN_NAME_PHOTO_ID));
		int index = cur.getInt(cur.getColumnIndexOrThrow(PhotoImageEntry.COLUMN_NAME_INDEX));
		String title = cur.getString(cur.getColumnIndexOrThrow(PhotoImageEntry.COLUMN_NAME_TITLE));
		String url = cur.getString(cur.getColumnIndexOrThrow(PhotoImageEntry.COLUMN_NAME_URL));
		String credits = cur.getString(cur.getColumnIndexOrThrow(PhotoImageEntry.COLUMN_NAME_CREDITS));
		String description = cur.getString(cur.getColumnIndexOrThrow(PhotoImageEntry.COLUMN_NAME_DESCRIPTION));
		
		return new PhotoImage(id, photoid, index, url, title, credits, description);
	}

	@Override
	protected Uri getContentProviderUri() {
		// TODO return proper value
		return null;
	}

	@Override
	protected String getIdColumnName() {
		return PhotoImageEntry._ID;
	}

	@Override
	protected String getKeyColumnName() {
		return PhotoImageEntry._ID;
	}

	@Override
	protected SQLiteType getKeyColumnType() {
		return SQLiteType.INTEGER;
	}

	@Override
	protected String[] getProjectionAll() {
		return projectionAll;
	}

	@Override
	public Collection<PhotoImage> readForParentObject(long parentId) {
		return null;
	}

//	public Collection<PhotoImage> readForPhoto(SQLiteDatabase db, long photoId) {
//		List<PhotoImage> result = new ArrayList<PhotoImage>();
//		
//		String[] keyWhereArgs = { String.valueOf(photoId) };
//		String keyWhere = PhotoImageEntry.COLUMN_NAME_PHOTO_ID + " = ?";
//		
//		Cursor cur = db.query(
//				getTableName(), 
//				getProjectionAll(), 
//				keyWhere,
//				keyWhereArgs, 
//				null, 
//				null, 
//				null
//				);
//		
//		try {
//			if (cur.moveToFirst()) {
//				do {
//					result.add(createDaoObject(cur));
//				} while (cur.moveToNext());
//			}
//			
//			Collections.sort(result);
//			
//			return result;
//		} finally {
//			cur.close();
//		}
//	}
}
