package com.xeppaka.lentareader.downloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;

public class LentaHttpImageDownloader {
    public static Bitmap downloadBitmap(String url) throws HttpStatusCodeException, IOException {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
        HttpConnectionParams.setSoTimeout(httpParameters, 4000);

        final AndroidHttpClient client = AndroidHttpClient.newInstance(LentaConstants.UserAgent);
        final HttpGet getRequest = new HttpGet(url);
        getRequest.setParams(httpParameters);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.e(LentaConstants.LoggerMainAppTag, "Error downloading bitmap: " + url + ", returned status http code: " + statusCode);
                throw new HttpStatusCodeException(url, statusCode);
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream is = null;
                try {
                    is = entity.getContent();
                    return BitmapFactory.decodeStream(is);
                } finally {
                    if (is != null) {
                        is.close();
                    }

                    entity.consumeContent();
                }
            }
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return null;
    }
}
