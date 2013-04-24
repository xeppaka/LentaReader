package cz.fit.lentaruand.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import android.test.AndroidTestCase;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.DefaultDao;
import cz.fit.lentaruand.downloader.Page;
import cz.fit.lentaruand.downloader.PageDownloader;
import cz.fit.lentaruand.parser.rss.LentaRssParser;

public class RssParserTest extends AndroidTestCase {
	private final Logger logger = Logger.getLogger(RssParserTest.class.getName());
	private LentaRssParser rssParser;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rssParser = new LentaRssParser();
	}

	public void testSimpleParsing() throws MalformedURLException, IOException, XPathExpressionException {
		for (Rubrics rubric : Rubrics.values()) {
			logger.log(Level.INFO, "Downloading rubric: " + rubric.getName());
			Page rssPage = PageDownloader.downloadPage(rubric, NewsType.NEWS);
			logger.log(Level.INFO, "Rubric downloaded.");
			logger.log(Level.INFO, "Parsing rubric: " + rubric.getName());
			rssParser.parseItems(rssPage);
			logger.log(Level.INFO, "Rubric parsed.");
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
