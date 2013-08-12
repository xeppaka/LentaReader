package cz.fit.lentaruand.service;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.utils.LentaConstants;

/**
 * Singleton which exposes a simple asynchronous API to be used by the user
 * interface. 
 * Prepare and send the Service request: + 
 * Check if the method is already pending; + 
 * Create the request Intent; + 
 * Add the operation type and a unique request id; + 
 * Add the method specific parameters; 
 * Add the binder callback; + 
 * Call startService(Intent); + 
 * Return the request id. + 
 * Handle the callback from the service: + 
 * Dispatch callbacks to the user interface listeners; + 
 * methods should be called from fragment or activity. +
 * 
 * @author TheWalkingDelirium
 * @see http://bit.ly/15amlM4
 * @see LentaService
 * 
 */

public class ServiceHelper {

	Context context;

//	private Application application;
	
	private ArrayList<ServiceCallbackListener> currentListeners = new ArrayList<ServiceCallbackListener>();

	private SparseArray<Intent> pendingActivities = new SparseArray<Intent>();

	private AtomicInteger idCounter = new AtomicInteger();


    public ServiceHelper(Context context) {
		 this.context = context;
		 return;
	 }

	public int downloadListOfBriefNews(Rubrics rubric) {
		final int requestId = createId(); // создание ID запроса
		Intent i = createIntent(context, rubric, requestId); // создание и упаковка интента
		return runRequest(requestId, i); // добавление id запроса в реестр действующих задач, старт сервиса
	}
	
	private int createId() {
		return idCounter.getAndIncrement();
	}

	private Intent createIntent(final Context context, Rubrics rubric, final int requestId){
		Intent i = new Intent(context, LentaService.class);
		i.setAction(LentaService.ACTION_EXECUTE_DOWNLOAD_BRIEF); // hardcoded command type, temporary way
		if(rubric == null) Log.d(LentaConstants.LoggerMainAppTag, "rubric is null");
		i.putExtra(LentaService.EXTRA_RUBRIC, rubric);
		i.putExtra(LentaService.EXTRA_REQUEST_ID, requestId);
		i.putExtra(LentaService.EXTRA_STATUS_RECEIVER, new ResultReceiver(
				new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				Intent originalIntent = pendingActivities.get(requestId);
				if (isPending(requestId)) {
					if (resultCode != Processor.RESPONSE_PROGRESS) {
						pendingActivities.remove(requestId);
					}
					for (ServiceCallbackListener currentListener : currentListeners) {
						if (currentListener != null) {
							currentListener.onServiceCallback(requestId,
									originalIntent, resultCode, resultData);
						}
					}
				}
			}
		});
		return i;
	}

	private int runRequest(final int requestId, Intent i) {
		pendingActivities.append(requestId, i);
		Log.d(LentaConstants.LoggerMainAppTag, "Starting the service");
		context.startService(i);
		return requestId;
	}

	public boolean isPending(int requestId) {
		return pendingActivities.get(requestId) != null;
	}

	public void addListener(ServiceCallbackListener currentListener) {
		currentListeners.add(currentListener);
	}

	public void removeListener(ServiceCallbackListener currentListener) {
		currentListeners.remove(currentListener);
	}

}
