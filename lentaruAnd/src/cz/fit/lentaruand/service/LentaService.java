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
import cz.fit.lentaruand.data.IntentContent;
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

	private static final int NUM_THREADS = 4;

	private ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS); // поменять потом очередь приоритетов на стек
	private SparseArray<RunningCommand> runningCommands = new SparseArray<RunningCommand>();
	Processor processor = new Processor(this);
	ResultReceiver resultReceiver;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		resultReceiver = getReceiver(intent);
		processor.setResultReceiver(resultReceiver);
		
		Log.d(LentaConstants.LoggerServiceTag, "Got the intent, checking the command");
		
		if (IntentContent.ACTION_EXECUTE_DOWNLOAD_BRIEF.getIntentContent().equals(intent.getAction())) {
			RunningCommand runningCommand = new RunningCommand(intent);
			Log.d(LentaConstants.LoggerServiceTag, "Service got command");
			synchronized (runningCommands) {
				if (runningCommands.get(getCommandId(intent)) == null) {
					Log.d(LentaConstants.LoggerServiceTag, "adding the task to running command array");
					runningCommands.append(getCommandId(intent), runningCommand);
				}
				else 
					return START_NOT_STICKY;
			}
			Log.d(LentaConstants.LoggerServiceTag, "submitting the task");
			executor.submit(runningCommand);
		}
		
		// if (ACTION_CANCEL_COMMAND.equals(intent.getAction())) {
		// RunningCommand runningCommand = runningCommands
		// .get(getCommandId(intent));
		// if (runningCommand != null) {
		// runningCommand.cancel();
		// }
		// }
		
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
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

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			Log.d(LentaConstants.LoggerServiceTag, "starting download");
			// processor.downloadRubricBrief(getRubric(intent),
			// getReceiver(intent));
			processor.execute(intent);
			Log.d(LentaConstants.LoggerServiceTag, "Downloaded news. Stopping myself.");
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
		return intent.getIntExtra(IntentContent.EXTRA_REQUEST_ID.getIntentContent(), -1);
	}
	
	private ResultReceiver getReceiver(Intent intent) {
		return intent.getParcelableExtra(IntentContent.EXTRA_STATUS_RECEIVER.getIntentContent());
	}
}