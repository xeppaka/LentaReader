package cz.fit.lentaruand.site;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.xpath.XPathExpressionException;

import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.parser.LentaMobileNewsParser;
import cz.fit.lentaruand.parser.MobileNews;
import cz.fit.lentaruand.parser.NewsParser;
import cz.fit.lentaruand.parser.exceptions.MobileNewsParseException;
import cz.fit.lentaruand.rss.LentaRssItem;
import cz.fit.lentaruand.rss.LentaRssParser;

public class LentaNewsDownloader {
	private final LentaRssParser rssParser = new LentaRssParser();
	private final NewsParser<MobileNews> newsParser = new LentaMobileNewsParser();
	
	public Collection<News> downloadRubricBrief(Rubrics rubric) throws XPathExpressionException, IOException {
		URL url = URLHelper.getRssForRubric(rubric, NewsType.NEWS);
		Page xmlPage = PageDownloader.downloadPage(url);
		
		Collection<LentaRssItem> items = rssParser.parseItems(xmlPage, rubric, NewsType.NEWS);
		Collection<News> result = new ArrayList<News>();
		
		for (LentaRssItem item : items) {
			result.add(new News(item));
		}
		
		return result;
	}

	public void downloadFull(News brief) throws IOException, MobileNewsParseException {
		URL url = URLHelper.createMobileUrl(brief.getLink());
		Page mobilePage = PageDownloader.downloadPage(url);
		
		MobileNews mobileNews = newsParser.parse(mobilePage);
		brief.setFullText(mobileNews.getText());
		brief.setImageCaption(mobileNews.getImageCaption());
		brief.setImageCredits(mobileNews.getImageCredits());
	}
}
