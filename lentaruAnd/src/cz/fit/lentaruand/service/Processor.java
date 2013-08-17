package cz.fit.lentaruand.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import cz.fit.lentaruand.data.IntentContent;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.BitmapReference;
import cz.fit.lentaruand.data.dao.Dao;
import cz.fit.lentaruand.data.dao.ImageDao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.downloader.LentaHttpImageDownloader;
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


	public ResultReceiver getResultReceiver() {
		return resultReceiver;
	}

	public void setResultReceiver(ResultReceiver resultReceiver) {
		this.resultReceiver = resultReceiver;
	}
	
	public void downloadRubricBrief(Rubrics rubric, ResultReceiver receiver) { // TODO DB interaction		
		this.resultReceiver = receiver;		
		List<News> news;		
		try {
			news = lnd.downloadRubricBrief(rubric);
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
		for (News n : news) {
			newsDao.delete(n.getGuid());
		}
		
		loadImages(news);
		newsDao.create(news);
		
		Bundle b = new Bundle();
		b.putString("EXTRA_STRING", "downloaded");
		sendUpdate(Progress.RESPONSE_SUCCESS, b); // sending message to subscribed activities or another components
		return;
	}
	
	public void loadImages(Collection<News> news) {
		ImageDao imageDao = ImageDao.getInstance(service.getContentResolver());
		
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
	
	public void loadImage(News news) {
		ImageDao imageDao = ImageDao.getInstance(service.getContentResolver());
	}
	
	public void downloadFull(News brief) { // TODO DB interaction
		
	}

	private void sendUpdate(Progress progressCode, Bundle data) {
		if (resultReceiver != null) {
			resultReceiver.send(progressCode.getValue(), data);
		}
	}

	public void execute(Intent intent) {
		if(intent.getAction().equals(IntentContent.ACTION_EXECUTE_DOWNLOAD_BRIEF.getIntentContent())){
			downloadRubricBrief(getRubric(intent), getReceiver(intent));
		}
		
	}
	
	private ResultReceiver getReceiver(Intent intent) {
		return intent.getParcelableExtra(IntentContent.EXTRA_STATUS_RECEIVER
				.getIntentContent());
	}

	private Rubrics getRubric(Intent intent) {
		Rubrics rubric = (Rubrics) intent.getSerializableExtra(IntentContent.EXTRA_RUBRIC.getIntentContent());
		if(rubric == null) Log.d(LentaConstants.LoggerServiceTag, "rubric is null");
		return rubric;
	}

}
