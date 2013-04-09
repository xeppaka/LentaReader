package cz.fit.lentaruand.asyncloaders;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.xpath.XPathExpressionException;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;
import cz.fit.lentaruand.parser.exceptions.PageParseException;

public abstract class AsyncNewsObjectLoader<T extends NewsObject> extends AsyncTaskLoader<List<T>> {

	private Set<String> skipGuids;
	private Rubrics rubric;
	private final LentaNewsObjectDownloader<T> downloader;

	public AsyncNewsObjectLoader(Context context) {
		this(context, Rubrics.ROOT, Collections.<String>emptySet());
	}
	
	public AsyncNewsObjectLoader(Context context, Rubrics rubric, Set<String> skipGuids) {
		super(context);
		this.rubric = rubric;
		this.skipGuids = skipGuids;
		downloader = createDownloader();
	}
	
	@Override
	public List<T> loadInBackground() {
		try {
			List<T> newsObjects = downloader.downloadRubricBrief(rubric);
			Iterator<T> it = newsObjects.iterator();
			
			while (it.hasNext()) {
				T n = it.next();
				
				if (skipGuids.contains(n.getGuid())) {
					it.remove();
					continue;
				}
				
				try {
					downloader.downloadFull(n);
				} catch (PageParseException e) {
					e.printStackTrace();
				}
			}
			
			return newsObjects;			
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return Collections.emptyList();
	}
	
	public abstract LentaNewsObjectDownloader<T> createDownloader();
	
	public void setRubric(Rubrics rubric) {
		this.rubric = rubric;
	}
}
