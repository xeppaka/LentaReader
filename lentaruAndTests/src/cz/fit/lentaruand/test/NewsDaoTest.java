package cz.fit.lentaruand.test;

import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
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

	private long createNews() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			return newsDao.create(db, testNews);
		} finally {
			db.close();
		}
	}

	public void testReadNews() {
		long id = createNews();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
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
		db.close();
		testDeleteNews();
	}
	
	public void testDeleteNews() {
		createNews();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		NewsDao newsDao = new NewsDao();
		newsDao.delete(db, "guid1");
		News n = newsDao.read(db, "guid1");
		assertNull(n);
		db.close();
	}
	
	public void testUpdateNews() {
		createNews();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		NewsDao newsDao = new NewsDao();
		News n = newsDao.read(db, "guid1");
		assertEquals("News 1", n.getTitle());
		n.setTitle("mytitle1");
		newsDao.update(db, n);
		n = newsDao.read(db, "guid1");
		assertEquals("mytitle1", n.getTitle());
		newsDao.delete(db, "guid1");
		n = newsDao.read(db, "guid1");
		assertNull(n);
		db.close();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
	}
}
