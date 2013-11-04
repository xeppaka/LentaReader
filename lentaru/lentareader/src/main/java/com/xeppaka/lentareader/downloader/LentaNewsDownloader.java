package com.xeppaka.lentareader.downloader;

import java.io.IOException;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.LentaMobileNewsParser;
import com.xeppaka.lentareader.parser.MobileNews;
import com.xeppaka.lentareader.parser.NewsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;
import com.xeppaka.lentareader.utils.URLHelper;

public class LentaNewsDownloader extends LentaNewsObjectDownloader<News> {

	private final NewsParser<MobileNews> newsParser = new LentaMobileNewsParser();
	
	@Override
	public void downloadFull(News brief) throws ParseWithRegexException, IOException, HttpStatusCodeException {
		Page mobilePage = LentaHttpPageDownloader.downloadPage(URLHelper.createMobileUrl(brief.getLink()));
		
		MobileNews mobileNews = newsParser.parse(mobilePage);
		brief.setBody(mobileNews.getText());
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
