package cz.fit.lentaruand.service.commands;

import java.io.IOException;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.dao.AsyncDao;
import cz.fit.lentaruand.data.dao.ImageDao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.data.dao.StrongBitmapReference;
import cz.fit.lentaruand.downloader.LentaHttpImageDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.service.BundleConstants;
import cz.fit.lentaruand.service.ServiceResultAction;
import cz.fit.lentaruand.service.commands.exceptions.ImageUpdateException;
import cz.fit.lentaruand.utils.LentaConstants;

public class NewsImageUpdateServiceCommand extends ImageUpdateServiceCommand<News> {
	private Bundle result;

	public NewsImageUpdateServiceCommand(int requestId, News newsObject,
			ContentResolver contentResolver, ResultReceiver resultReceiver,
			boolean reportError) {
		super(requestId, newsObject, contentResolver, resultReceiver, reportError);
	}
	
	@Override
	public void execute() throws Exception {
		Log.d(LentaConstants.LoggerServiceTag, "Command started: " + getClass().getSimpleName());
		
		AsyncDao<News> newsDao = NewsDao.getInstance(getContentResolver());

		News news = getNewsObject();
		
		if (news == null) {
			news = newsDao.read(getNewsId());
		}

		if (news != null) {
			Log.d(LentaConstants.LoggerServiceTag, "Update image for news guid: " + news.getGuid());
			
			String imageLink = news.getImageLink();
			
			if (imageLink != null && !TextUtils.isEmpty(imageLink)) {
				ImageDao imageDao = ImageDao.getInstance(getContentResolver());

				if (!imageDao.imageExist(imageLink)) {
					try {
						Log.d(LentaConstants.LoggerServiceTag, "Image doesn't exist in the cache: " + imageLink);
						
						Bitmap newBitmap = LentaHttpImageDownloader.downloadBitmap(imageLink);
						news.setImage(new StrongBitmapReference(newBitmap));
						
						newsDao.update(news);

						Log.d(LentaConstants.LoggerServiceTag, "Successfuly downloaded and saved image: " + imageLink);
						
						prepareResultUpdated(news.getId());
					} catch (HttpStatusCodeException e) {
						Log.e(LentaConstants.LoggerServiceTag,
								"Error loading image from URL: " + imageLink, e);
						throw e;
					} catch (IOException e) {
						Log.e(LentaConstants.LoggerServiceTag,
								"Error loading image from URL: " + imageLink, e);
						throw e;
					}
				}
			} else {
				throw new ImageUpdateException("Unable to load images for news with guid " + news.getGuid() + ". Image link is empty.");
			}
		} else {
			throw new ImageUpdateException("Read news from database for guid " + getNewsId() + " failed.");
		}
		
		Log.d(LentaConstants.LoggerServiceTag, "Command finished successfuly: " + getClass().getSimpleName());
	}
	
	private void prepareResultUpdated(long id) {
		result = new Bundle();
		result.putInt(BundleConstants.KEY_REQUEST_ID.name(), getRequestId());
		result.putString(BundleConstants.KEY_ACTION.name(), ServiceResultAction.IMAGE_UPDATED.name());
		result.putString(BundleConstants.KEY_NEWS_TYPE.name(), NewsType.NEWS.name());
		result.putLongArray(BundleConstants.KEY_IDS.name(), new long[]{id});
	}

	@Override
	protected Bundle getResult() {
		return result;
	}
}
