package cz.fit.lentaruand.site;

import java.io.IOException;
import java.net.URL;

import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.parser.LentaMobileNewsParser;
import cz.fit.lentaruand.parser.MobileNews;
import cz.fit.lentaruand.parser.NewsParser;
import cz.fit.lentaruand.parser.exceptions.PageParseException;
import cz.fit.lentaruand.rss.LentaRssItem;

public class LentaNewsDownloader extends LentaNewsObjectDownloader<News> {

	private final NewsParser<MobileNews> newsParser = new LentaMobileNewsParser();
	
	@Override
	public void downloadFull(News brief) throws PageParseException, IOException {
		URL url = URLHelper.createMobileUrl(brief.getLink());
		Page mobilePage = PageDownloader.downloadPage(url);
		
		MobileNews mobileNews = newsParser.parse(mobilePage);
		brief.setFullText(mobileNews.getText());
		brief.setImageCaption(mobileNews.getImageCaption());
		brief.setImageCredits(mobileNews.getImageCredits());
	}

	@Override
	protected NewsType getNewsType() {
		return NewsType.NEWS;
	}

	@Override
	protected News createNewsObject(LentaRssItem item) {
		return new News(item);
	}
}
