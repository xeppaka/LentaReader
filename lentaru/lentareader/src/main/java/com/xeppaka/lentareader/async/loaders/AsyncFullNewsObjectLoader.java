package com.xeppaka.lentareader.async.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.downloader.LentaNewsObjectDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.io.IOException;

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
		} catch (ParseWithRegexException e) {
			Log.e(LentaConstants.LoggerMainAppTag, "Error occured during downloading full news:" + newsObject.getLink(), e);
		} catch (IOException e) {
			Log.e(LentaConstants.LoggerMainAppTag, "Error occured during downloading RSS for rubric:" + newsObject.getLink(), e);
		} catch (HttpStatusCodeException e) {
			Log.e(LentaConstants.LoggerMainAppTag, "Error occured during downloading RSS for rubric:" + newsObject.getLink(), e);
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
