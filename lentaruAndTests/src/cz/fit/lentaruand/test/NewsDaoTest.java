package cz.fit.lentaruand.test;

import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.data.db.LentaDbHelper;

public class NewsDaoTest extends AndroidTestCase {

	private LentaDbHelper dbHelper;
	private Context context;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();
		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
		dbHelper = new LentaDbHelper(context);
	}

	private void createNews() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		NewsDao newsDao = new NewsDao();
		News news = new News("guid1", "News 1", "http://www.1.ru", "Brief news info", "Full news text", 
				new Date(), "http://www.image.com/image.png", "Image caption", "Photo: PK", Rubrics.CULTURE, true);
		newsDao.create(db, news);
		db.close();
	}

	public void testReadNews() {
		createNews();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		NewsDao newsDao = new NewsDao();
		News n = newsDao.read(db, "guid1");
		assertEquals("guid1", n.getGuid());
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
		News news1 = newsDao.read(db, "guid1");
		assertEquals("News 1", news1.getTitle());
		news1.setTitle("mytitle1");
		newsDao.update(db, news1);
		news1 = newsDao.read(db, "guid1");
		assertEquals("mytitle1", news1.getTitle());
		newsDao.delete(db, "guid1");
		news1 = newsDao.read(db, "guid1");
		assertNull(news1);
		db.close();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
	}
}
