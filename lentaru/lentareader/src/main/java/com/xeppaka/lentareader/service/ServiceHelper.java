package com.xeppaka.lentareader.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

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
        private List<Callback> listeners;

        private ServiceRequest(Intent intent, Callback listener) {
            this.intent = intent;

            this.listeners = new ArrayList<Callback>(4);
            this.listeners.add(listener);
        }

        public List<Callback> getListeners() {
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
            List<Callback> requestListeners = request.getListeners();

            if (requestListeners.isEmpty()) {
                return;
            }

            switch (result) {
                case SUCCESS:
                    for (Callback listener : requestListeners) {
                        listener.onSuccess();
                    }
                    break;
                case FAILURE:
                    for (Callback listener : requestListeners) {
                        listener.onFailure();
                    }
                    break;
            }
		}
	}

	private Context context;
	
    public ServiceHelper(Context context, Handler handler) {
    	this.context = context;
	}

	public void updateRubric(NewsType newsType, Rubrics rubric, Callback callback) {
		final int requestId = requestCounter.incrementAndGet();

        Uri uri = LentaService.BASE_URI.buildUpon().appendPath(newsType.name()).appendPath(rubric.name()).build();
        Intent intent = new Intent(LentaService.Action.UPDATE_RUBRIC.name(), uri, context, LentaService.class);

        synchronized (sync) {
            for (ServiceRequest request : serviceRequests.values()) {
                if (request.getIntent().filterEquals(intent)) {
                    request.getListeners().add(callback);
                    return;
                }
            }

            serviceRequests.put(requestId, new ServiceRequest(intent, callback));
        }

        startService(intent);
	}

    public void downloadImage(String url, Callback callback) {
        final int requestId = requestCounter.incrementAndGet();

        Intent intent = new Intent(LentaService.Action.DOWNLOAD_IMAGE.name(), Uri.EMPTY, context, LentaService.class);
        intent.putExtra("url", url);

        synchronized (sync) {
            for (ServiceRequest request : serviceRequests.values()) {
                if (request.getIntent().filterEquals(intent) && url.equals(request.getIntent().getStringExtra("url"))) {
                    request.getListeners().add(callback);
                    return;
                }
            }

            serviceRequests.put(requestId, new ServiceRequest(intent, callback));
        }

        startService(intent);
    }

	private void startService(Intent intent) {
		context.startService(intent);
	}
}
