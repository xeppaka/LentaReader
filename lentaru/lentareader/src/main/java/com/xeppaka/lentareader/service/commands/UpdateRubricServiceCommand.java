package com.xeppaka.lentareader.service.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.objects.NewsDao;
import com.xeppaka.lentareader.downloader.LentaNewsDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;
import com.xeppaka.lentareader.service.BundleConstants;
import com.xeppaka.lentareader.service.ServiceResultAction;
import com.xeppaka.lentareader.utils.LentaConstants;

public final class UpdateRubricServiceCommand extends RunnableServiceCommand {
	private Rubrics rubric;
	private ExecutorService executor;
	private ContentResolver contentResolver;
	private NewsType newsType;
	private Bundle result;

	public UpdateRubricServiceCommand(int requestId, Rubrics rubric, NewsType newsType, ExecutorService executor, ContentResolver contentResolver, ResultReceiver resultReceiver, boolean reportError) {
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

		Log.d(LentaConstants.LoggerServiceTag, "Number of new news from downloaded: " + nonExistingNews.size());
		
		Collection<Long> newsIds = newsDao.create(nonExistingNews);
		Log.d(LentaConstants.LoggerServiceTag, "Newly created news ids: " + newsIds);
		
		prepareResultCreated(newsIds);

		Collections.sort(nonExistingNews, Collections.reverseOrder());
		
//		for (News n : nonExistingNews) {
//			executor.execute(new RetrieveNewsFullTextServiceCommand(getRequestId(), n, contentResolver, getResultReceiver(), false));
//		}
		
		for (News n : nonExistingNews) {
			executor.execute(new RetrieveNewsImageServiceCommand(getRequestId(), n, contentResolver, getResultReceiver(), false));
		}
	}
	
	private void prepareResultCreated(Collection<Long> ids) {
		if (ids.isEmpty()) {
			result = null;
			return;
		}
		
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
