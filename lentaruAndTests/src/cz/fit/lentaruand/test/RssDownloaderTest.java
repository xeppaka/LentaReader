package cz.fit.lentaruand.test;

import android.test.AndroidTestCase;
import cz.fit.lentaruand.parser.rss.LentaRssParser;

public class RssDownloaderTest extends AndroidTestCase {
	private LentaRssParser rssParser;
	
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//		rssParser = new LentaRssParser();
//	}
//
//	@MediumTest
//	public void testSimpleRssParsing() throws MalformedURLException, IOException, XPathExpressionException {
//		for (Rubrics rubric : Rubrics.values()) {
//			NewsType newsType = NewsType.NEWS;
////			for (NewsType newsType : NewsType.values()) {
//				logger.log(Level.INFO, "Downloading rubric: " + rubric.name() + ", news type: " + newsType + "...");
//				Page rssPage = PageDownloader.downloadPage(rubric, newsType);
//				logger.log(Level.INFO, "Downloaded.");
//				logger.log(Level.INFO, "Parsing...");
//				rssParser.parseItems(rssPage);
//				logger.log(Level.INFO, "Finished.");
////			}
//		}
//	}
//	
//	@Override
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
}
