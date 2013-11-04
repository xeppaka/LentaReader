package com.xeppaka.lentareader.asyncloaders;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.LentaNewsObjectDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;
import com.xeppaka.lentareader.utils.LentaConstants;

/**
 * 
 * @author nnm
 *
 * @param <T>
 */
public abstract class AsyncBriefNewsObjectLoader<T extends NewsObject> extends AsyncTaskLoader<List<T>> {
	private Rubrics rubric;
	private final LentaNewsObjectDownloader<T> downloader;

	public AsyncBriefNewsObjectLoader(Context context) {
		this(context, Rubrics.ROOT);
	}
	
	public AsyncBriefNewsObjectLoader(Context context, Rubrics rubric) {
		super(context);
		this.rubric = rubric;
		downloader = createDownloader();
	}
	
	@Override
	public List<T> loadInBackground() {
		try {
			return downloader.downloadRubricBrief(rubric);
		} catch (ParseWithXPathException e) {
			Log.e(LentaConstants.LoggerMainAppTag, "Error occured during downloading RSS for rubric:" + rubric.getName(), e);
		} catch (IOException e) {
			Log.e(LentaConstants.LoggerMainAppTag, "Error occured during downloading RSS for rubric:" + rubric.getName(), e);
		} catch (HttpStatusCodeException e) {
			Log.e(LentaConstants.LoggerMainAppTag, "Error occured during downloading RSS for rubric:" + rubric.getName(), e);
		}
		
		return Collections.emptyList();
	}
	
	public abstract LentaNewsObjectDownloader<T> createDownloader();
	
	public void setRubric(Rubrics rubric) {
		this.rubric = rubric;
	}
}
