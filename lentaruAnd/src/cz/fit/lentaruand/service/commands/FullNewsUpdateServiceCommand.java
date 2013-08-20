package cz.fit.lentaruand.service.commands;

import java.io.IOException;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.dao.AsyncDao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithRegexException;
import cz.fit.lentaruand.utils.LentaConstants;

public class FullNewsUpdateServiceCommand extends RunnableServiceCommand {
	private long newsId;
	private News news;
	private ContentResolver contentResolver;
	
	public FullNewsUpdateServiceCommand(long newsId, ContentResolver contentResolver, ResultReceiver resultReceiver) {
		super(resultReceiver);
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}

		this.newsId = newsId;
		this.contentResolver = contentResolver;
	}

	public FullNewsUpdateServiceCommand(News news, ContentResolver contentResolver, ResultReceiver resultReceiver) {
		super(resultReceiver);
		
		if (news == null) {
			throw new NullPointerException("news is null.");
		}
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}

		this.news = news;
		this.contentResolver = contentResolver;
	}
	
	@Override
	public void execute() throws Exception {
		AsyncDao<News> newsDao = NewsDao.getInstance(contentResolver);

		if (news == null) {
			news = newsDao.read(newsId);
		}

		if (news.getFullText() != null && TextUtils.isEmpty(news.getFullText())) {
			return;
		}
		
		if (news != null) {
			try {
				new LentaNewsDownloader().downloadFull(news);
				newsDao.update(news);
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
	}

	@Override
	protected Bundle prepareResult() {
		return null;
	}
}
