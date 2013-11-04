package com.xeppaka.lentareader.service.commands;

import java.io.IOException;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.objects.NewsDao;
import com.xeppaka.lentareader.downloader.LentaNewsDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.service.BundleConstants;
import com.xeppaka.lentareader.service.ServiceResultAction;
import com.xeppaka.lentareader.service.commands.exceptions.NewsItemUpdateException;
import com.xeppaka.lentareader.utils.LentaConstants;

public final class RetrieveNewsFullTextServiceCommand extends RunnableServiceCommand {
	private long newsId;
	private News news;
	private ContentResolver contentResolver;
	private Bundle result;
	
	public RetrieveNewsFullTextServiceCommand(int requestId, News news, ContentResolver contentResolver, ResultReceiver resultReceiver, boolean reportError) {
		super(requestId, resultReceiver, reportError);
		
		if (news == null) {
			throw new NullPointerException("news is null.");
		}
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}
		
		this.news = news;
		this.contentResolver = contentResolver;
	}
	
	public RetrieveNewsFullTextServiceCommand(int requestId, long newsId, ContentResolver contentResolver, ResultReceiver resultReceiver, boolean reportError) {
		super(requestId, resultReceiver, reportError);
		
		if (newsId < 0) {
			throw new IllegalArgumentException("newsId is negative.");
		}
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}
		
		this.newsId = newsId;
		this.contentResolver = contentResolver;
	}
	
	@Override
	public void execute() throws Exception {
		AsyncDao<News> newsDao = NewsDao.getInstance(contentResolver);

		if (news == null) {
			news = newsDao.read(newsId);
		}

		if (news != null) {
			Log.d(LentaConstants.LoggerServiceTag, "Update full text for news guid: " + news.getGuid());
			
			try {
				new LentaNewsDownloader().downloadFull(news);
				newsDao.update(news);
				
				prepareResultUpdated(news.getId());
			} catch (ParseWithRegexException e) {
				Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, parse error.", e);
				throw e;
			} catch (IOException e) {
				Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, I/O error.", e);
				throw e;
			} catch (HttpStatusCodeException e) {
				Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, status code returned: " + e.getHttpStatusCode() + ".", e);
				throw e;
			}
		} else {
			throw new NewsItemUpdateException("Cannot find in database news with guid " + news.getGuid());
		}
	}

	private void prepareResultUpdated(long id) {
		result = new Bundle();
		result.putInt(BundleConstants.KEY_REQUEST_ID.name(), getRequestId());
		result.putString(BundleConstants.KEY_ACTION.name(), ServiceResultAction.DATABASE_OBJECT_UPDATED.name());
		result.putString(BundleConstants.KEY_NEWS_TYPE.name(), NewsType.NEWS.name());
		result.putLongArray(BundleConstants.KEY_IDS.name(), new long[]{id});
	}
	
	@Override
	protected Bundle getResult() {
		return result;
	}
}
