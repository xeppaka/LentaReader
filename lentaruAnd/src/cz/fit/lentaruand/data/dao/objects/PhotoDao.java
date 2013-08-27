package cz.fit.lentaruand.data.dao.objects;

import java.util.Collection;
import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import cz.fit.lentaruand.data.Photo;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.db.PhotoEntry;
import cz.fit.lentaruand.data.db.SQLiteType;

public class PhotoDao extends ContentResolverDao<Photo> {
	private static final String[] projectionAll = {
		PhotoEntry._ID,
		PhotoEntry.COLUMN_NAME_GUID,
		PhotoEntry.COLUMN_NAME_TITLE,
		PhotoEntry.COLUMN_NAME_SECOND_TITLE,
		PhotoEntry.COLUMN_NAME_LINK,
		PhotoEntry.COLUMN_NAME_DESCRIPTION,
		PhotoEntry.COLUMN_NAME_PUBDATE,
		PhotoEntry.COLUMN_NAME_RUBRIC,
		PhotoEntry.COLUMN_NAME_RUBRIC_UPDATE
	};
	
	//private PhotoImageDao photoImageDao;
	
	public PhotoDao(ContentResolver cr) {
		super(cr);
		//photoImageDao = new PhotoImageDao(cr);
	}
	
	@Override
	protected ContentValues prepareContentValues(Photo photo) {
		ContentValues values = new ContentValues();
		
		values.put(PhotoEntry.COLUMN_NAME_GUID, photo.getGuid());
		values.put(PhotoEntry.COLUMN_NAME_TITLE, photo.getTitle());
		values.put(PhotoEntry.COLUMN_NAME_LINK, photo.getLink());

		if (photo.getSecondTitle() == null)
			values.putNull(PhotoEntry.COLUMN_NAME_SECOND_TITLE);
		else
			values.put(PhotoEntry.COLUMN_NAME_SECOND_TITLE, photo.getSecondTitle());
		
		if (photo.getDescription() == null)
			values.putNull(PhotoEntry.COLUMN_NAME_DESCRIPTION);
		else
			values.put(PhotoEntry.COLUMN_NAME_DESCRIPTION, photo.getDescription());
		
		values.put(PhotoEntry.COLUMN_NAME_PUBDATE, photo.getPubDate().getTime());
		values.put(PhotoEntry.COLUMN_NAME_RUBRIC, photo.getRubric().name());
		values.put(PhotoEntry.COLUMN_NAME_RUBRIC_UPDATE, photo.isRubricUpdateNeed() ? 1 : 0);
		
		return values;
	}

	@Override
	protected Photo createDataObject(Cursor cur) {
		long id = cur.getLong(cur.getColumnIndexOrThrow(PhotoEntry._ID));
		String guidDb = cur.getString(cur.getColumnIndexOrThrow(PhotoEntry.COLUMN_NAME_GUID));
		String title = cur.getString(cur.getColumnIndexOrThrow(PhotoEntry.COLUMN_NAME_TITLE));
		String secondTitle = cur.getString(cur.getColumnIndexOrThrow(PhotoEntry.COLUMN_NAME_SECOND_TITLE));
		String link = cur.getString(cur.getColumnIndexOrThrow(PhotoEntry.COLUMN_NAME_LINK));
		String description = cur.getString(cur.getColumnIndexOrThrow(PhotoEntry.COLUMN_NAME_DESCRIPTION));
		Date pubDate = new Date(cur.getLong(cur.getColumnIndexOrThrow(PhotoEntry.COLUMN_NAME_PUBDATE)));
		Rubrics rubric = Rubrics.valueOf(cur.getString(cur.getColumnIndexOrThrow(PhotoEntry.COLUMN_NAME_RUBRIC)));
		boolean rubricUpdateNeed = cur.getInt(cur.getColumnIndexOrThrow(PhotoEntry.COLUMN_NAME_RUBRIC_UPDATE)) > 0;
		
		return new Photo(id, guidDb, title, secondTitle, description, null, link, pubDate, rubric, rubricUpdateNeed);
	}

	@Override
	protected Uri getContentProviderUri() {
		// TODO return proper Uri
		return null;
	}

	@Override
	protected String getIdColumnName() {
		return PhotoEntry._ID;
	}
	
	@Override
	protected String getKeyColumnName() {
		return PhotoEntry.COLUMN_NAME_GUID;
	}

	@Override
	protected SQLiteType getKeyColumnType() {
		return SQLiteType.TEXT;
	}

	@Override
	protected String[] getProjectionAll() {
		return projectionAll;
	}

	@Override
	public Collection<Photo> readForParentObject(long parentId) {
		return null;
	}
	
//	@Override
//	public Photo read(SQLiteDatabase db, String key) {
//		Photo photo = super.read(db, key);
//		
//		if (photo == null)
//			return photo;
//			
//		List<PhotoImage> images = new ArrayList<PhotoImage>(photoImageDao.readForPhoto(db, photo.getId()));
//		Collections.sort(images);
//		
//		photo.setPhotos(images);
//		return photo;
//	}
//
//	@Override
//	public Photo read(SQLiteDatabase db, long id) {
//		Photo photo = super.read(db, id);
//		
//		if (photo == null)
//			return photo;
//			
//		List<PhotoImage> images = new ArrayList<PhotoImage>(photoImageDao.readForPhoto(db, photo.getId()));
//		Collections.sort(images);
//		
//		photo.setPhotos(images);
//		return photo;
//	}
//
//	@Override
//	public Photo read(SQLiteDatabase db, SQLiteType keyType,
//			String keyColumnName, String keyValue) {
//		Photo photo = super.read(db, keyType, keyColumnName, keyValue);
//		
//		if (photo == null)
//			return photo;
//			
//		List<PhotoImage> images = new ArrayList<PhotoImage>(photoImageDao.readForPhoto(db, photo.getId()));
//		Collections.sort(images);
//		
//		photo.setPhotos(images);
//		return photo;
//	}
//
//	@Override
//	public long create(SQLiteDatabase db, Photo photo) {
//		long photoId = super.create(db, photo);
//		Collection<PhotoImage> images = photo.getPhotos();
//		
//		for (PhotoImage image : images) {
//			image.setPhotoId(photoId);
//			photoImageDao.create(db, image);
//		}
//		
//		return photoId;
//	}
//
//	@Override
//	public void delete(SQLiteDatabase db, String key) {
//		Photo photo = read(db, key);
//		Collection<PhotoImage> images = photo.getPhotos();
//		
//		for (PhotoImage image : images) {
//			photoImageDao.delete(db, image.getKeyValue());
//		}
//		
//		super.delete(db, photo.getKeyValue());
//	}
//
//	@Override
//	public void update(SQLiteDatabase db, Photo photo) {
//		Collection<PhotoImage> images = photo.getPhotos();
//		
//		for (PhotoImage image : images) {
//			photoImageDao.update(db, image);
//		}
//		
//		super.update(db, photo);
//	}
}
