package cz.fit.lentaruand.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Photo;
import cz.fit.lentaruand.data.PhotoImage;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.PhotoDao;
import cz.fit.lentaruand.data.db.LentaDbHelper;

public class PhotoDaoTest extends AndroidTestCase {
	private LentaDbHelper dbHelper;
	private Context context;
	private Photo testPhoto;
	private List<PhotoImage> photoImages;
	private Date date;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();
		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
		date = new Date();
		photoImages = new ArrayList<PhotoImage>();
		
		photoImages.add(new PhotoImage(4, "http://www.mishki.ru/image.jpg", "image4", "Photo by: NNM", "Description"));
		photoImages.add(new PhotoImage(3, "http://www.mishki.ru/image.jpg", "image3", "Photo by: NNM", "Description"));
		photoImages.add(new PhotoImage(2, "http://www.mishki.ru/image.jpg", "image2", "Photo by: NNM", "Description"));
		photoImages.add(new PhotoImage(5, "http://www.mishki.ru/image.jpg", "image5", "Photo by: NNM", "Description"));
		photoImages.add(new PhotoImage(1, "http://www.mishki.ru/image.jpg", "image1", "Photo by: NNM", "Description"));
		
		testPhoto = new Photo("guid1", "Photo1", "PhotoPhoto2", "House", photoImages, 
				"http://www.mishki.ru", date, Rubrics.CULTURE_KINO, true);
		
		dbHelper = new LentaDbHelper(context);
	}

	private long createPhoto(String guid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			PhotoDao photoDao = new PhotoDao();
			testPhoto.setGuid(guid);
			return photoDao.create(db, testPhoto);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testReadPhotoUseId() {
		long id = createPhoto("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			PhotoDao photoDao = new PhotoDao();
			Photo n = photoDao.read(db, id);
			assertEquals(id, n.getId());
			assertEquals("guid1", n.getGuid());
			assertEquals(NewsType.PHOTO, n.getType());
			assertEquals("Photo1", n.getTitle());
			assertEquals("PhotoPhoto2", n.getSecondTitle());
			assertEquals("House", n.getDescription());
			assertEquals("http://www.mishki.ru", n.getLink());
			assertEquals(date, n.getPubDate());
			assertEquals(Rubrics.CULTURE_KINO, n.getRubric());
			assertEquals(true, n.isRubricUpdateNeed());
			assertEquals(photoImages.size(), n.getPhotos().size());
			
			photoDao.delete(db, id);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testReadPhotoUseGuid() {
		long id = createPhoto("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			PhotoDao photoDao = new PhotoDao();
			Photo n = photoDao.read(db, "guid1");
			assertEquals(id, n.getId());
			assertEquals("guid1", n.getGuid());
			assertEquals(NewsType.PHOTO, n.getType());
			assertEquals("Photo1", n.getTitle());
			assertEquals("PhotoPhoto2", n.getSecondTitle());
			assertEquals("House", n.getDescription());
			assertEquals("http://www.mishki.ru", n.getLink());
			assertEquals(date, n.getPubDate());
			assertEquals(Rubrics.CULTURE_KINO, n.getRubric());
			assertEquals(true, n.isRubricUpdateNeed());
			assertEquals(photoImages.size(), n.getPhotos().size());
			
			photoDao.delete(db, "guid1");
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testDeletePhotoUseGuid() {
		createPhoto("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			PhotoDao photoDao = new PhotoDao();
			photoDao.delete(db, "guid1");
			Photo n = photoDao.read(db, "guid1");
			assertNull(n);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testDeletePhotoUseId() {
		long id = createPhoto("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			PhotoDao photoDao = new PhotoDao();
			photoDao.delete(db, id);
			Photo n = photoDao.read(db, id);
			assertNull(n);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testUpdatePhoto() {
//		long id = createPhoto("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			PhotoDao photoDao = new PhotoDao();
//			Photo n = photoDao.read(db, "guid1");
//
//			n.setGuid("newguid1");
//			n.setBriefText("newbrief1");
//			n.setFullText("newfull1");
//			n.setImageCaption("newimagecaption1");
//			n.setImageCredits("newimagecredits1");
//			n.setImageLink("newimagelink1");
//			n.setLink("newlink1");
//			n.setPubDate(date);
//			n.setRubric(Rubrics.WORLD_SOCIETY);
//			n.setRubricUpdateNeed(true);
//			n.setAuthor("new author1");
//			n.setSecondTitle("new second title1");
//			n.setTitle("newtitle1");
//			
//			PhotoDao.update(db, n);
//			
//			n = PhotoDao.read(db, id);
//			
//			assertEquals(id, n.getId());
//			assertEquals(NewsType.Photo, n.getType());
//			assertEquals("newguid1", n.getGuid());
//			assertEquals("newbrief1", n.getBriefText());
//			assertEquals("newfull1", n.getFullText());
//			assertEquals("newtitle1", n.getTitle());
//			assertEquals("newlink1", n.getLink());
//			assertEquals(date, n.getPubDate());
//			assertEquals("newimagelink1", n.getImageLink());
//			assertEquals("newimagecaption1", n.getImageCaption());
//			assertEquals("newimagecredits1", n.getImageCredits());
//			assertEquals("new author1", n.getAuthor());
//			assertEquals("new second title1", n.getSecondTitle());
//			assertEquals(Rubrics.WORLD_SOCIETY, n.getRubric());
//			assertEquals(true, n.isRubricUpdateNeed());
//			
//			PhotoDao.delete(db, "newguid1");
//			n = PhotoDao.read(db, "newguid1");
//			assertNull(n);
//		} finally {
//			db.close();
//		}
	}
	
	@SmallTest
	public void testMorePhotosInDb() {
		List<String> ids = new ArrayList<String>();
		ids.add("guid1");
		ids.add("guid2");
		ids.add("guid3");
		ids.add("guid4");
		Collections.sort(ids);
		
		for (String id : ids) {
			createPhoto(id);
		}
		
		PhotoDao PhotoDao = new PhotoDao();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Collection<String> allIds = PhotoDao.readAllKeys(db);
			List<String> sortedIds = new ArrayList<String>(allIds);
			Collections.sort(sortedIds);
			
			assertEquals(ids, sortedIds);
			
			for (String id : ids) {
				PhotoDao.delete(db, id);
			}
			
			allIds = PhotoDao.readAllKeys(db);
			assertEquals(allIds.size(), 0);
		} finally {
			db.close();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
	}
}
