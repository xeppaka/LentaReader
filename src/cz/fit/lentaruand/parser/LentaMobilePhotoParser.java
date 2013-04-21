package cz.fit.lentaruand.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import cz.fit.lentaruand.downloader.Page;
import cz.fit.lentaruand.parser.exceptions.MobilePhotoParseException;

/**
 * LentaMobilePhotoParser is NewsParser implementation that can parse mobile
 * version of the photo news page.
 * 
 * @author kacpa01
 * 
 */
public class LentaMobilePhotoParser implements NewsParser<MobilePhoto> {
	private static final Logger logger = Logger.getLogger(LentaMobilePhotoParser.class.getName());
	
	private static final Pattern PHOTO_SECOND_TITLE = Pattern.compile("<div class=\"b-topic__rightcol\">(.+?)</div>");
	private static final int PHOTO_SECOND_TITLE_GROUPS = 1;
	private static final Pattern PHOTO_OBJECT = Pattern.compile("<li class=\"b-gallery__item\">(.+?)</li>");
	private static final int PHOTO_OBJECT_GROUPS = 1;
	private static final Pattern PHOTO_URL = Pattern.compile("<img.+?data-index=\"(\\d+?)\"\\s+?src=\"(.+?)\"");
	private static final int PHOTO_URL_GROUPS = 2;
	private static final Pattern PHOTO_CAPTION = Pattern.compile("<div class=\"b-gallery__item__caption\">(.+?)</div>");
	private static final int PHOTO_CAPTION_GROUPS = 1;
	private static final Pattern PHOTO_CREDITS = Pattern.compile("<div class=\"b-gallery__item__credits\">(.+?)</div>");
	private static final int PHOTO_CREDITS_GROUPS = 1;
	private static final Pattern PHOTO_DESCRIPTION = Pattern.compile("<div class=\"b-gallery__item__alt\">(.+?)</div>");
	private static final int PHOTO_DESCRIPTION_GROUPS = 1;
	
	@Override
	public MobilePhoto parse(Page page) throws MobilePhotoParseException {
		String secondTitle = null;
		Collection<MobilePhotoImage> photos = new ArrayList<MobilePhotoImage>();
		
		Iterator<List<String>> it = ParseHelper.createParser(page.getText(), PHOTO_SECOND_TITLE, PHOTO_SECOND_TITLE_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			secondTitle = val.get(1);
		}
		
		it = ParseHelper.createParser(page.getText(), PHOTO_OBJECT, PHOTO_OBJECT_GROUPS).iterator();

		while(it.hasNext()) {
			List<String> val = it.next();
			String photoObjectStr = val.get(1);
			
			int index = 0;
			String url = null;
			String title = null;
			String description = null;
			String credits = null;
			
			Iterator<List<String>> photoObjIt = ParseHelper.createParser(photoObjectStr, PHOTO_URL, PHOTO_URL_GROUPS).iterator();
			
			if (photoObjIt.hasNext()) {
				List<String> photoObjVal = photoObjIt.next();
				url = photoObjVal.get(1);
				index = Integer.valueOf(photoObjVal.get(2));
			} else {
				logger.log(Level.SEVERE, "Error parsing mobile photo url='" + page.getUrl().toExternalForm() + "'. Image URL and index is not found.");
				throw new MobilePhotoParseException(page.getUrl(), PHOTO_URL.pattern());
			}
			
			photoObjIt = ParseHelper.createParser(photoObjectStr, PHOTO_CAPTION, PHOTO_CAPTION_GROUPS).iterator();
			
			if (photoObjIt.hasNext()) {
				List<String> photoObjVal = photoObjIt.next();
				title = photoObjVal.get(1);
			}
			
			photoObjIt = ParseHelper.createParser(photoObjectStr, PHOTO_CREDITS, PHOTO_CREDITS_GROUPS).iterator();
			
			if (photoObjIt.hasNext()) {
				List<String> photoObjVal = photoObjIt.next();
				credits = photoObjVal.get(1);
			}
			
			photoObjIt = ParseHelper.createParser(photoObjectStr, PHOTO_DESCRIPTION, PHOTO_DESCRIPTION_GROUPS).iterator();
			
			if (photoObjIt.hasNext()) {
				List<String> photoObjVal = photoObjIt.next();
				description = photoObjVal.get(1);
			}
			
			photos.add(new MobilePhotoImage(index, url, title, credits, description));
		}
		
		return new MobilePhoto(secondTitle, photos);
	}
}
