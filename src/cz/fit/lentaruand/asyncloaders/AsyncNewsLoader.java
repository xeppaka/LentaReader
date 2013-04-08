package cz.fit.lentaruand.asyncloaders;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.parser.exceptions.PageParseException;

public class AsyncNewsLoader extends AsyncTaskLoader<List<News>> {

	private Set<String> skipGuids;
	private Rubrics rubric;
	private final LentaNewsDownloader newsDownloader;

	public AsyncNewsLoader(Context context) {
		this(context, Rubrics.ROOT, Collections.<String>emptySet());
	}
	
	public AsyncNewsLoader(Context context, Rubrics rubric, Set<String> skipGuids) {
		super(context);
		this.rubric = rubric;
		this.skipGuids = skipGuids;
		newsDownloader = new LentaNewsDownloader();
	}
	
	@Override
	public List<News> loadInBackground() {
		try {
			List<News> news = newsDownloader.downloadRubricBrief(rubric);

			Iterator<News> it = news.iterator();
			while (it.hasNext()) {
				News n = it.next();
				
				if (skipGuids.contains(n.getGuid())) {
					it.remove();
					continue;
				}
				
				try {
					newsDownloader.downloadFull(n);
				} catch (PageParseException e) {
					e.printStackTrace();
				}
			}
			
			return news;			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Collections.emptyList();
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}
	
	public void setRubric(Rubrics rubric) {
		this.rubric = rubric;
	}
	
	public void setSkipGuids(Set<String> guids) {
		this.skipGuids = guids;
	}
}
