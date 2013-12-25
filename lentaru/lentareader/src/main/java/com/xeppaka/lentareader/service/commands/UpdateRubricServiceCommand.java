package com.xeppaka.lentareader.service.commands;

import android.content.ContentResolver;
import android.os.ResultReceiver;
import android.util.Log;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.downloader.LentaNewsDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

public final class UpdateRubricServiceCommand extends RunnableServiceCommand {
    private NewsType newsType;
	private Rubrics rubric;
	private ExecutorService executor;
	private ContentResolver contentResolver;

	public UpdateRubricServiceCommand(int requestId, NewsType newsType, Rubrics rubric, ExecutorService executor, ContentResolver contentResolver, ResultReceiver resultReceiver) {
		super(requestId, resultReceiver);
		
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
			news = new LentaNewsDownloader().download(rubric);

			Log.d(LentaConstants.LoggerServiceTag, "Downloaded " + news.size() + " news.");
		} catch (IOException e) {
			Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, I/O error.", e);
			throw e;
		} catch (HttpStatusCodeException e) {
			Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, status code returned: " + e.getHttpStatusCode() + ".", e);
			throw e;
		}

        NODao<News> newsDao = NewsDao.getInstance(contentResolver);

		List<News> nonExistingNews = new ArrayList<News>();
        List<News> withNewImage = new ArrayList<News>();

		for (News n : news) {
			if (!newsDao.exist(n.getGuid())) {
				nonExistingNews.add(n);
			} else if (n.hasImage() && !newsDao.hasImage(n.getGuid())) {
                withNewImage.add(n);
            }
		}

		Log.d(LentaConstants.LoggerServiceTag, "Number of new news from downloaded: " + nonExistingNews.size());

		Collection<Long> newsIds = newsDao.create(nonExistingNews);
		Log.d(LentaConstants.LoggerServiceTag, "Newly created news ids: " + newsIds);

        for (News n : withNewImage) {
            News newWithImage = newsDao.read(n.getGuid());

            newWithImage.setImageLink(n.getImageLink());
            newWithImage.setImageCredits(n.getImageCredits());
            newWithImage.setImageCaption(n.getImageCaption());

            newsDao.update(newWithImage);
        }
	}
	
	private void prepareResultCreated(Collection<Long> ids) {
//		if (ids.isEmpty()) {
//			result = null;
//			return;
//		}
//
//		result = new Bundle();
//		result.putInt(BundleConstants.KEY_REQUEST_ID.name(), getRequestId());
//		result.putString(BundleConstants.KEY_ACTION.name(), ServiceResultAction.DATABASE_OBJECT_CREATED.name());
//		result.putString(BundleConstants.KEY_NEWS_TYPE.name(), newsType.name());
//
//		long[] createdIds = new long[ids.size()];
//		int index = 0;
//		for (Long id : ids) {
//			createdIds[index++] = id;
//		}
//
//		result.putLongArray(BundleConstants.KEY_IDS.name(), createdIds);
	}
}
