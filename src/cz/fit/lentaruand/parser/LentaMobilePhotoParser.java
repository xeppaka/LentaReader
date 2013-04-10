package cz.fit.lentaruand.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import cz.fit.lentaruand.data.PhotoObject;
import cz.fit.lentaruand.downloader.Page;
import cz.fit.lentaruand.parser.exceptions.MobileArticleParseException;

public class LentaMobilePhotoParser implements NewsParser<MobilePhoto> {
	private static final Logger logger = Logger.getLogger(LentaMobilePhotoParser.class.getName());
	
	private static final Pattern PHOTO_SECOND_TITLE = Pattern.compile("<div class=\"b-topic__rightcol\">(.+?)</div>");
	private static final int PHOTO_SECOND_TITLE_GROUPS = 1;
	private static final Pattern PHOTO_OBJECT = Pattern.compile("<div class=\"b-gallery__item__img\"><img (alt=\" \")?? data-index=\"(\\d+)\" src=\"(.+?)\"></div><div class=\"b-gallery__item__caption\">(.+?)</div><div class=\"b-gallery__item__credits\">(.+?)</div>");
	private static final int PHOTO_OBJECT_GROUPS = 1;
	
	@Override
	public MobilePhoto parse(Page page) throws MobileArticleParseException {
		String secondTitle = null;
		Collection<PhotoObject> photos = new ArrayList<PhotoObject>();
		
		Iterator<List<String>> it = ParseHelper.createParser(page.getText(), PHOTO_SECOND_TITLE, PHOTO_SECOND_TITLE_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			secondTitle = val.get(1);
		}
		
//		it = ParseHelper.createParser(page.getText(), ARTICLE_IMAGE_CAPTION, ARTICLE_IMAGE_CAPTION_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			imageCaption = val.get(1);
//		}
//
//		it = ParseHelper.createParser(page.getText(), ARTICLE_IMAGE_CREDITS, ARTICLE_IMAGE_CREDITS_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			imageCredits = val.get(1);
//		}
//
//		it = ParseHelper.createParser(page.getText(), ARTICLE_BODY, ARTICLE_BODY_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			text = val.get(1);
//		} else {
//			logger.log(Level.SEVERE, "Error parsing url='" + page.getUrl().toExternalForm() + "'");
//			throw new MobileArticleParseException(page.getUrl(), ARTICLE_BODY.pattern());
//		}
		
		return new MobilePhoto(secondTitle, photos);
	}
}
