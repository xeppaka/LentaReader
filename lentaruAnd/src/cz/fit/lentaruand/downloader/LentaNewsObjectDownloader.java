package cz.fit.lentaruand.downloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithXPathException;
import cz.fit.lentaruand.parser.rss.LentaRssItem;
import cz.fit.lentaruand.parser.rss.LentaRssParser;

public abstract class LentaNewsObjectDownloader<T extends NewsObject> implements NewsObjectDownloader<T> {
	private final LentaRssParser rssParser = new LentaRssParser();
	
	public List<T> downloadRubricBrief(Rubrics rubric) throws ParseWithXPathException, HttpStatusCodeException, IOException {
		Page xmlPage = LentaHttpPageDownloader.downloadPage(rubric, getNewsType());
		
		Collection<LentaRssItem> items = rssParser.parseItems(xmlPage);
		List<T> result = new ArrayList<T>();
		
		for (LentaRssItem item : items) {
			result.add(createNewsObject(item));
		}
		
		Collections.sort(result);
		
		return result;
	}
	
	protected abstract NewsType getNewsType();
	protected abstract T createNewsObject(LentaRssItem item);
}
