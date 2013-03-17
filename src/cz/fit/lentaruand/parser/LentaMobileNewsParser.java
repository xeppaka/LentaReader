package cz.fit.lentaruand.parser;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import cz.fit.lentaruand.Page;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.parser.exceptions.NewsDateNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsDateParseException;
import cz.fit.lentaruand.parser.exceptions.NewsImageCreditsNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsImageNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsImageTitleNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsTextNotFoundException;
import cz.fit.lentaruand.parser.exceptions.NewsTitleNotFoundException;

public class LentaMobileNewsParser implements NewsParser<News> {
	private static final Logger logger = Logger.getLogger(LentaMobileNewsParser.class.getName());
	
	private static final Pattern NEWS_DATE = Pattern.compile("<time.+?datetime=\"(.+?)\".+?</time>");
	private static final int NEWS_DATE_GROUPS = 1;
	
	private static final Pattern NEWS_TITLE = Pattern.compile("<h\\d class=\"b-topic__title\">(.+?)</h\\d>");
	private static final int NEWS_TITLE_GROUPS = 1;
	
	private static final Pattern NEWS_IMAGE = Pattern.compile("<div class=\"b-topic__title-image\">.*?<img.+?src=\"(.+?)\"");
	private static final int NEWS_IMAGE_GROUPS = 1;
	
	private static final Pattern NEWS_IMAGE_TITLE = Pattern.compile("<div class=\"b-label__caption\">(.*?)</div>");
	private static final int NEWS_IMAGE_TITLE_GROUPS = 1;
	
	private static final Pattern NEWS_IMAGE_CREDITS = Pattern.compile("<div class=\"b-label__credits\">(.*?)</div>");
	private static final int NEWS_IMAGE_CREDITS_GROUPS = 1;
	
	private static final Pattern NEWS_TEXT = Pattern.compile("<div class=\"b-text\">(.+?)</div>");
	private static final int NEWS_TEXT_GROUPS = 1;
	
	@Override
	public News parse(Page page) throws NewsDateNotFoundException,
			NewsDateParseException, NewsTitleNotFoundException,
			NewsImageNotFoundException, NewsImageTitleNotFoundException,
			NewsImageCreditsNotFoundException, NewsTextNotFoundException {
//		Date date = null;
//		String title = null;
//		String imageUrl = null;
//		String imageTitle = null;
//		String imageCredits = null;
//		String text = null;
//		
//		Iterator<List<String>> it = GeneralParser.createParser(page.getText(), NEWS_DATE, NEWS_DATE_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			date = LentaDateParser.parse(val.get(1));
//		} else {
//			throw new NewsDateNotFoundException(page.getUrl(), NEWS_DATE.toString());
//		}
//		
//		it = GeneralParser.createParser(page.getText(), NEWS_TITLE, NEWS_TITLE_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			title = val.get(1);
//		} else {
//			throw new NewsTitleNotFoundException(page.getUrl(), NEWS_TITLE.toString());
//		}
//		
//		it = GeneralParser.createParser(page.getText(), NEWS_IMAGE, NEWS_IMAGE_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			imageUrl = val.get(1);
//		} else {
//			throw new NewsImageNotFoundException(page.getUrl(), NEWS_IMAGE.toString());
//		}
//		
//		it = GeneralParser.createParser(page.getText(), NEWS_IMAGE_TITLE, NEWS_IMAGE_TITLE_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			imageTitle = val.get(1);
//		} else {
//			throw new NewsImageTitleNotFoundException(page.getUrl(), NEWS_IMAGE_TITLE.toString());
//		}
//		
//		it = GeneralParser.createParser(page.getText(), NEWS_IMAGE_CREDITS, NEWS_IMAGE_CREDITS_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			imageCredits = val.get(1);
//		} else {
//			throw new NewsImageCreditsNotFoundException(page.getUrl(), NEWS_IMAGE_CREDITS.toString());
//		}
//		
//		it = GeneralParser.createParser(page.getText(), NEWS_TEXT, NEWS_TEXT_GROUPS).iterator();
//
//		if (it.hasNext()) {
//			List<String> val = it.next();
//			text = val.get(1);
//		} else {
//			throw new NewsTextNotFoundException(page.getUrl(), NEWS_TEXT.toString());
//		}
//		
//		return new News(URLHelper.getPath(page.getUrl()), title, imageUrl, imageTitle, imageCredits, text, date);
		
		return null;
	}

	@Override
	public String toString() {
		return "NEWS";
	}
}
