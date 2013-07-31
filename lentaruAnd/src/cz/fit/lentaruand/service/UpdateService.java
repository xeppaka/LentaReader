package cz.fit.lentaruand.service;

import java.io.IOException;
import java.util.List;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.Dao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.parser.exceptions.ParseWithXPathException;
import cz.fit.lentaruand.utils.LentaConstants;

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
				newsDao.create(news);
				
				UpdateService.this.stopSelf();
			}
		}.start();
		
		return Service.START_NOT_STICKY;
	}
}
