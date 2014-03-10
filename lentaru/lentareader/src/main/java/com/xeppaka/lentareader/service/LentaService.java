package com.xeppaka.lentareader.service;

import android.app.Service;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.service.commands.ServiceCommand;
import com.xeppaka.lentareader.service.commands.UpdateRubricServiceCommand;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.util.Comparator;
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

    public enum Action {
        UPDATE_RUBRIC
    }

    public enum Result {
        SUCCESS,
        FAILURE
    }

    public static final String INTENT_RESULT_RECEIVER_NAME = "resultReceiver";
    public static final String INTENT_REQUEST_ID_NAME = "requestId";
    public static final String INTENT_NOTIFICATION_NAME = "notification";

    public static final int NO_REQUEST_ID = -1;

    private static final int POOL_SIZE = 2;
	private static final int KEEP_ALIVE_TIME = 20;
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

    public static final Bundle resultSuccess;

    private static final String URI_BASE_STRING = "com.xeppaka.lentareader.service";

    public static final Uri BASE_URI = new Uri.Builder().scheme("content").authority(URI_BASE_STRING).build();

    private static final int NEWS = 0;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(URI_BASE_STRING, NewsType.NEWS.name() + "/*", NEWS);

        resultSuccess = new Bundle();
        resultSuccess.putString(BundleConstants.RESULT.name(), Result.SUCCESS.name());
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
		
		final int requestId = intent.getIntExtra(INTENT_REQUEST_ID_NAME, NO_REQUEST_ID);
        final ResultReceiver receiver = intent.getParcelableExtra(INTENT_RESULT_RECEIVER_NAME);
        final boolean notification = intent.getBooleanExtra(INTENT_NOTIFICATION_NAME, false);
		final Action action = Action.valueOf(intent.getAction());

        switch (action) {
            case UPDATE_RUBRIC:
                NewsType newsType = getNewsType(intent.getData());
                Rubrics rubric = getRubric(intent.getData());

                if (newsType == null || rubric == null) {
                    failStart(requestId, receiver);
                    stopSelf(startId);
                    return START_NOT_STICKY;
                }

                updateRubric(requestId, rubric, newsType, notification, receiver);
                break;
        }

		return START_NOT_STICKY;
	}

    private void failStart(int requestId, ResultReceiver receiver) {
        if (receiver != null) {
            receiver.send(requestId, createFailResult(null));
        }
    }

    private Rubrics getRubric(Uri uri) {
        return Rubrics.valueOf(uri.getLastPathSegment());
    }

    private NewsType getNewsType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case NEWS:
                return NewsType.NEWS;
            default:
                return null;
        }
    }

	private void updateRubric(int requestId, Rubrics rubric, NewsType newsType, boolean notification, ResultReceiver resultReceiver) {
		UpdateRubricServiceCommand command = new UpdateRubricServiceCommand(requestId, newsType, rubric, notification, getApplicationContext(), resultReceiver);
		executor.execute(command);
	}

    public static Bundle createFailResult(Exception ex) {
        final Bundle resultFail = new Bundle();
        resultFail.putString(BundleConstants.RESULT.name(), Result.FAILURE.name());

        if (ex != null)
            resultFail.putSerializable(BundleConstants.EXCEPTION.name(), ex);

        return resultFail;
    }

    public static Bundle createFailResult() {
        return createFailResult(null);
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}