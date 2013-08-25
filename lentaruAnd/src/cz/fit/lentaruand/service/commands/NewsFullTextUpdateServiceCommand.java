package cz.fit.lentaruand.service.commands;

import java.io.IOException;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.dao.AsyncDao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithRegexException;
import cz.fit.lentaruand.service.BundleConstants;
import cz.fit.lentaruand.service.ServiceResultAction;
import cz.fit.lentaruand.utils.LentaConstants;

public final class NewsFullTextUpdateServiceCommand extends RunnableServiceCommand {
	private long newsId;
	private News news;
	private ContentResolver contentResolver;
	private Bundle result;
	
	public NewsFullTextUpdateServiceCommand(int requestId, News news, ContentResolver contentResolver, ResultReceiver resultReceiver, boolean reportError) {
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
	
	public NewsFullTextUpdateServiceCommand(int requestId, long newsId, ContentResolver contentResolver, ResultReceiver resultReceiver, boolean reportError) {
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
		Log.d(LentaConstants.LoggerServiceTag, "Command started: " + getClass().getSimpleName());
		
		AsyncDao<News> newsDao = NewsDao.getInstance(contentResolver);

		if (news == null) {
			news = newsDao.read(newsId);
		}

		if (news != null) {
			Log.d(LentaConstants.LoggerServiceTag, "Update full text for news guid: " + news.getGuid());
			
			if (news.getFullText() != null && TextUtils.isEmpty(news.getFullText())) {
				return;
			}
			
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
		}
		
		Log.d(LentaConstants.LoggerServiceTag, "Command finished successfuly: " + getClass().getSimpleName());
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