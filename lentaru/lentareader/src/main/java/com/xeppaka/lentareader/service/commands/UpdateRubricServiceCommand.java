package com.xeppaka.lentareader.service.commands;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.NODao;
import com.xeppaka.lentareader.data.dao.daoobjects.ArticleDao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.downloader.LentaArticlesDownloader;
import com.xeppaka.lentareader.downloader.LentaNewsDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.ui.activities.NewsBriefActivity;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.PreferencesConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class UpdateRubricServiceCommand extends RunnableServiceCommand {
    private NewsType newsType;
	private Rubrics rubric;
    private Context context;
    private boolean scheduled;

    public UpdateRubricServiceCommand(int requestId, NewsType newsType, Rubrics rubric, boolean scheduled, Context context, ResultReceiver resultReceiver) {
		super(context, requestId, resultReceiver, true);
		
		this.context = context;
		this.newsType = newsType;
		this.rubric = rubric;
        this.scheduled = scheduled;
	}
	
	@Override
	public void execute() throws Exception {
		switch (newsType) {
		case NEWS:
			executeNews();
			break;
        case ARTICLE:
            executeArticle();
            break;
		default:
			throw new UnsupportedOperationException("rubric update for news type: " + newsType.name() + " is not supported.");
		}
	}

	private void executeNews() throws Exception {
		List<News> news;

        NODao<News> newsDao = NewsDao.getInstance(context.getContentResolver());

//        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        final boolean deleteOldNews = preferences.getBoolean(PreferencesConstants.PREF_KEY_NEWS_DELETE_NEWS, PreferencesConstants.NEWS_DELETE_NEWS_DEFAULT);

//        if (deleteOldNews) {
//            final int deleteDays = preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_DELETE_NEWS_DAYS, PreferencesConstants.NEWS_DELETE_NEWS_DAYS_DEFAULT);
//
//            // one day is 86400000 milliseconds
//            newsDao.deleteOlderOrEqual(rubric, System.currentTimeMillis() - deleteDays * 86400000);
//        }

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

        final int newsBefore = newsDao.count();
		final List<News> nonExistingNews = new ArrayList<News>();
        final List<News> withNewImage = new ArrayList<News>();

		for (News n : news) {
			if (!newsDao.exist(n.getGuid())) {
				nonExistingNews.add(n);
                n.setUpdatedInBackground(scheduled);

                if (rubric == Rubrics.LATEST && newsBefore > 0) {
                    n.setRecent(true);
                }
			} else if (n.hasImage() && !newsDao.hasImage(n.getGuid())) {
                withNewImage.add(n);
            }
		}

		Log.d(LentaConstants.LoggerServiceTag, "Number of new news from downloaded: " + nonExistingNews.size());

        if (nonExistingNews.size() > 0) {
            Log.d(LentaConstants.LoggerServiceTag, "Clearing UpdatedInBackground and Recent flags for all news.");
            newsDao.clearUpdatedInBackgroundFlag();

            if (rubric == Rubrics.LATEST) {
                newsDao.clearRecentFlag();
            }
        }

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

        List<Long> ids = newsDao.readAllIds(Rubrics.LATEST);
        if (ids.size() > 500) {
            Collections.sort(ids, Collections.reverseOrder());

            newsDao.deleteSmallerIds(ids.get(500));
        }

        if (rubric != Rubrics.LATEST) {
            newsDao.clearUpdatedFromLatestFlag(rubric);
        }

//        if (scheduled) {
//            showNotification(nonExistingNews);
//        }
	}

//    private void showNotification(List<News> news) {
//        final Intent briefNewsIntent = new Intent(context, NewsBriefActivity.class);
//        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, briefNewsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        builder.setContentTitle("Lenta.ru новые новости")
//               .setContentText("test")
//               .setSmallIcon(R.drawable.lenta_icon)
//               .setNumber(news.size())
//               .setAutoCancel(true)
//               .setDeleteIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, builder.build());
//    }

    private void executeArticle() throws Exception {
        List<Article> articles;

        NODao<Article> articleDao = ArticleDao.getInstance(context.getContentResolver());

//        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        final boolean deleteOldArticles = preferences.getBoolean(PreferencesConstants.PREF_KEY_ARTICLE_DELETE_ARTICLES, PreferencesConstants.ARTICLE_DELETE_ARTICLES_DEFAULT);
//
//        if (deleteOldArticles) {
//            final int deleteDays = preferences.getInt(PreferencesConstants.PREF_KEY_ARTICLE_DELETE_ARTICLE_DAYS, PreferencesConstants.ARTICLE_DELETE_ARTICLES_DAYS_DEFAULT);
//
//            // one day is 86400000 milliseconds
//            articleDao.deleteOlderOrEqual(rubric, System.currentTimeMillis() - deleteDays * 86400000);
//        }

        try {
            Article latest = articleDao.readLatestWOImage(rubric, LentaConstants.WITHOUT_PICTURE_LIMIT);

            if (latest == null) {
                articles = new LentaArticlesDownloader().download(rubric);
            } else {
                articles = new LentaArticlesDownloader().download(rubric, latest.getPubDate().getTime());
            }

            Log.d(LentaConstants.LoggerServiceTag, "Downloaded " + articles.size() + " articles.");
        } catch (IOException e) {
            Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, I/O error.", e);
            throw e;
        } catch (HttpStatusCodeException e) {
            Log.e(LentaConstants.LoggerServiceTag, "Error downloading page, status code returned: " + e.getHttpStatusCode() + ".", e);
            throw e;
        }

        final int newsBefore = articleDao.count();
        final List<Article> nonExistingNews = new ArrayList<Article>();
        final List<Article> withNewImage = new ArrayList<Article>();

        for (Article art : articles) {
            if (!articleDao.exist(art.getGuid())) {
                nonExistingNews.add(art);
                art.setUpdatedInBackground(scheduled);

                if (rubric == Rubrics.LATEST && newsBefore > 0) {
                    art.setRecent(true);
                }
            } else if (art.hasImage() && !articleDao.hasImage(art.getGuid())) {
                withNewImage.add(art);
            }
        }

        Log.d(LentaConstants.LoggerServiceTag, "Number of new articles from downloaded: " + nonExistingNews.size());

        if (nonExistingNews.size() > 0) {
            Log.d(LentaConstants.LoggerServiceTag, "Clearing UpdatedInBackground and Recent flags for all articles.");
            articleDao.clearUpdatedInBackgroundFlag();

            if (rubric == Rubrics.LATEST) {
                articleDao.clearRecentFlag();
            }
        }

        Collection<Long> newsIds = articleDao.create(nonExistingNews);
        Log.d(LentaConstants.LoggerServiceTag, "Newly created news ids: " + newsIds);

        for (Article art : withNewImage) {
            Article newWithImage = articleDao.read(art.getGuid());

            newWithImage.setTitle(art.getTitle());
            newWithImage.setDescription(art.getDescription());
            newWithImage.setBody(art.getBody());
            newWithImage.setImageLink(art.getImageLink());
            newWithImage.setImageCredits(art.getImageCredits());
            newWithImage.setImageCaption(art.getImageCaption());

            articleDao.update(newWithImage);
        }

        List<Long> ids = articleDao.readAllIds(Rubrics.LATEST);
        if (ids.size() > 500) {
            Collections.sort(ids, Collections.reverseOrder());

            articleDao.deleteSmallerIds(ids.get(500));
        }

        if (rubric != Rubrics.LATEST) {
            articleDao.clearUpdatedFromLatestFlag(rubric);
        }

//        if (scheduled) {
//            showNotification(nonExistingNews);
//        }
    }
}
