package cz.fit.lentaruand.site;

import java.net.MalformedURLException;
import java.net.URL;

import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;

public class URLHelper {
	public static URL getRssForRubric(Rubrics rubric, NewsType type) throws MalformedURLException {
		if (rubric == null)
			throw new IllegalArgumentException("Argument rubric must not be null");
		
		return new URL(LentaSite.URL_ROOT + "/" + rubric.getRssPath(type));
	}
	
	public static URL createMobileUrl(String link) throws MalformedURLException {
		String fullUrl = link.replaceFirst("http://", "http://m.");
		
		return new URL(fullUrl);
	}
}
