package cz.fit.lentaruand.downloader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.utils.LentaConstants;

public class LentaHttpImageDownloader {
	public static Bitmap downloadBitmap(String url) throws HttpStatusCodeException, IOException {
		final AndroidHttpClient client = AndroidHttpClient.newInstance(LentaConstants.UserAgent);
		final HttpGet getRequest = new HttpGet(url);
		
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
