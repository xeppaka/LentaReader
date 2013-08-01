package cz.fit.lentaruand.service;

import java.io.IOException;
import java.util.List;

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

// TODO updating the DB flags
/**
 * 
 * the role is to mirror the state of server resources in local database. The
 * role is to say "okay, this is the state of that particular resource." by
 * setting flags using content provider. e.g. set "downloading", "updating",
 * "parsing", "done" according to the pattern methods should be called from
 * Service entity.
 * 
 * @author TheWalkingDelirium
 * @see presentation http://bit.ly/15amlM4 slide 11
 * @see LentaService
 * @see Downloader
 */

public class Processor {
	public static final int RESPONSE_SUCCESS = 0;
	public static final int RESPONSE_FAILURE = 1;
	public static final int RESPONSE_PROGRESS = 2;

	private	LentaNewsDownloader lnd;
	private ResultReceiver resultReceiver;
	private LentaService service;

	public Processor() {
		lnd = new LentaNewsDownloader();
	}
	
	public Processor(LentaService service) {
		lnd = new LentaNewsDownloader();
		this.service = service;
	}

	public void downloadRubricBrief(Rubrics rubric,
			ResultReceiver receiver) { // TODO DB interaction		
		this.resultReceiver = receiver;		
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
		
		ContentResolver cr = service.getApplicationContext().getContentResolver();
		Dao<News> newsDao = NewsDao.getInstance(cr);
		newsDao.create(news);
		
		Bundle b = new Bundle();
		b.putString("EXTRA_STRING", "downloaded");
		sendUpdate(RESPONSE_SUCCESS, b); // sending message to subscribed activities or another components
		return;
	}

	public void downloadFull(News brief) { // TODO DB interaction
		
	}

	private void sendUpdate(int resultCode, Bundle data) {
		if (resultReceiver != null) {
			resultReceiver.send(resultCode, data);
		}
	}

}
