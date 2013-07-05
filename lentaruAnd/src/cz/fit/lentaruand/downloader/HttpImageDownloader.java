package cz.fit.lentaruand.downloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;

public class HttpImageDownloader {
	public static Bitmap downloadBitmap(URL url) throws IOException {
		if (!url.getProtocol().equals("http"))
			throw new IllegalArgumentException("url must have 'http' protocol.");
		
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		try {
			connection.connect();
			Object obj = connection.getContent();
			if (obj instanceof Bitmap) {
				return (Bitmap)obj;
			}

			return null;
		} finally {
			connection.disconnect();
		}
	}
}
