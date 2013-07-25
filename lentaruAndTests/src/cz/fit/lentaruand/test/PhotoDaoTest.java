package cz.fit.lentaruand.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import cz.fit.lentaruand.data.Photo;
import cz.fit.lentaruand.data.PhotoImage;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.PhotoDao;
import cz.fit.lentaruand.data.dao.PhotoImageDao;
import cz.fit.lentaruand.data.db.LentaDbHelper;

public class PhotoDaoTest extends AndroidTestCase {
	private LentaDbHelper dbHelper;
	private Context context;
	private Photo testPhoto;
	private List<PhotoImage> photoImages;
	private Date date;
	
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		context = getContext();
//		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
//		date = new Date();
//		photoImages = new ArrayList<PhotoImage>();
//		
//		photoImages.add(new PhotoImage(4, "http://www.mishki.ru/image.jpg", "image4", "Photo by: NNM", "Description"));
//		photoImages.add(new PhotoImage(3, "http://www.mishki.ru/image.jpg", "image3", "Photo by: NNM", "Description"));
//		photoImages.add(new PhotoImage(2, "http://www.mishki.ru/image.jpg", "image2", "Photo by: NNM", "Description"));
//		photoImages.add(new PhotoImage(5, "http://www.mishki.ru/image.jpg", "image5", "Photo by: NNM", "Description"));
//		photoImages.add(new PhotoImage(1, "http://www.mishki.ru/image.jpg", "image1", "Photo by: NNM", "Description"));
//		
//		Collections.sort(photoImages);
//		
//		testPhoto = new Photo("guid1", "Photo1", "PhotoPhoto2", "House", photoImages, 
//				"http://www.mishki.ru", date, Rubrics.CULTURE_KINO, true);
//		
//		dbHelper = new LentaDbHelper(context);
//	}
//
//	private long createPhoto(String guid) {
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		try {
//			PhotoDao photoDao = new PhotoDao();
//			testPhoto.setGuid(guid);
//			return photoDao.create(db, testPhoto);
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testReadPhotoUseId() {
//		long id = createPhoto("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			PhotoDao photoDao = new PhotoDao();
//			Photo p = photoDao.read(db, id);
//
//			assertEquals(testPhoto, p);
//			
//			photoDao.delete(db, id);
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testReadPhotoUseGuid() {
//		createPhoto("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			PhotoDao photoDao = new PhotoDao();
//			Photo p = photoDao.read(db, "guid1");
//			
//			assertEquals(testPhoto, p);
//			
//			photoDao.delete(db, "guid1");
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testDeletePhotoUseGuid() {
//		createPhoto("guid1");
//		
//		Iterator<PhotoImage> it = testPhoto.getPhotos().iterator();
//		long photoImage1 = it.next().getId();
//		long photoImage2 = it.next().getId();
//		long photoImage3 = it.next().getId();
//		long photoImage4 = it.next().getId();
//		long photoImage5 = it.next().getId();
//		
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			PhotoDao photoDao = new PhotoDao();
//			photoDao.delete(db, "guid1");
//			Photo n = photoDao.read(db, "guid1");
//			assertNull(n);
//			
//			PhotoImageDao photoImageDao = new PhotoImageDao();
//			PhotoImage p1 = photoImageDao.read(db, photoImage1);
//			assertNull(p1);
//			p1 = photoImageDao.read(db, photoImage2);
//			assertNull(p1);
//			p1 = photoImageDao.read(db, photoImage3);
//			assertNull(p1);
//			p1 = photoImageDao.read(db, photoImage4);
//			assertNull(p1);
//			p1 = photoImageDao.read(db, photoImage5);
//			assertNull(p1);
//			
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testDeletePhotoUseId() {
//		long id = createPhoto("guid1");
//		
//		Iterator<PhotoImage> it = testPhoto.getPhotos().iterator();
//		long photoImage1 = it.next().getId();
//		long photoImage2 = it.next().getId();
//		long photoImage3 = it.next().getId();
//		long photoImage4 = it.next().getId();
//		long photoImage5 = it.next().getId();
//		
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			PhotoDao photoDao = new PhotoDao();
//			photoDao.delete(db, id);
//			Photo n = photoDao.read(db, id);
//			assertNull(n);
//			
//			PhotoImageDao photoImageDao = new PhotoImageDao();
//			PhotoImage p1 = photoImageDao.read(db, photoImage1);
//			assertNull(p1);
//			p1 = photoImageDao.read(db, photoImage2);
//			assertNull(p1);
//			p1 = photoImageDao.read(db, photoImage3);
//			assertNull(p1);
//			p1 = photoImageDao.read(db, photoImage4);
//			assertNull(p1);
//			p1 = photoImageDao.read(db, photoImage5);
//			assertNull(p1);
//			
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testUpdatePhoto() {
//		long id = createPhoto("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			PhotoDao photoDao = new PhotoDao();
//			Photo p = photoDao.read(db, "guid1");
//
//			p.setGuid("newguid1");
//			p.setDescription("new description");
//			p.setLink("newlink1");
//			p.setPubDate(new Date());
//			p.setRubric(Rubrics.WORLD_SOCIETY);
//			p.setRubricUpdateNeed(true);
//			p.setSecondTitle("new second title");
//			p.setTitle("new title 2");
//			
//			Collection<PhotoImage> photos = p.getPhotos();
//			int i = 0;
//			for (PhotoImage photoImage : photos) {
//				photoImage.setCredits("New credits " + i);
//				photoImage.setDescription("new description " + i);
//				photoImage.setTitle("new title " + i);
//				photoImage.setUrl("new url " + i);
//				++i;
//			}
//			
//			photoDao.update(db, p);
//			Photo p1 = photoDao.read(db, id);
//			
//			assertEquals(p, p1);
//			
//			photoDao.delete(db, "newguid1");
//			p = photoDao.read(db, "newguid1");
//			assertNull(p);
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testMorePhotosInDb() {
//		List<String> ids = new ArrayList<String>();
//		ids.add("guid1");
//		ids.add("guid2");
//		ids.add("guid3");
//		ids.add("guid4");
//		Collections.sort(ids);
//		
//		for (String id : ids) {
//			createPhoto(id);
//		}
//		
//		PhotoDao PhotoDao = new PhotoDao();
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			Collection<String> allIds = PhotoDao.readAllKeys(db);
//			List<String> sortedIds = new ArrayList<String>(allIds);
//			Collections.sort(sortedIds);
//			
//			assertEquals(ids, sortedIds);
//			
//			for (String id : ids) {
//				PhotoDao.delete(db, id);
//			}
//			
//			allIds = PhotoDao.readAllKeys(db);
//			assertEquals(allIds.size(), 0);
//		} finally {
//			db.close();
//		}
//	}
//	
//	@Override
//	protected void tearDown() throws Exception {
//		super.tearDown();
//		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
//	}
}
