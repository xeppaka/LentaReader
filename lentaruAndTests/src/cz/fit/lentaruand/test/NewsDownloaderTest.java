package cz.fit.lentaruand.test;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithRegexException;
import cz.fit.lentaruand.parser.exceptions.ParseWithXPathException;

public class NewsDownloaderTest extends AndroidTestCase {
	private final Logger logger = Logger.getLogger(NewsDownloaderTest.class.getName());
	private LentaNewsDownloader lentaNewsDownloader;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lentaNewsDownloader = new LentaNewsDownloader();
	}

	@LargeTest
	public void testFullParsing() throws IOException, ParseWithRegexException, ParseWithXPathException, HttpStatusCodeException {
		int total = 0;
		
		for (Rubrics rubric : Rubrics.values()) {
			logger.log(Level.INFO, "Downloading brief rubric: " + rubric.name() + "...");
			List<News> news = lentaNewsDownloader.downloadRubricBrief(rubric);
			logger.log(Level.INFO, "Downloaded. News number: " + news.size() + ".");
			logger.log(Level.INFO, "Downloading news full text...");
			for (News n : news) {
				logger.log(Level.INFO, "Downloading: " + n.getTitle());
				lentaNewsDownloader.downloadFull(n);
				logger.log(Level.INFO, "Finished.");
				++total;
			}
		}
		
		logger.log(Level.INFO, "Total number of news processed: " + total);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
