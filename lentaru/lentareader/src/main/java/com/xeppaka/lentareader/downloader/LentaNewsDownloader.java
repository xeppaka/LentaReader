package com.xeppaka.lentareader.downloader;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.convertednews.ConvertedNewsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.parser.originalnews.LentaMobileNewsParser;
import com.xeppaka.lentareader.parser.originalnews.MobileNews;
import com.xeppaka.lentareader.parser.originalnews.NewsParser;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;
import com.xeppaka.lentareader.utils.URLHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class LentaNewsDownloader extends LentaNewsObjectDownloader<News> {

    private static final ConvertedNewsParser parser = new ConvertedNewsParser();

	private final NewsParser<MobileNews> newsParser = new LentaMobileNewsParser();
	
	@Override
	public void downloadFull(News brief) throws ParseWithRegexException, IOException, HttpStatusCodeException {
        String url = URLHelper.createMobileUrl(brief.getLink());
		Page mobilePage = new Page(url, brief.getRubric(), NewsType.NEWS, HttpPageDownloader.download(url));
		
		MobileNews mobileNews = newsParser.parse(mobilePage);
		brief.setBody(mobileNews.getText());
		brief.setImageCaption(mobileNews.getImageCaption());
		brief.setImageCredits(mobileNews.getImageCredits());
	}

    @Override
    public List<News> download(Rubrics rubric) throws HttpStatusCodeException, IOException, XmlPullParserException {
        String url = URLHelper.getXmlForRubric(rubric, NewsType.NEWS);
        //Page xml = new Page(url, rubric, NewsType.NEWS, HttpPageDownloader.download(url));

        List<News> result = parser.parse(HttpPageDownloader.download(url));

        for (News n : result) {
            n.setUpdatedFromLatest(rubric == Rubrics.LATEST);
        }

        return result;
    }

    @Override
    public List<News> download(Rubrics rubric, long fromDate) throws HttpStatusCodeException, IOException, XmlPullParserException {
        String url = URLHelper.getXmlForRubric(rubric, NewsType.NEWS, fromDate);
        //Page xml = new Page(url, rubric, NewsType.NEWS, HttpPageDownloader.download(url));

        List<News> result = parser.parse(HttpPageDownloader.download(url));

        for (News n : result) {
            n.setUpdatedFromLatest(rubric == Rubrics.LATEST);
        }

        return result;
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
