package com.xeppaka.lentareader.downloader;

import java.io.IOException;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.LentaMobileArticleParser;
import com.xeppaka.lentareader.parser.MobileArticle;
import com.xeppaka.lentareader.parser.NewsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;
import com.xeppaka.lentareader.utils.URLHelper;

public class LentaArticleDownloader extends LentaNewsObjectDownloader<Article> {

	private final NewsParser<MobileArticle> articleParser = new LentaMobileArticleParser();
	
	@Override
	public void downloadFull(Article brief) throws HttpStatusCodeException, IOException, ParseWithRegexException {
		Page mobilePage = LentaHttpPageDownloader.downloadPage(URLHelper.createMobileUrl(brief.getLink()));
		
		MobileArticle mobileArticle = articleParser.parse(mobilePage);
		brief.setFullText(mobileArticle.getText());
		brief.setImageCaption(mobileArticle.getImageCaption());
		brief.setImageCredits(mobileArticle.getImageCredits());
		brief.setSecondTitle(mobileArticle.getSecondTitle());
	}

	@Override
	protected NewsType getNewsType() {
		return NewsType.ARTICLE;
	}

	@Override
	protected Article createNewsObject(LentaRssItem item) {
		return new Article(item);
	}
}
