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
import cz.fit.lentaruand.data.Article;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.ArticleDao;
import cz.fit.lentaruand.data.db.LentaDbHelper;

public class ArticleDaoTest extends AndroidTestCase {
	private LentaDbHelper dbHelper;
	private Context context;
	private Article testArticle;
	private Date date;
	
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		context = getContext();
//		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
//		date = new Date();
//		testArticle = new Article("guid1", "Vse o medvedjah.", "Mishki na severe.", "Pavel Kachalouski", "http://www.mishki.ru", 
//				"Holodno im.", "-40 tam ved'.", date, "http://www.mishki.ru/medved.jpg", "Mishka", "Photo: ISS", null, Rubrics.LIFE_ANIMALS, false);
//		dbHelper = new LentaDbHelper(context);
//	}
//
//	private long createArticle(String guid) {
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//		try {
//			ArticleDao articleDao = new ArticleDao();
//			testArticle.setGuid(guid);
//			return articleDao.create(db, testArticle);
//		} finally {
//			db.close();
//		}
//	}
//
//	@SmallTest
//	public void testReadArticleUseId() {
//		long id = createArticle("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			ArticleDao articleDao = new ArticleDao();
//			Article n = articleDao.read(db, id);
//			assertEquals(id, n.getId());
//			assertEquals("guid1", n.getGuid());
//			assertEquals(NewsType.ARTICLE, n.getType());
//			assertEquals("Vse o medvedjah.", n.getTitle());
//			assertEquals("Mishki na severe.", n.getSecondTitle());
//			assertEquals("Pavel Kachalouski", n.getAuthor());
//			assertEquals("http://www.mishki.ru", n.getLink());
//			assertEquals("Holodno im.", n.getBriefText());
//			assertEquals("-40 tam ved'.", n.getFullText());
//			assertEquals(date, n.getPubDate());
//			assertEquals("http://www.mishki.ru/medved.jpg", n.getImageLink());
//			assertEquals("Mishka", n.getImageCaption());
//			assertEquals("Photo: ISS", n.getImageCredits());
//			assertEquals(Rubrics.LIFE_ANIMALS, n.getRubric());
//			assertEquals(false, n.isRubricUpdateNeed());
//			
//			articleDao.delete(db, id);
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testReadArticleUseGuid() {
//		long id = createArticle("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			ArticleDao articleDao = new ArticleDao();
//			Article n = articleDao.read(db, "guid1");
//			assertEquals(id, n.getId());
//			assertEquals("guid1", n.getGuid());
//			assertEquals(NewsType.ARTICLE, n.getType());
//			assertEquals("Vse o medvedjah.", n.getTitle());
//			assertEquals("Mishki na severe.", n.getSecondTitle());
//			assertEquals("Pavel Kachalouski", n.getAuthor());
//			assertEquals("http://www.mishki.ru", n.getLink());
//			assertEquals("Holodno im.", n.getBriefText());
//			assertEquals("-40 tam ved'.", n.getFullText());
//			assertEquals(date, n.getPubDate());
//			assertEquals("http://www.mishki.ru/medved.jpg", n.getImageLink());
//			assertEquals("Mishka", n.getImageCaption());
//			assertEquals("Photo: ISS", n.getImageCredits());
//			assertEquals(Rubrics.LIFE_ANIMALS, n.getRubric());
//			assertEquals(false, n.isRubricUpdateNeed());
//			
//			articleDao.delete(db, "guid1");
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testDeleteArticleUseGuid() {
//		createArticle("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			ArticleDao articleDao = new ArticleDao();
//			articleDao.delete(db, "guid1");
//			Article n = articleDao.read(db, "guid1");
//			assertNull(n);
//		} finally {
//			db.close();
//		}
//	}
//	
//	public void testDeleteArticleUseId() {
//		long id = createArticle("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			ArticleDao articleDao = new ArticleDao();
//			articleDao.delete(db, id);
//			Article n = articleDao.read(db, id);
//			assertNull(n);
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testUpdateArticle() {
//		long id = createArticle("guid1");
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			ArticleDao articleDao = new ArticleDao();
//			Article n = articleDao.read(db, "guid1");
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
//			articleDao.update(db, n);
//			
//			n = articleDao.read(db, id);
//			
//			assertEquals(id, n.getId());
//			assertEquals(NewsType.ARTICLE, n.getType());
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
//			articleDao.delete(db, "newguid1");
//			n = articleDao.read(db, "newguid1");
//			assertNull(n);
//		} finally {
//			db.close();
//		}
//	}
//	
//	@SmallTest
//	public void testMoreNewsInDb() {
//		List<String> ids = new ArrayList<String>();
//		ids.add("guid1");
//		ids.add("guid2");
//		ids.add("guid3");
//		ids.add("guid4");
//		Collections.sort(ids);
//		
//		for (String id : ids) {
//			createArticle(id);
//		}
//		
//		ArticleDao articleDao = new ArticleDao();
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		try {
//			Collection<String> allIds = articleDao.readAllKeys(db);
//			List<String> sortedIds = new ArrayList<String>(allIds);
//			Collections.sort(sortedIds);
//			
//			assertEquals(ids, sortedIds);
//			
//			for (String id : ids) {
//				articleDao.delete(db, id);
//			}
//			
//			allIds = articleDao.readAllKeys(db);
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
