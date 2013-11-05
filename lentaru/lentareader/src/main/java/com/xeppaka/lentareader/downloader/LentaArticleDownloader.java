package com.xeppaka.lentareader.downloader;

import java.io.IOException;
import java.util.Collection;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.body.LentaBody;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;
import com.xeppaka.lentareader.parser.originalnews.LentaMobileArticleParser;
import com.xeppaka.lentareader.parser.originalnews.MobileArticle;
import com.xeppaka.lentareader.parser.originalnews.NewsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;
import com.xeppaka.lentareader.utils.URLHelper;

import org.xmlpull.v1.XmlPullParserException;

public class LentaArticleDownloader extends LentaNewsObjectDownloader<Article> {

	private final NewsParser<MobileArticle> articleParser = new LentaMobileArticleParser();
	
	@Override
	public void downloadFull(Article brief) throws HttpStatusCodeException, IOException, ParseWithRegexException {
        String url = URLHelper.createMobileUrl(brief.getLink());
		Page mobilePage = new Page(url, brief.getRubric(), NewsType.ARTICLE, HttpDownloader.download(url));
		
		MobileArticle mobileArticle = articleParser.parse(mobilePage);
		brief.setBody(LentaBody.create(mobileArticle.getText().toXml()));
		brief.setImageCaption(mobileArticle.getImageCaption());
		brief.setImageCredits(mobileArticle.getImageCredits());
		brief.setSecondTitle(mobileArticle.getSecondTitle());
	}

    @Override
    public Collection<Article> download(Rubrics rubric) throws HttpStatusCodeException, IOException, XmlPullParserException {
        return null;
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
