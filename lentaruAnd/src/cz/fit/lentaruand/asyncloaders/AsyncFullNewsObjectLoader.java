package cz.fit.lentaruand.asyncloaders;

import java.io.IOException;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;
import cz.fit.lentaruand.parser.exceptions.PageParseException;

/**
 * 
 * @author nnm
 *
 * @param <T>
 */
public abstract class AsyncFullNewsObjectLoader<T extends NewsObject> extends AsyncTaskLoader<T> {
	private final LentaNewsObjectDownloader<T> downloader;
	private T newsObject;

	public AsyncFullNewsObjectLoader(Context context, T newsObject) {
		super(context);
		downloader = createDownloader();
		this.newsObject = newsObject;
	}
	
	@Override
	public T loadInBackground() {
		try {
			downloader.downloadFull(newsObject);
		} catch (PageParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return newsObject;
	}
	
	public abstract LentaNewsObjectDownloader<T> createDownloader();

	public T getNewsObject() {
		return newsObject;
	}

	public void setNewsObject(T newsObject) {
		this.newsObject = newsObject;
	}
}
