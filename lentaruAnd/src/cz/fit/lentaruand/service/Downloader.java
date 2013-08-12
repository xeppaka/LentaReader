package cz.fit.lentaruand.service;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.parser.LentaMobileNewsParser;
import cz.fit.lentaruand.parser.MobileNews;
import cz.fit.lentaruand.parser.NewsParser;
import cz.fit.lentaruand.parser.rss.LentaRssItem;
import cz.fit.lentaruand.parser.rss.LentaRssParser;

/**
 * 
 * this class represents RESTmethod entity from presentation
 * "Developing Android REST Client Applications". Prepare an URL, execute HTTP
 * transaction, process the response (e.x. parse);
 * according to the model methods should be called from processor entity
 * 
 * Didn't want to extend LentaNewsDownloader because the source became hard to read,
 * may be some refactoring is needed later
 * 
 * @author TheWalkingDelirium
 * 
 * @see http://bit.ly/15amlM4
 * @see Processor
 */

public class Downloader {

	private final LentaRssParser rssParser = new LentaRssParser();
	private final NewsParser<MobileNews> newsParser = new LentaMobileNewsParser();
	final String LOG_TAG = "myLogs";

	/**
	 * 
	 * @param rubric from which news will be downloaded
	 * @return List of news, can be processed by adapter 
	 * @throws XPathExpressionException
	 * @throws IOException
	 */
	public void downloadRubricBrief(Rubrics rubric) {
//		if(rubric == null) {
//			Log.d(LOG_TAG, "rubric is null");
//		}
//		
//		Page xmlPage = null;
//		
//		try {
//			xmlPage = PageDownloader.downloadPage(rubric, getNewsType());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Collection<LentaRssItem> items = null;
//		
//		try {
//			items = rssParser.parseItems(xmlPage);
//		} catch (XPathExpressionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		List<News> result = new ArrayList<News>();
//
//		for (LentaRssItem item : items) {
//			result.add(createNewsObject(item));
//		}
//		
//		Collections.sort(result);
//		return result;
	}

/**
 * 
 * @param brief some brief news that need full text and more data, updates parameter object
 * @throws PageParseException
 * @throws IOException
 */
	public void downloadFull(News brief) {
//		
//		URL url = URLHelper.createMobileUrl(brief.getLink());
//		Page mobilePage = PageDownloader.downloadPage(url);
//		MobileNews mobileNews = newsParser.parse(mobilePage);
//
//		brief.setFullText(mobileNews.getText());
//		brief.setImageCaption(mobileNews.getImageCaption());
//		brief.setImageCredits(mobileNews.getImageCredits());
	}

	protected NewsType getNewsType() {
		return NewsType.NEWS;
	}

	protected News createNewsObject(LentaRssItem item) {
		return new News(item);
	}

}
