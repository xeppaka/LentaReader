package com.xeppaka.lentareader.downloader;

import java.io.IOException;
import java.util.Collection;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.convertednews.ConvertedNewsParser;
import com.xeppaka.lentareader.parser.originalnews.LentaMobileNewsParser;
import com.xeppaka.lentareader.parser.originalnews.MobileNews;
import com.xeppaka.lentareader.parser.originalnews.NewsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;
import com.xeppaka.lentareader.utils.URLHelper;

import org.xmlpull.v1.XmlPullParserException;

public class LentaNewsDownloader extends LentaNewsObjectDownloader<News> {

	private final NewsParser<MobileNews> newsParser = new LentaMobileNewsParser();
	
	@Override
	public void downloadFull(News brief) throws ParseWithRegexException, IOException, HttpStatusCodeException {
        String url = URLHelper.createMobileUrl(brief.getLink());
		Page mobilePage = new Page(url, brief.getRubric(), NewsType.NEWS, HttpDownloader.download(url));
		
		MobileNews mobileNews = newsParser.parse(mobilePage);
		brief.setBody(mobileNews.getText());
		brief.setImageCaption(mobileNews.getImageCaption());
		brief.setImageCredits(mobileNews.getImageCredits());
	}

    @Override
    public Collection<News> download(Rubrics rubric) throws HttpStatusCodeException, IOException, XmlPullParserException {
        String url = URLHelper.getXmlForRubric(rubric, NewsType.NEWS);
        Page xml = new Page(url, rubric, NewsType.NEWS, HttpDownloader.download(url));

        return new ConvertedNewsParser().parse(xml);
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
