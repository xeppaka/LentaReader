package cz.fit.lentaruand.asyncloaders;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithXPathException;
import cz.fit.lentaruand.utils.LentaConstants;

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
