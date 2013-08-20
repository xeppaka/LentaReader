package cz.fit.lentaruand.service;

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
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.Dao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithXPathException;
import cz.fit.lentaruand.utils.LentaConstants;

public class RubricUpdateServiceCommand extends RunnableServiceCommand {
	private Rubrics rubric;
	private ExecutorService executor;
	private ContentResolver contentResolver;

	public RubricUpdateServiceCommand(Rubrics rubric, ContentResolver contentResolver, ExecutorService executor, ResultReceiver resultReceiver) {
		super(resultReceiver);
		
		if (rubric == null) {
			throw new NullPointerException("rubric is null.");
		}
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}
		
		if (executor == null) {
			throw new NullPointerException("executor is null.");
		}
		
		this.rubric = rubric;
		this.executor = executor;
		this.contentResolver = contentResolver;
	}

	@Override
	public void execute() throws Exception {
		List<News> news;
		
		try {
			news = new LentaNewsDownloader().downloadRubricBrief(rubric);
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
		
		Collection<Long> newsIds = newsDao.create(nonExistingNews);
		
		for (Long id : newsIds) {
			executor.execute(new ImageUpdateServiceCommand(id, contentResolver, getResultReceiver()));
		}
	}

	@Override
	protected Bundle prepareResult() {
		Bundle b = new Bundle();
		b.putString("EXTRA_STRING", "downloaded");
		
		return b;
	}
}
