package com.xeppaka.lentareader.utils;

import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;

import java.net.MalformedURLException;

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
	 * @return URL that can be used to download RSS page.
	 */
	public static String getRssForRubric(Rubrics rubric, NewsType type) {
		return LentaConstants.LENTA_URL_ROOT + rubric.getRssPath(type);
	}

    public static String getXmlForRubric(Rubrics rubric, NewsType type) {
        if (LentaConstants.DEVELOPER_MODE) {
            return LentaConstants.OWNSERVER_URL_ROOT_JOLLA + rubric.getXmlPath(type);
        } else {
            return LentaConstants.OWNSERVER_URL_ROOT + rubric.getXmlPath(type);
        }
    }

    public static String getXmlForRubric(Rubrics rubric, NewsType type, long fromDate) {
        if (LentaConstants.DEVELOPER_MODE) {
            return LentaConstants.OWNSERVER_URL_ROOT_JOLLA + rubric.getXmlPath(type) + String.valueOf(fromDate) + ".xml";
        } else {
            return LentaConstants.OWNSERVER_URL_ROOT + rubric.getXmlPath(type) + String.valueOf(fromDate) + ".xml";
        }
    }

	/**
	 * Returns URL to mobile version of the site created from the link to the
	 * full version.
	 * 
	 * @param link
	 *            is the link to the full version of the site (e.g.
	 *            http://lenta.ru/news... )
	 * @return URL that can be used to download mobile page.
	 */
	public static String createMobileUrl(String link) {
		String fullUrl = link.replaceFirst("http://", "http://m.");
		
		return fullUrl;
	}
}