package com.xeppaka.lentareader.parser.originalnews;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import android.util.Log;
import com.xeppaka.lentareader.downloader.Page;
import com.xeppaka.lentareader.parser.exceptions.ParseWithRegexException;
import com.xeppaka.lentareader.utils.LentaConstants;

public class LentaMobileArticleParser implements NewsParser<MobileArticle> {
	private static final Pattern ARTICLE_SECOND_TITLE = Pattern.compile("<div class=\"b-topic__rightcol\">(.+?)</div>");
	private static final int ARTICLE_SECOND_TITLE_GROUPS = 1;
	private static final Pattern ARTICLE_IMAGE_CAPTION = Pattern.compile("<div class=\"b-label__caption\">(.+?)</div>");
	private static final int ARTICLE_IMAGE_CAPTION_GROUPS = 1;
	private static final Pattern ARTICLE_IMAGE_CREDITS = Pattern.compile("<div class=\"b-label__credits\">(.+?)</div>");
	private static final int ARTICLE_IMAGE_CREDITS_GROUPS = 1;
	private static final Pattern ARTICLE_BODY = Pattern.compile("<div class=\"b-topic__body\">(.+?)</div>");
	private static final int ARTICLE_BODY_GROUPS = 1;
	
	@Override
	public MobileArticle parse(Page page) throws ParseWithRegexException {
		String secondTitle = null;
		String imageCaption = null;
		String imageCredits = null;
		String text = null;
		
		Iterator<List<String>> it = ParseHelper.createParser(page.getText(), ARTICLE_SECOND_TITLE, ARTICLE_SECOND_TITLE_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			secondTitle = val.get(1);
		}
		
		it = ParseHelper.createParser(page.getText(), ARTICLE_IMAGE_CAPTION, ARTICLE_IMAGE_CAPTION_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			imageCaption = val.get(1);
		}

		it = ParseHelper.createParser(page.getText(), ARTICLE_IMAGE_CREDITS, ARTICLE_IMAGE_CREDITS_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			imageCredits = val.get(1);
		}

		it = ParseHelper.createParser(page.getText(), ARTICLE_BODY, ARTICLE_BODY_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			text = val.get(1);
		} else {
			Log.w(LentaConstants.LoggerMainAppTag, "Error parsing body for page: " + page.getUrl() + ", body will be ommited for this news.");
		}
		
		return new MobileArticle(secondTitle, imageCaption, imageCredits, text);
	}
}
