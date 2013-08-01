package cz.fit.lentaruand.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
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

public class LentaService extends IntentService {
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
	
	Processor processor = new Processor(this);
	
	public LentaService(){
		super("Lenta Download Service");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
	    Log.d(LentaConstants.LoggerServiceTag, "Service started!");
	    return super.onStartCommand(intent,flags,startId);
	}
	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns,
	 * IntentService stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d(LentaConstants.LoggerServiceTag, "Handled intent!");
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action)) {
			if(action == ACTION_EXECUTE_DOWNLOAD_BRIEF){
				Log.d(LentaConstants.LoggerServiceTag, "starting download");
				processor.downloadRubricBrief(getRubric(intent), getReceiver(intent)); // 
				Log.d(LentaConstants.LoggerServiceTag, "Done. Stopping myself");
				stopSelf();				 
			}
		}
	}
	
	private ResultReceiver getReceiver(Intent intent) {
		return intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
	}

	private Rubrics getRubric(Intent intent) {
		Rubrics rubric = (Rubrics) intent.getSerializableExtra(EXTRA_RUBRIC);
		if(rubric == null) Log.d(LentaConstants.LoggerServiceTag, "rubric is null");
		return rubric;
	}
}
