package cz.fit.lentaruand.downloader;

import java.io.BufferedReader;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.util.Log;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.utils.LentaConstants;
import cz.fit.lentaruand.utils.URLHelper;

public class LentaHttpPageDownloader {
	static class FlushedInputStream extends FilterInputStream {
	    public FlushedInputStream(InputStream inputStream) {
	        super(inputStream);
	    }

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int byteRead = read();
					if (byteRead < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
	
	public static Page downloadPage(String url) throws HttpStatusCodeException, IOException {
		final AndroidHttpClient client = AndroidHttpClient.newInstance(LentaConstants.UserAgent);
		final HttpGet getRequest = new HttpGet(url);
		//getRequest.addHeader("Accept", "text/html,application/xml");
		//getRequest.addHeader("Accept-Encoding", "gzip");
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
				InputStream is = null;
				try {
					is = entity.getContent();
					String line = null;
					final BufferedReader br = new BufferedReader(new InputStreamReader(new FlushedInputStream(is)));
					final StringBuilder result = new StringBuilder();
					
					while ((line = br.readLine()) != null) {
						result.append(line);
					}
					
					return new Page(url, result.toString());
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
	
	public static Page downloadPage(Rubrics rubric, NewsType type) throws HttpStatusCodeException, IOException {
		Page page = downloadPage(URLHelper.getRssForRubric(rubric, type));
		
		if (page == null) {
			return page;
		}
		
		page.setRubric(rubric);
		page.setType(type);
		
		return page;
	}
}
