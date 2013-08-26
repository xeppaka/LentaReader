package cz.fit.lentaruand.service.commands;

import java.util.concurrent.ExecutorService;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ResultReceiver;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.dao.async.AsyncDao;
import cz.fit.lentaruand.data.dao.newsobject.ImageDao;
import cz.fit.lentaruand.data.dao.newsobject.NewsDao;

public class NewsUpdateServiceCommand extends RunnableServiceCommand {
	private ContentResolver contentResolver;
	private ExecutorService executor;
	private News news;
	private long newsId;
	
	public NewsUpdateServiceCommand(int requestId, long newsId, ContentResolver contentResolver, ExecutorService executor, ResultReceiver resultReceiver, boolean reportError) {
		super(requestId, resultReceiver, reportError);
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}
		
		if (executor == null) {
			throw new NullPointerException("executor is null.");
		}
		
		if (newsId < 0) {
			throw new NullPointerException("newsId is negative.");
		}
		
		this.newsId = newsId;
		this.contentResolver = contentResolver;
		this.executor = executor;
	}

	public NewsUpdateServiceCommand(int requestId, News news, ContentResolver contentResolver, ExecutorService executor, ResultReceiver resultReceiver, boolean reportError) {
		super(requestId, resultReceiver, reportError);
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}
		
		if (executor == null) {
			throw new NullPointerException("executor is null.");
		}

		if (news == null) {
			throw new NullPointerException("news is null.");
		}
		
		this.contentResolver = contentResolver;
		this.executor = executor;
		this.news = news;
	}

	@Override
	public void execute() throws Exception {
		AsyncDao<News> newsDao = NewsDao.getInstance(contentResolver);

		if (news == null) {
			news = newsDao.read(newsId);
		}

		if (news != null) {
			ImageDao imageDao = ImageDao.getInstance(contentResolver);
			
			if (!imageDao.imageExist(news.getImageLink())) {
				executor.execute(new NewsImageUpdateServiceCommand(getRequestId(), news, contentResolver, getResultReceiver(), false));
			}
			
			if (news.getFullText() == null) {
				executor.execute(new NewsFullTextUpdateServiceCommand(getRequestId(), news, contentResolver, getResultReceiver(), false));
			}
		}
	}

	@Override
	protected Bundle getResult() {
		return null;
	}	
}