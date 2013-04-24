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
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.data.db.LentaDbHelper;

public class NewsDaoTest extends AndroidTestCase {

	private LentaDbHelper dbHelper;
	private Context context;
	private News testNews;
	private Date date;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();
		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
		date = new Date();
		testNews = new News("guid1", "News 1", "http://www.1.ru", "Brief news info", "Full news text", 
				date, "http://www.image.com/image.png", "Image caption", "Photo: PK", Rubrics.CULTURE, true);
		dbHelper = new LentaDbHelper(context);
	}

	private long createNews(String guid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			testNews.setGuid(guid);
			return newsDao.create(db, testNews);
		} finally {
			db.close();
		}
	}

	@SmallTest
	public void testReadNewsUseId() {
		long id = createNews("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			News n = newsDao.read(db, id);
			assertEquals(id, n.getId());
			assertEquals(NewsType.NEWS, n.getType());
			assertEquals("guid1", n.getGuid());
			assertEquals("News 1", n.getTitle());
			assertEquals("http://www.1.ru", n.getLink());
			assertEquals("Brief news info", n.getBriefText());
			assertEquals("Full news text", n.getFullText());
			assertEquals(date, n.getPubDate());
			assertEquals("http://www.image.com/image.png", n.getImageLink());
			assertEquals("Image caption", n.getImageCaption());
			assertEquals("Photo: PK", n.getImageCredits());
			assertEquals(Rubrics.CULTURE, n.getRubric());
			assertEquals(true, n.isRubricUpdateNeed());
			
			newsDao.delete(db, id);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testReadNewsUseGuid() {
		long id = createNews("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			News n = newsDao.read(db, "guid1");
			assertEquals(id, n.getId());
			assertEquals(NewsType.NEWS, n.getType());
			assertEquals("guid1", n.getGuid());
			assertEquals("News 1", n.getTitle());
			assertEquals("http://www.1.ru", n.getLink());
			assertEquals("Brief news info", n.getBriefText());
			assertEquals("Full news text", n.getFullText());
			assertEquals(date, n.getPubDate());
			assertEquals("http://www.image.com/image.png", n.getImageLink());
			assertEquals("Image caption", n.getImageCaption());
			assertEquals("Photo: PK", n.getImageCredits());
			assertEquals(Rubrics.CULTURE, n.getRubric());
			assertEquals(true, n.isRubricUpdateNeed());
			
			newsDao.delete(db, "guid1");
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testDeleteNewsUseGuid() {
		createNews("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			newsDao.delete(db, "guid1");
			News n = newsDao.read(db, "guid1");
			assertNull(n);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testDeleteNewsUseId() {
		long id = createNews("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			newsDao.delete(db, id);
			News n = newsDao.read(db, id);
			assertNull(n);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testUpdateNews() {
		long id = createNews("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			News n = newsDao.read(db, "guid1");
			assertEquals("News 1", n.getTitle());
	
			n.setBriefText("newbrief1");
			n.setFullText("newfull1");
			n.setGuid("newguid1");
			n.setImageCaption("newcaption1");
			n.setImageCredits("newcredits1");
			n.setImageLink("newimagelink1");
			n.setLink("newlink1");
			n.setPubDate(date);
			n.setRubric(Rubrics.ECONOMICS_FINANCE);
			n.setRubricUpdateNeed(false);
			n.setTitle("newtitle1");
			
			newsDao.update(db, n);
			
			n = newsDao.read(db, id);
			
			assertEquals(id, n.getId());
			assertEquals(NewsType.NEWS, n.getType());
			assertEquals("newguid1", n.getGuid());
			assertEquals("newtitle1", n.getTitle());
			assertEquals("newlink1", n.getLink());
			assertEquals("newbrief1", n.getBriefText());
			assertEquals("newfull1", n.getFullText());
			assertEquals(date, n.getPubDate());
			assertEquals("newimagelink1", n.getImageLink());
			assertEquals("newcaption1", n.getImageCaption());
			assertEquals("newcredits1", n.getImageCredits());
			assertEquals(Rubrics.ECONOMICS_FINANCE, n.getRubric());
			assertEquals(false, n.isRubricUpdateNeed());
			
			newsDao.delete(db, "newguid1");
			n = newsDao.read(db, "newguid1");
			assertNull(n);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testMoreNewsInDb() {
		List<String> ids = new ArrayList<String>();
		ids.add("guid1");
		ids.add("guid2");
		ids.add("guid3");
		ids.add("guid4");
		Collections.sort(ids);
		
		for (String id : ids) {
			createNews(id);
		}
		
		NewsDao newsDao = new NewsDao();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Collection<String> allIds = newsDao.readAllKeys(db);
			List<String> sortedIds = new ArrayList<String>(allIds);
			Collections.sort(sortedIds);
			
			assertEquals(ids, sortedIds);
			
			for (String id : ids) {
				newsDao.delete(db, id);
			}
			
			allIds = newsDao.readAllKeys(db);
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
