package cz.fit.lentaruand.service;

import java.io.IOException;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.dao.AsyncDao;
import cz.fit.lentaruand.data.dao.ImageDao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.downloader.LentaHttpImageDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.utils.LentaConstants;

public class ImageUpdateServiceCommand extends RunnableServiceCommand {
	private long newsId;
	private ContentResolver contentResolver;
	
	public ImageUpdateServiceCommand(long newsId, ContentResolver contentResolver, ResultReceiver resultReceiver) {
		super(resultReceiver);
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}

		this.newsId = newsId;
		this.contentResolver = contentResolver;
	}

	@Override
	public void execute() {
		AsyncDao<News> newsDao = NewsDao.getInstance(contentResolver);

		News news = newsDao.read(newsId);

		if (news != null) {
			String imageLink = news.getImageLink();
			if (imageLink != null && !TextUtils.isEmpty(imageLink)) {
				ImageDao imageDao = ImageDao.getInstance(contentResolver);

				if (!imageDao.imageExist(imageLink)) {
					try {
						Bitmap newBitmap = LentaHttpImageDownloader.downloadBitmap(imageLink);
						imageDao.create(imageLink, newBitmap);
					} catch (HttpStatusCodeException e) {
						Log.e(LentaConstants.LoggerServiceTag,
								"Error loading image from URL: " + imageLink, e);
					} catch (IOException e) {
						Log.e(LentaConstants.LoggerServiceTag,
								"Error loading image from URL: " + imageLink, e);
					}
				}
			}
		}
	}

	@Override
	protected Bundle prepareResult() {
		return null;
	}
}
