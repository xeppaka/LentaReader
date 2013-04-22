package cz.fit.lentaruand.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PageDownloader {
	public static Page downloadPage(URL url) throws IOException {
		if (!url.getProtocol().equals("http"))
			throw new IllegalArgumentException("url must have 'http' protocol.");
		
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		try {
			connection.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			String line;
			
			StringBuilder sb = new StringBuilder();
			
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			
			return new Page(url, sb.toString());
		} finally {
			connection.disconnect();
		}
	}
}
