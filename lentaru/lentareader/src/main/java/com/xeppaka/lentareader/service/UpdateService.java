package com.xeppaka.lentareader.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.objects.BitmapReference;
import com.xeppaka.lentareader.data.dao.objects.ImageDao;
import com.xeppaka.lentareader.data.dao.objects.NewsDao;
import com.xeppaka.lentareader.downloader.LentaHttpImageDownloader;
import com.xeppaka.lentareader.downloader.LentaNewsDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.exceptions.ParseWithXPathException;
import com.xeppaka.lentareader.utils.LentaConstants;

@Deprecated
public class UpdateService extends Service {
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread() {
			@Override
			public void run() {
				Log.i(LentaConstants.LoggerServiceTag, "Started thread in the service.");
				
				LentaNewsDownloader lnd = new LentaNewsDownloader();
				
				List<News> news;
				try {
					news = lnd.downloadRubricBrief(Rubrics.ECONOMICS);
				} catch (ParseWithXPathException e) {
					Log.w(LentaConstants.LoggerServiceTag, "Error downloading page, parse error.");
					return;
				} catch (IOException e) {
					Log.w(LentaConstants.LoggerServiceTag, "Error downloading page, I/O error.");
					return;
				} catch (HttpStatusCodeException e) {
					Log.w(LentaConstants.LoggerServiceTag, "Error downloading page, status code returned: " + e.getHttpStatusCode() + ".");
					return;
				}
				
				ContentResolver cr = UpdateService.this.getApplicationContext().getContentResolver();
				Dao<News> newsDao = NewsDao.getInstance(cr);
				for (News n : news) {
					newsDao.delete(n.getGuid());
				}
				
				loadImages(news);
				newsDao.create(news);
				
				UpdateService.this.stopSelf();
			}
		}.start();
		
		return Service.START_NOT_STICKY;
	}
	
	private void loadImages(Collection<News> news) {
		ImageDao imageDao = ImageDao.getInstance(getContentResolver());
		
		for (News n : news) {
			try {
				String imageLink = n.getImageLink();
				if (imageLink != null && !TextUtils.isEmpty(imageLink)) {
					if (!imageDao.imageExist(imageLink)) {
						Bitmap newBitmap = LentaHttpImageDownloader.downloadBitmap(imageLink);
						BitmapReference newBitmapRef = imageDao.create(imageLink, newBitmap);
						BitmapReference newThumbnailBitmapRef = imageDao.readThumbnail(imageLink);
						
						n.setImage(newBitmapRef);
						n.setThumbnailImage(newThumbnailBitmapRef);
					}
				}
			} catch (HttpStatusCodeException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}
