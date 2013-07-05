package cz.fit.lentaruand.downloader;

import java.io.IOException;
import java.net.URL;

import cz.fit.lentaruand.data.Article;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.parser.LentaMobileArticleParser;
import cz.fit.lentaruand.parser.MobileArticle;
import cz.fit.lentaruand.parser.NewsParser;
import cz.fit.lentaruand.parser.exceptions.PageParseException;
import cz.fit.lentaruand.parser.rss.LentaRssItem;
import cz.fit.lentaruand.site.URLHelper;

public class LentaArticleDownloader extends LentaNewsObjectDownloader<Article> {

	private final NewsParser<MobileArticle> articleParser = new LentaMobileArticleParser();
	
	@Override
	public void downloadFull(Article brief) throws IOException, PageParseException {
		URL url = URLHelper.createMobileUrl(brief.getLink());
		Page mobilePage = HttpPageDownloader.downloadPage(url);
		
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
