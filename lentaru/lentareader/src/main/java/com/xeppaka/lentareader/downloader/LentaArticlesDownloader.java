package com.xeppaka.lentareader.downloader;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.convertednews.ConvertedArticlesParser;
import com.xeppaka.lentareader.parser.convertednews.ConvertedNewsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.parser.originalnews.LentaMobileArticleParser;
import com.xeppaka.lentareader.parser.originalnews.MobileArticle;
import com.xeppaka.lentareader.parser.originalnews.NewsParser;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;
import com.xeppaka.lentareader.utils.URLHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class LentaArticlesDownloader extends LentaNewsObjectDownloader<Article> {

    private static final ConvertedArticlesParser parser = new ConvertedArticlesParser();

	private final NewsParser<MobileArticle> articleParser = new LentaMobileArticleParser();
	
	@Override
	public void downloadFull(Article brief) throws HttpStatusCodeException, IOException, ParseWithRegexException {
        String url = URLHelper.createMobileUrl(brief.getLink());
		Page mobilePage = new Page(url, brief.getRubric(), /*NewsType.ARTICLE*/ null, HttpPageDownloader.download(url));
		
		MobileArticle mobileArticle = articleParser.parse(mobilePage);
		//brief.setBody(LentaBody.create(mobileArticle.getText().toXml()));
		brief.setImageCaption(mobileArticle.getImageCaption());
		brief.setImageCredits(mobileArticle.getImageCredits());
		brief.setSecondTitle(mobileArticle.getSecondTitle());
	}

    @Override
    public List<Article> download(Rubrics rubric) throws HttpStatusCodeException, IOException, XmlPullParserException {
        String url = URLHelper.getXmlForRubric(rubric, NewsType.ARTICLE);
        //Page xml = new Page(url, rubric, NewsType.NEWS, HttpPageDownloader.download(url));

        List<Article> result = parser.parse(HttpPageDownloader.download(url));

        for (News n : result) {
            n.setUpdatedFromLatest(rubric == Rubrics.LATEST);
        }

        return result;
    }

    @Override
    public List<Article> download(Rubrics rubric, long fromDate) throws HttpStatusCodeException, IOException, XmlPullParserException {
        String url = URLHelper.getXmlForRubric(rubric, NewsType.ARTICLE, fromDate);
        //Page xml = new Page(url, rubric, NewsType.NEWS, HttpPageDownloader.download(url));

        List<Article> result = parser.parse(HttpPageDownloader.download(url));

        for (News n : result) {
            n.setUpdatedFromLatest(rubric == Rubrics.LATEST);
        }

        return result;
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
