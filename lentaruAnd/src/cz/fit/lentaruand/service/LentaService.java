package cz.fit.lentaruand.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.utils.LentaConstants;

/**
 * 
 * Forward path [call from ServiceHelper]: 
 * receives the Intent sent by the ServiceHelper;
 * unpacks the content of incoming intent and start the corresponding method 
 * Return path: 
 * handles the Processor callback and invokes the ServiceHelper binder callback. 
 * Invoke a binder callback. (think of it as an interface that was passed in the request intent, in this case -
 * ResultReceiver).
 * 
 * @author TheWalkingDelirium
 * 
 * @see http://bit.ly/15amlM4
 * @see ServiceHelper
 * @see Processor
 */

public class LentaService extends Service {
	
	private static final int NUM_THREADS = 4;
	 
	public static final String PACKAGE = "cz.fit.lentaruand.service";
	public static final String ACTION_EXECUTE_COMMAND = PACKAGE.concat(".ACTION_EXECUTE_COMMAND");
	public static final String ACTION_EXECUTE_DOWNLOAD_BRIEF = PACKAGE.concat(".ACTION_EXECUTE_DOWNLOAD_BRIEF");
	public static final String ACTION_EXECUTE_DOWNLOAD_FULL = PACKAGE.concat(".ACTION_EXECUTE_DOWNLOAD_FULL");
	public static final String ACTION_CANCEL_COMMAND = PACKAGE.concat(".ACTION_CANCEL_COMMAND");
	public static final String EXTRA_REQUEST_ID = PACKAGE.concat(".EXTRA_REQUEST_ID");
	public static final String EXTRA_STATUS_RECEIVER = PACKAGE.concat(".EXTRA_STATUS_RECEIVER");
	public static final String EXTRA_COMMAND = PACKAGE.concat(".EXTRA_COMMAND");
	public static final String EXTRA_RUBRIC = PACKAGE.concat(".EXTRA_RUBRIC");
	public static final String EXTRA_LIST = PACKAGE.concat(".EXTRA_LIST");
	
	private ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS); // поменять потом очередь приоритетов
	private SparseArray<RunningCommand> runningCommands = new SparseArray<RunningCommand>();
	Processor processor = new Processor(this);
		
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (ACTION_EXECUTE_DOWNLOAD_BRIEF.equals(intent.getAction())) {
			RunningCommand runningCommand = new RunningCommand(intent);
			synchronized (runningCommands) {
				runningCommands.append(getCommandId(intent), runningCommand);
			}
			executor.submit(runningCommand);
		}
//		if (ACTION_CANCEL_COMMAND.equals(intent.getAction())) {
//			RunningCommand runningCommand = runningCommands
//					.get(getCommandId(intent));
//			if (runningCommand != null) {
//				runningCommand.cancel();
//			}
//		}
		return START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "service created", Toast.LENGTH_SHORT).show();
		Log.d(LentaConstants.LoggerServiceTag, "Service created!");

		super.onCreate();
	}
	
	@Override
    public void onDestroy() {
		super.onDestroy();
	}
	
	private class RunningCommand implements Runnable {

		private Intent intent;

		public RunningCommand(Intent intent) {
			this.intent = intent;
		}

		public void cancel() {
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			Log.d(LentaConstants.LoggerServiceTag, "starting download");
//			processor.downloadRubricBrief(getRubric(intent), getReceiver(intent)); 
			processor.execute(intent);
			Log.d(LentaConstants.LoggerServiceTag, "Done download.");
			shutdown();
		}

		private void shutdown() {
			synchronized (runningCommands) {
				runningCommands.remove(getCommandId(intent));
				if (runningCommands.size() == 0) {
					stopSelf();
				}
			}
		}

	}
	
	private int getCommandId(Intent intent) {
		return intent.getIntExtra(EXTRA_REQUEST_ID, -1);
	}
	
	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns,
	 * IntentService stops the service, as appropriate.
	 */
//	@Override
//	protected void onHandleIntent(Intent intent) {
//
//		Log.d(LentaConstants.LoggerServiceTag, "Handled intent!");
//		String action = intent.getAction();
//		if (!TextUtils.isEmpty(action)) {
//			if(action == ACTION_EXECUTE_DOWNLOAD_BRIEF){
//				Log.d(LentaConstants.LoggerServiceTag, "starting download");
//				processor.downloadRubricBrief(getRubric(intent), getReceiver(intent)); // 
//				Log.d(LentaConstants.LoggerServiceTag, "Done. Stopping myself");
//				stopSelf();				 
//			}
//		}
//	}
}
