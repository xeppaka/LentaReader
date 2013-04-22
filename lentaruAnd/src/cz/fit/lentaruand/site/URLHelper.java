package cz.fit.lentaruand.site;

import java.net.MalformedURLException;
import java.net.URL;

import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;

/**
 * URLHelper helps to get URLs from which news can be downloaded (e.g. get URL
 * for some rubric or get URL to mobile version of article from URL to full
 * version of article.
 * 
 * @author kacpa01
 * 
 */
public class URLHelper {
	
	/**
	 * Returns URL to RSS page for specified rubric and news type.
	 * 
	 * @param rubric
	 *            is the value from Rubrics enum that specifies news rubric.
	 * @param type
	 *            is the value from NewsType enum.
	 * @return New URL instance that can be used to download RSS page.
	 * @throws MalformedURLException
	 *             if URL cannot be created. It can happen only if some
	 *             constants in Rubrics or NewsType enums are set incorrectly.
	 */
	public static URL getRssForRubric(Rubrics rubric, NewsType type) throws MalformedURLException {
		if (rubric == null)
			throw new IllegalArgumentException("Argument rubric must not be null");
		
		return new URL(LentaSite.URL_ROOT + "/" + rubric.getRssPath(type));
	}

	/**
	 * Returns URL to mobile version of the site created from the link to the
	 * full version.
	 * 
	 * @param link
	 *            is the link to the full version of the site (e.g.
	 *            http://lenta.ru/news... )
	 * @return New URL instance that can be used to download mobile page.
	 * @throws MalformedURLException
	 *             if provided link argument is malformed URL.
	 */
	public static URL createMobileUrl(String link) throws MalformedURLException {
		String fullUrl = link.replaceFirst("http://", "http://m.");
		
		return new URL(fullUrl);
	}
}
