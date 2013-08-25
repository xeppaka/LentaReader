package cz.fit.lentaruand.service.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.Dao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithXPathException;
import cz.fit.lentaruand.service.BundleConstants;
import cz.fit.lentaruand.service.ServiceResultAction;
import cz.fit.lentaruand.utils.LentaConstants;

public final class RubricUpdateServiceCommand extends RunnableServiceCommand {
	private Rubrics rubric;
	private ExecutorService executor;
	private ContentResolver contentResolver;
	private NewsType newsType;
	private Bundle result;

	public RubricUpdateServiceCommand(int requestId, Rubrics rubric, NewsType newsType, ExecutorService executor, ContentResolver contentResolver, ResultReceiver resultReceiver, boolean reportError) {
		super(requestId, resultReceiver, reportError);
		
		if (rubric == null) {
			throw new NullPointerException("rubric is null.");
		}
		
		if (newsType == null) {
			throw new NullPointerException("newsType is null.");
		}
		
		if (executor == null) {
			throw new NullPointerException("executor is null.");
		}
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}
		
		this.executor = executor;
		this.contentResolver = contentResolver;
		this.newsType = newsType;
		this.rubric = rubric;
	}
	
	@Override
	public void execute() throws Exception {
		switch (newsType) {
		case NEWS:
			executeNews();
			break;
		default:
			throw new UnsupportedOperationException("rubric update for news type: " + newsType.name() + " is not supported.");
		}
	}

	private void executeNews() throws Exception {
		Log.d(LentaConstants.LoggerServiceTag, "Command started: " + getClass().getSimpleName());
		List<News> news;
		
		try {
			news = new LentaNewsDownloader().downloadRubricBrief(rubric);
			
			Log.d(LentaConstants.LoggerServiceTag, "Downloaded " + news.size() + " news.");
		} catch (ParseWithXPathException e) {
			Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, parse error.", e);
			throw e;
		} catch (IOException e) {
			Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, I/O error.", e);
			throw e;
		} catch (HttpStatusCodeException e) {
			Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, status code returned: " + e.getHttpStatusCode() + ".", e);
			throw e;
		}
		
		Dao<News> newsDao = NewsDao.getInstance(contentResolver);
		
		List<News> nonExistingNews = new ArrayList<News>();
		
		for (News n : news) {
			if (!newsDao.exist(n.getGuid())) {
				nonExistingNews.add(n);
			}
		}

		Log.d(LentaConstants.LoggerServiceTag, "New news from downloaded " + nonExistingNews.size());
		
		Collection<Long> newsIds = newsDao.create(nonExistingNews);
		
		Log.d(LentaConstants.LoggerServiceTag, "Newly created news ids: " + newsIds);
		
		prepareResultCreated(newsIds);
		
		for (News n : nonExistingNews) {
			executor.execute(new NewsImageUpdateServiceCommand(getRequestId(), n, contentResolver, getResultReceiver(), false));
		}
		
		for (News n : nonExistingNews) {
			executor.execute(new NewsFullTextUpdateServiceCommand(getRequestId(), n, contentResolver, getResultReceiver(), false));
		}
		
		Log.d(LentaConstants.LoggerServiceTag, "Command finished successfuly: " + getClass().getSimpleName());
	}
	
	private void prepareResultCreated(Collection<Long> ids) {
		result = new Bundle();
		result.putInt(BundleConstants.KEY_REQUEST_ID.name(), getRequestId());
		result.putString(BundleConstants.KEY_ACTION.name(), ServiceResultAction.DATABASE_OBJECT_CREATED.name());
		result.putString(BundleConstants.KEY_NEWS_TYPE.name(), newsType.name());
		
		long[] createdIds = new long[ids.size()];
		int index = 0;
		for (Long id : ids) {
			createdIds[index++] = id;
		}
		
		result.putLongArray(BundleConstants.KEY_IDS.name(), createdIds);
	}
	
	@Override
	protected Bundle getResult() {
		return result;
	}
}