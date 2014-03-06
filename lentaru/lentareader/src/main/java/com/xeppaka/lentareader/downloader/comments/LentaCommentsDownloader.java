package com.xeppaka.lentareader.downloader.comments;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nnm on 2/27/14.
 */
public class LentaCommentsDownloader {
    private static final String URL = "http://c1n1.hypercomments.com/api/comments";
    private static final String KEY_DATA = "data";

    private static class AsyncDownloaderTaskParams {
        private AsyncListener<String> listener;
        private String xid;

        private AsyncDownloaderTaskParams(String xid, AsyncListener<String> listener) {
            this.listener = listener;
            this.xid = xid;
        }
    }

    private class AsyncDownloaderTask extends AsyncTask<AsyncDownloaderTaskParams, Void, String> {
        private AsyncListener<String> listener;
        private Exception e;

        @Override
        protected String doInBackground(AsyncDownloaderTaskParams... params) {
            AsyncDownloaderTaskParams taskParams = params[0];
            listener = taskParams.listener;

            try {
                return download(taskParams.xid);
            } catch (HttpStatusCodeException e) {
                Log.e(LentaConstants.LoggerMainAppTag, "Error while downloading comments in async task.", e);
                this.e = e;

                return null;
            } catch (IOException e) {
                Log.e(LentaConstants.LoggerMainAppTag, "Error while downloading comments in async task.", e);
                this.e = e;

                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (e == null) {
                listener.onSuccess(s);
            } else {
                listener.onFailure(e);
            }
        }
    }

    public String download(String xid) throws HttpStatusCodeException, IOException {
        final AndroidHttpClient client = AndroidHttpClient.newInstance(LentaConstants.UserAgent);
        final HttpPost postRequest = new HttpPost(URL);

        final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        final LentaCommentsPayloaderBuilder payloaderBuilder = new LentaCommentsPayloaderBuilder();
        payloaderBuilder.setXid(xid);

        nameValuePairs.add(new BasicNameValuePair(KEY_DATA, payloaderBuilder.build()));

        postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        try {
            HttpResponse response = client.execute(postRequest);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.e(LentaConstants.LoggerMainAppTag, "Error getting comments for xid: " + xid + ", returned status http code: " + statusCode);
                throw new HttpStatusCodeException(xid, statusCode);
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return null;
    }

    public AsyncTask<AsyncDownloaderTaskParams, Void, String> downloadAsync(String xid, AsyncListener<String> listener) {
        return new AsyncDownloaderTask().execute(new AsyncDownloaderTaskParams(xid, listener));
    }
}