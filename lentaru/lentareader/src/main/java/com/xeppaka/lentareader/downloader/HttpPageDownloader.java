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
	
	public static String download(String url) throws HttpStatusCodeException, IOException {
		final AndroidHttpClient client = AndroidHttpClient.newInstance(LentaConstants.UserAgent);
		final HttpGet getRequest = new HttpGet(url);
		getRequest.addHeader("Accept", "*/*");
		getRequest.addHeader("Accept-Encoding", "gzip");

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != HttpStatus.SC_OK) {
				Log.e(LentaConstants.LoggerMainAppTag, "Error downloading page: " + url + ", returned status http code: " + statusCode);
				throw new HttpStatusCodeException(url, statusCode);
			}
			
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
                final Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    final InputStream ungzippedStream = AndroidHttpClient.getUngzippedContent(entity);
                    try {
                        final BufferedReader reader = new BufferedReader(new InputStreamReader(ungzippedStream));
                        final StringBuilder sb = new StringBuilder();

                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }

                        return sb.toString();
                    } finally {
                        ungzippedStream.close();
                    }
                } else {
                    return EntityUtils.toString(entity, "UTF-8");
                }
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
