package cz.fit.lentaruand.service;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import cz.fit.lentaruand.data.IntentContent;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.service.commands.RubricUpdateServiceCommand;
import cz.fit.lentaruand.service.commands.ServiceCommand;
import cz.fit.lentaruand.utils.LentaConstants;

/**
 * 
 * Forward path [call from ServiceHelper]: receives the Intent sent by the
 * ServiceHelper; unpacks the content of incoming intent and start the
 * corresponding method Return path: handles the Processor callback and invokes
 * the ServiceHelper binder callback. Invoke a binder callback. (think of it as
 * an interface that was passed in the request intent, in this case -
 * ResultReceiver).
 * 
 * скачать картинку скачать новость обнови рубрику -- разбить на мелкие
 * 
 * @author TheWalkingDelirium
 * 
 * @see http://bit.ly/15amlM4
 * @see ServiceHelper
 * @see Processor
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
	
	Processor processor = new Processor(this);
	
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
		
		if (IntentContent.ACTION_EXECUTE_DOWNLOAD_BRIEF.getIntentContent().equals(intent.getAction())) {
			RubricUpdateServiceCommand command = new RubricUpdateServiceCommand(Rubrics.ROOT, getContentResolver(), executor, getReceiver(intent));
			executor.execute(command);
		}
		
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private ResultReceiver getReceiver(Intent intent) {
		return intent.getParcelableExtra(IntentContent.EXTRA_STATUS_RECEIVER.getIntentContent());
	}
}