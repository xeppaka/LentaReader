package com.xeppaka.lentareader.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.xeppaka.lentareader.data.DatabaseObject;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.service.ServiceIntentBuilder.ServiceIntentKey;
import com.xeppaka.lentareader.service.commands.ServiceCommand;
import com.xeppaka.lentareader.service.commands.SyncNewsServiceCommand;
import com.xeppaka.lentareader.service.commands.SyncRubricServiceCommand;
import com.xeppaka.lentareader.service.commands.UpdateRubricServiceCommand;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author nnm
 *
 */
public class LentaService extends Service {

	private static final int POOL_SIZE = 2;
	private static final int KEEP_ALIVE_TIME = 60;
	private static final TimeUnit KEEP_ALIVE_TIME_UNITS = TimeUnit.SECONDS;

	private ThreadPoolExecutor executor;
	private AtomicInteger threads = new AtomicInteger();
	
	class CountableThread extends Thread {
		public CountableThread(Runnable runnable) {
			super(runnable);
		}

		@Override
		public void run() {
			threads.incrementAndGet();
			
			super.run();
			
			if (threads.decrementAndGet() == 0) {
				Log.d(LentaConstants.LoggerServiceTag, "Stopping service: no working threads are active.");
				stopSelf();
			}
		}
	}
	
	class CountableThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable runnable) {
			return new CountableThread(runnable);
		}
	}
	
	public LentaService() {
		Comparator<Runnable> comparator = new Comparator<Runnable>() {
			@Override
			public int compare(Runnable command1, Runnable command2) {
				return ((ServiceCommand)command1).compareTo((ServiceCommand)command2);
			}
		};
		
		executor = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE,
				KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNITS,	new PriorityBlockingQueue<Runnable>(11, comparator));
		executor.allowCoreThreadTimeOut(true);
		executor.setThreadFactory(new CountableThreadFactory());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LentaConstants.LoggerServiceTag, "Starting service.");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LentaConstants.LoggerServiceTag, "Got the intent, checking the command");
		
		int requestId = intent.getIntExtra(ServiceIntentKey.REQUEST_ID.name(), ServiceIntentBuilder.NO_REQUEST_ID);
		
		ServiceAction action = ServiceAction.valueOf(intent.getStringExtra(ServiceIntentBuilder.ServiceIntentKey.SERVICE_ACTION.name()));
		NewsType newsType = NewsType.valueOf(intent.getStringExtra(ServiceIntentBuilder.ServiceIntentKey.NEWS_TYPE.name()));
		
		switch (action) {
		case UPDATE_RUBRIC:
			updateRubric(requestId, getRubric(intent), newsType, getReceiver(intent));
			break;
		case SYNC_RUBRIC:
			syncRubric(requestId, getRubric(intent), newsType, getReceiver(intent));
			break;
		case UPDATE_ITEM:
			updateItem(requestId, newsType, getItemId(intent), getReceiver(intent));
			break;
		case UPDATE_ITEM_FULL_TEXTS:
			break;
		case UPDATE_ITEM_IMAGES:
			break;
		default:
			break;
		}
		
		return START_NOT_STICKY;
	}

	private void updateRubric(int requestId, Rubrics rubric, NewsType newsType, ResultReceiver resultReceiver) {
		UpdateRubricServiceCommand command = new UpdateRubricServiceCommand(requestId, rubric, newsType, executor, getContentResolver(), resultReceiver, true);
		executor.execute(command);
	}
	
	private void syncRubric(int requestId, Rubrics rubric, NewsType newsType, ResultReceiver resultReceiver) {
		SyncRubricServiceCommand command = new SyncRubricServiceCommand(requestId, rubric, newsType, executor, getContentResolver(), resultReceiver, true);
		executor.execute(command);
	}
	
	private void updateItem(int requestId, NewsType newsType, long itemId, ResultReceiver resultReceiver) {
		switch (newsType) {
		case NEWS:
			updateNewsItem(requestId, itemId, getContentResolver(), executor, resultReceiver);
			break;
		case ARTICLE:
			break;
		case COLUMN:
			break;
		case PHOTO:
			break;
		case VIDEO:
			break;
		}
	}
	
	private void updateNewsItem(int requestId, long newsId, ContentResolver contentResolver, ExecutorService executor, ResultReceiver resultReceiver) {
		SyncNewsServiceCommand command = new SyncNewsServiceCommand(requestId, newsId, contentResolver, executor, resultReceiver, false);
		executor.execute(command);
	}
	
	private ResultReceiver getReceiver(Intent intent) {
		return intent.getParcelableExtra(ServiceIntentBuilder.ServiceIntentKey.RESULT_RECEIVER.name());
	}
	
	private Rubrics getRubric(Intent intent) {
		return Rubrics.valueOf(intent.getStringExtra(ServiceIntentKey.RUBRIC.name()));
	}
	
	private long getItemId(Intent intent) {
		return intent.getLongExtra(ServiceIntentBuilder.ServiceIntentKey.ITEM_ID.name(), DatabaseObject.ID_NONE);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}