package cz.fit.lentaruand.service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;

/**
 * 
 * @author nnm
 *
 */
public class ServiceHelper {
	private static ArrayList<ServiceCallbackListener> listeners = new ArrayList<ServiceCallbackListener>();
	private static AtomicInteger requestCounter = new AtomicInteger();

	private class ServiceResultReceiver extends ResultReceiver {
		public ServiceResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (resultCode == ServiceResult.SUCCESS.ordinal()) {
				notifySucessResult(resultData);
			} else {
				notifyFailResult(resultData);
			}
		}
	}

	private ServiceResultReceiver resultReceiver;
	private Context context;
	
    public ServiceHelper(Context context, Handler handler) {
    	this.context = context;
		resultReceiver = new ServiceResultReceiver(handler);
	}

	public int updateRubric(NewsType newsType, Rubrics rubric) {
		final int requestId = requestCounter.incrementAndGet();

		startService(new ServiceIntentBuilder(context, requestId)
				.forAction(ServiceAction.UPDATE_RUBRIC).forNewsType(newsType)
				.forResultReceiver(resultReceiver).forRubric(rubric).build());
		
		return requestId;
	}
	
	public int syncRubric(NewsType newsType, Rubrics rubric) {
		final int requestId = requestCounter.incrementAndGet();

		startService(new ServiceIntentBuilder(context, requestId)
				.forAction(ServiceAction.SYNC_RUBRIC).forNewsType(newsType)
				.forResultReceiver(resultReceiver).forRubric(rubric).build());
		
		return requestId;
	}
	
	private void startService(Intent intent) {
		context.startService(intent);
	}
	
	private void notifySucessResult(Bundle bundle) {
		ServiceResultAction resultAction = ServiceResultAction.valueOf(bundle.getString(BundleConstants.KEY_ACTION.name()));
		
		switch (resultAction) {
		case DATABASE_OBJECT_CREATED:
			notifyDatabaseObjectsCreated(bundle);
			break;
		case DATABASE_OBJECT_UPDATED:
			break;
		case IMAGE_UPDATED:
			notifyImageUpdated(bundle);
			break;
		}
	}
	
	private void notifyFailResult(Bundle bundle) {
	}
	
	private void notifyDatabaseObjectsCreated(Bundle bundle) {
		int requestId = bundle.getInt(BundleConstants.KEY_REQUEST_ID.name());
		long[] ids = bundle.getLongArray(BundleConstants.KEY_IDS.name());
		NewsType newsType = NewsType.valueOf(bundle.getString(BundleConstants.KEY_NEWS_TYPE.name()));
		
		List<Long> newIds = new ArrayList<Long>(ids.length);
		
		for (long id : ids) {
			newIds.add(id);
		}
		
		List<ServiceCallbackListener> currentListeners;
		
		synchronized (listeners) {
			currentListeners = new ArrayList<ServiceCallbackListener>(listeners);
		}
		
		for (ServiceCallbackListener listener : currentListeners) {
			listener.onDatabaseObjectsCreate(requestId, newsType, newIds);
		}
	}
	
	private void notifyImageUpdated(Bundle bundle) {
		int requestId = bundle.getInt(BundleConstants.KEY_REQUEST_ID.name());
		long[] ids = bundle.getLongArray(BundleConstants.KEY_IDS.name());
		NewsType newsType = NewsType.valueOf(bundle.getString(BundleConstants.KEY_NEWS_TYPE.name()));
		
		List<Long> newIds = new ArrayList<Long>(ids.length);
		
		for (long id : ids) {
			newIds.add(id);
		}
		
		List<ServiceCallbackListener> currentListeners;
		
		synchronized (listeners) {
			currentListeners = new ArrayList<ServiceCallbackListener>(listeners);
		}
		
		for (ServiceCallbackListener listener : currentListeners) {
			listener.onImagesUpdate(requestId, newsType, newIds);
		}
	}
	
	public void addListener(ServiceCallbackListener currentListener) {
		synchronized (listeners) {
			listeners.add(currentListener);
		}
	}

	public void removeListener(ServiceCallbackListener currentListener) {
		synchronized (listeners) {
			listeners.remove(currentListener);
		}
	}
}
