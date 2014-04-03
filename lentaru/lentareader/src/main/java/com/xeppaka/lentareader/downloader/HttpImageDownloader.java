package com.xeppaka.lentareader.downloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class HttpImageDownloader {
    public static Bitmap downloadBitmap(String url) throws HttpStatusCodeException, IOException {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
        HttpConnectionParams.setSoTimeout(httpParameters, 4000);

        final AndroidHttpClient client = AndroidHttpClient.newInstance(LentaConstants.UserAgent);
        final HttpGet getRequest = new HttpGet(url);
        getRequest.setParams(httpParameters);
        getRequest.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        getRequest.addHeader("Accept-Language", "en-US,en;q=0.5");
        getRequest.addHeader("Cache-Control", "max-age=0");
        getRequest.addHeader("Accept-Encoding", "gzip, deflate");

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                final Header location = response.getFirstHeader("Location");

                if (location != null) {
                    final URI prevUri = getRequest.getURI();
                    final URI newUri = new URI(prevUri.getScheme(), prevUri.getHost(), location.getValue(), null);
                    return downloadBitmap(newUri.toString());
                }
            }

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
        } catch (URISyntaxException e) {
            return null;
        } finally {
            if (client != null) {
                client.close();
            }
        }

        return null;
    }
}
