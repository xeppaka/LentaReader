package cz.fit.lentaruand.downloader;

import java.io.IOException;

import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.LentaMobileNewsParser;
import cz.fit.lentaruand.parser.MobileNews;
import cz.fit.lentaruand.parser.NewsParser;
import cz.fit.lentaruand.parser.exceptions.ParseWithRegexException;
import cz.fit.lentaruand.parser.rss.LentaRssItem;
import cz.fit.lentaruand.utils.URLHelper;

public class LentaNewsDownloader extends LentaNewsObjectDownloader<News> {

	private final NewsParser<MobileNews> newsParser = new LentaMobileNewsParser();
	
	@Override
	public void downloadFull(News brief) throws ParseWithRegexException, IOException, HttpStatusCodeException {
		Page mobilePage = LentaHttpPageDownloader.downloadPage(URLHelper.createMobileUrl(brief.getLink()));
		
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
