package com.xeppaka.lentareader.service.commands;

import java.util.concurrent.ExecutorService;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.objects.ImageDao;
import com.xeppaka.lentareader.data.dao.objects.NewsDao;

public class SyncNewsServiceCommand extends RunnableServiceCommand {
	private ContentResolver contentResolver;
	private ExecutorService executor;
	private News news;
	private long newsId;
	
	public SyncNewsServiceCommand(int requestId, long newsId, ContentResolver contentResolver, ExecutorService executor, ResultReceiver resultReceiver, boolean reportError) {
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

	public SyncNewsServiceCommand(int requestId, News news, ContentResolver contentResolver, ExecutorService executor, ResultReceiver resultReceiver, boolean reportError) {
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
		AsyncNODao<News> newsDao = NewsDao.getInstance(contentResolver);

		if (news == null) {
			news = newsDao.read(newsId);
		}

		if (news != null) {
			ImageDao imageDao = ImageDao.getInstance(contentResolver);
			
			String imageLink = news.getImageLink();
			if (imageLink != null && !TextUtils.isEmpty(imageLink)) {
				if (!imageDao.imageExist(imageLink)) {
					executor.execute(new RetrieveNewsImageServiceCommand(getRequestId(), news, contentResolver, getResultReceiver(), false));
				}
			}
			
			if (news.getFullText() == null) {
				executor.execute(new RetrieveNewsFullTextServiceCommand(getRequestId(), news, contentResolver, getResultReceiver(), false));
			}
		}
	}

	@Override
	protected Bundle getResult() {
		return null;
	}	
}