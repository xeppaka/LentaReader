package cz.fit.lentaruand.asyncloaders;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.parser.exceptions.PageParseException;

public class AsyncNewsLoader extends AsyncTaskLoader<List<News>> {

	private Rubrics rubric;
	private final LentaNewsDownloader newsDownloader;
	
	public AsyncNewsLoader(Context context, Rubrics rubric) {
		super(context);
		this.rubric = rubric;
		newsDownloader = new LentaNewsDownloader();
	}

	@Override
	public List<News> loadInBackground() {
		try {
			List<News> news = newsDownloader.downloadRubricBrief(rubric);
			
			for (News n : news) {
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
}
