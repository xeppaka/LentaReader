package com.example.testproject;

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

public class LentaHttpImageDownloader {
	public static Bitmap downloadBitmap(String url) throws IOException {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);
		
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != HttpStatus.SC_OK) {
				Log.e("123", "Error downloading bitmap: " + url + ", returned status http code: " + statusCode);
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
