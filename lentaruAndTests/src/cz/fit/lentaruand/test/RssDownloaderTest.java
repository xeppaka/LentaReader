package cz.fit.lentaruand.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathExpressionException;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.Page;
import cz.fit.lentaruand.downloader.PageDownloader;
import cz.fit.lentaruand.parser.rss.LentaRssParser;

public class RssDownloaderTest extends AndroidTestCase {
	private final Logger logger = Logger.getLogger(RssDownloaderTest.class.getName());
	private LentaRssParser rssParser;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		rssParser = new LentaRssParser();
	}

	@MediumTest
	public void testSimpleRssParsing() throws MalformedURLException, IOException, XPathExpressionException {
		for (Rubrics rubric : Rubrics.values()) {
			NewsType newsType = NewsType.NEWS;
//			for (NewsType newsType : NewsType.values()) {
				logger.log(Level.INFO, "Downloading rubric: " + rubric.name() + ", news type: " + newsType + "...");
				Page rssPage = PageDownloader.downloadPage(rubric, newsType);
				logger.log(Level.INFO, "Downloaded.");
				logger.log(Level.INFO, "Parsing...");
				rssParser.parseItems(rssPage);
				logger.log(Level.INFO, "Finished.");
//			}
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
