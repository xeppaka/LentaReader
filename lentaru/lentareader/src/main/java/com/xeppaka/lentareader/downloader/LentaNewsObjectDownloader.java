package com.xeppaka.lentareader.downloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;
import com.xeppaka.lentareader.parser.rss.LentaRssParser;

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
