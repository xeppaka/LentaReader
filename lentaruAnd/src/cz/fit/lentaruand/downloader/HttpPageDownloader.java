package cz.fit.lentaruand.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.site.URLHelper;

public class HttpPageDownloader {
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
	
	public static Page downloadPage(Rubrics rubric, NewsType type) throws IOException {
		Page page = downloadPage(URLHelper.getRssForRubric(rubric, type));
		page.setRubric(rubric);
		page.setType(type);
		
		return page;
	}
}
