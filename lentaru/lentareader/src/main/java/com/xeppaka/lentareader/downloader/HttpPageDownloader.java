package com.xeppaka.lentareader.downloader;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.URLHelper;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpPageDownloader {
	
//	private static class FlushedInputStream extends FilterInputStream {
//	    public FlushedInputStream(InputStream inputStream) {
//	        super(inputStream);
//	    }
//
//		@Override
//		public long skip(long n) throws IOException {
//			long totalBytesSkipped = 0L;
//			while (totalBytesSkipped < n) {
//				long bytesSkipped = in.skip(n - totalBytesSkipped);
//				if (bytesSkipped == 0L) {
//					int byteRead = read();
//					if (byteRead < 0) {
//						break; // we reached EOF
//					} else {
//						bytesSkipped = 1; // we read one byte
//					}
//				}
//				totalBytesSkipped += bytesSkipped;
//			}
//			return totalBytesSkipped;
//		}
//	}
	
	public static String download(String url) throws HttpStatusCodeException, IOException {
		final AndroidHttpClient client = AndroidHttpClient.newInstance(LentaConstants.UserAgent);
		final HttpGet getRequest = new HttpGet(url);
		getRequest.addHeader("Accept", "*/*");
		getRequest.addHeader("Accept-Encoding", "gzip,deflate,sdch");
		//getRequest.addHeader("Host", "lenta.ru");
		
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != HttpStatus.SC_OK) {
				Log.e(LentaConstants.LoggerMainAppTag, "Error downloading page: " + url + ", returned status http code: " + statusCode);
				throw new HttpStatusCodeException(url, statusCode);
			}
			
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
//                final Header contentEncoding = response.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getName().equalsIgnoreCase("gzip")) {
//                    final InputStream ungzippedStream = AndroidHttpClient.getUngzippedContent(entity);
//                    final BufferedReader reader = new BufferedReader(new InputStreamReader(ungzippedStream));
//
//                } else {
                    return EntityUtils.toString(entity, "UTF-8");
//                }
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
		
		return null;
	}
	
	public static Page downloadRss(Rubrics rubric, NewsType type) throws HttpStatusCodeException, IOException {
        String url = URLHelper.getRssForRubric(rubric, type);
		String pageText = download(url);
		
        return new Page(url, rubric, type, pageText);
	}

    public static Page downloadXml(Rubrics rubric, NewsType type) throws HttpStatusCodeException, IOException {
        String url = URLHelper.getXmlForRubric(rubric, type);
        String pageText = download(url);

        return new Page(url, rubric, type, pageText);
    }
}
