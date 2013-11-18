package com.xeppaka.lentareader.service.commands;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.objects.NewsDao;
import com.xeppaka.lentareader.data.dao.objects.StrongBitmapReference;
import com.xeppaka.lentareader.downloader.LentaHttpImageDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.service.BundleConstants;
import com.xeppaka.lentareader.service.ServiceResultAction;
import com.xeppaka.lentareader.service.commands.exceptions.ImageUpdateException;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.io.IOException;

public class RetrieveNewsImageServiceCommand extends RetrieveImageServiceCommand<News> {
	private Bundle result;

	public RetrieveNewsImageServiceCommand(int requestId, News newsObject,
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
			String imageLink = news.getImageLink();
			
			if (imageLink != null && !TextUtils.isEmpty(imageLink)) {
				try {
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
			} else {
				throw new ImageUpdateException("Image link is empty for news with guid " + news.getGuid());
			}
		} else {
			throw new ImageUpdateException("Cannot find in database news with guid " + getNewsId());
		}
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
