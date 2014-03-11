package com.xeppaka.lentareader.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author nnm
 *
 */
public class ServiceHelper {
	private static Map<Integer, ServiceRequest> serviceRequests = new HashMap<Integer, ServiceRequest>();
	private static AtomicInteger requestCounter = new AtomicInteger();

    private static final Object sync = new Object();

    /**
     * Represents request to the service
     */
    private static class ServiceRequest {
        private Intent intent;
        private List<AsyncListener<Void>> listeners;

        private ServiceRequest(Intent intent, AsyncListener<Void> listener) {
            this.intent = intent;

            this.listeners = new ArrayList<AsyncListener<Void>>(1);
            addListener(listener);
        }

        public void addListener(AsyncListener<Void> listener) {
            if (listener != null) {
                this.listeners.add(listener);
            }
        }

        public List<AsyncListener<Void>> getListeners() {
            return listeners;
        }

        public Intent getIntent() {
            return intent;
        }
    }

	private static class ServiceResultReceiver extends ResultReceiver {
		public ServiceResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int requestId, Bundle resultData) {
            ServiceRequest request;

            synchronized (sync) {
                request = serviceRequests.get(requestId);
                serviceRequests.remove(requestId);
            }

            if (request == null) {
                Log.e(LentaConstants.LoggerServiceTag, "Result is received with unknown request id");
                return;
            }

            LentaService.Result result = LentaService.Result.valueOf(resultData.getString(BundleConstants.RESULT.name()));
            List<AsyncListener<Void>> requestListeners = request.getListeners();

            if (requestListeners.isEmpty()) {
                return;
            }

            switch (result) {
                case SUCCESS:
                    for (AsyncListener<Void> listener : requestListeners) {
                        listener.onSuccess(null);
                    }
                    break;
                case FAILURE:
                    Exception ex = (Exception)resultData.getSerializable(BundleConstants.EXCEPTION.name());

                    for (AsyncListener listener : requestListeners) {
                        listener.onFailure(ex);
                    }
                    break;
            }
		}
	}

	private Context context;
    private final ServiceResultReceiver resultReceiver;

    public ServiceHelper(Context context, Handler handler) {
    	this.context = context;
        this.resultReceiver = new ServiceResultReceiver(handler);
	}

	public void updateRubric(NewsType newsType, Rubrics rubric, boolean scheduled, AsyncListener<Void> asyncListener) {
		final int requestId = requestCounter.incrementAndGet();

        Uri uri = LentaService.BASE_URI.buildUpon().appendPath(newsType.name()).appendPath(rubric.name()).build();
        Intent intent = new Intent(LentaService.Action.UPDATE_RUBRIC.name(), uri, context, LentaService.class);
        intent.putExtra(LentaService.INTENT_RESULT_RECEIVER_NAME, resultReceiver);
        intent.putExtra(LentaService.INTENT_REQUEST_ID_NAME, requestId);

        if (scheduled) {
            intent.putExtra(LentaService.INTENT_SCHEDULED_NAME, true);
        }

        synchronized (sync) {
            for (ServiceRequest request : serviceRequests.values()) {
                if (request.getIntent().filterEquals(intent)) {
                    request.addListener(asyncListener);
                    return;
                }
            }

            serviceRequests.put(requestId, new ServiceRequest(intent, asyncListener));
        }

        startService(intent);
	}

    public void updateRubrics(NewsType newsType, List<Rubrics> rubric, boolean withNotification, AsyncListener<Void> asyncListener) {
//        final int requestId = requestCounter.incrementAndGet();
//
//        Uri uri = LentaService.BASE_URI.buildUpon().appendPath(newsType.name()).appendPath(rubric.name()).build();
//        Intent intent = new Intent(LentaService.Action.UPDATE_RUBRIC.name(), uri, context, LentaService.class);
//        intent.putExtra(LentaService.INTENT_RESULT_RECEIVER_NAME, resultReceiver);
//        intent.putExtra(LentaService.INTENT_REQUEST_ID_NAME, requestId);
//
//        if (withNotification) {
//            intent.putExtra(LentaService.INTENT_SCHEDULED_NAME, true);
//        }
//
//        synchronized (sync) {
//            for (ServiceRequest request : serviceRequests.values()) {
//                if (request.getIntent().filterEquals(intent)) {
//                    request.addListener(asyncListener);
//                    return;
//                }
//            }
//
//            serviceRequests.put(requestId, new ServiceRequest(intent, asyncListener));
//        }
//
//        startService(intent);
    }

	private void startService(Intent intent) {
		context.startService(intent);
	}
}
