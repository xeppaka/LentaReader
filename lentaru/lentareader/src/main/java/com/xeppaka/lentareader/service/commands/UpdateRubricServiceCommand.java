package com.xeppaka.lentareader.service.commands;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.downloader.LentaNewsDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.PreferencesConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class UpdateRubricServiceCommand extends RunnableServiceCommand {
    private NewsType newsType;
	private Rubrics rubric;
    private Context context;

    public UpdateRubricServiceCommand(int requestId, NewsType newsType, Rubrics rubric, Context context, ResultReceiver resultReceiver) {
		super(context, requestId, resultReceiver, true);
		
		this.context = context;
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

        NODao<News> newsDao = NewsDao.getInstance(context.getContentResolver());

        try {
            News latest = newsDao.readLatestWOImage(rubric, LentaConstants.WITHOUT_PICTURE_LIMIT);

            if (latest == null) {
			    news = new LentaNewsDownloader().download(rubric);
            } else {
                news = new LentaNewsDownloader().download(rubric, latest.getPubDate().getTime());
            }

			Log.d(LentaConstants.LoggerServiceTag, "Downloaded " + news.size() + " news.");
		} catch (IOException e) {
			Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, I/O error.", e);
			throw e;
		} catch (HttpStatusCodeException e) {
			Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, status code returned: " + e.getHttpStatusCode() + ".", e);
			throw e;
		}

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

            newWithImage.setTitle(n.getTitle());
            newWithImage.setDescription(n.getDescription());
            newWithImage.setBody(n.getBody());
            newWithImage.setImageLink(n.getImageLink());
            newWithImage.setImageCredits(n.getImageCredits());
            newWithImage.setImageCaption(n.getImageCaption());

            newsDao.update(newWithImage);
        }

        if (rubric != Rubrics.LATEST) {
            newsDao.clearLatestFlag(rubric);
        }

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean deleteOldNews = preferences.getBoolean(PreferencesConstants.PREF_KEY_NEWS_DELETE_NEWS, PreferencesConstants.NEWS_DELETE_NEWS_DEFAULT);

        if (deleteOldNews) {
            final int deleteDays = preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_DELETE_NEWS_DAYS, PreferencesConstants.NEWS_DELETE_NEWS_DAYS_DEFAULT);

            // one day is 86400000 milliseconds
            newsDao.deleteOlderOrEqual(rubric, System.currentTimeMillis() - deleteDays * 86400000);
        }
	}
}
