package cz.fit.lentaruand.parser;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import cz.fit.lentaruand.parser.exceptions.MobileNewsParseException;
import cz.fit.lentaruand.site.Page;

public class LentaMobileNewsParser implements NewsParser<MobileNews> {
	private static final Logger logger = Logger.getLogger(LentaMobileNewsParser.class.getName());
	
	private static final Pattern NEWS_ALL = Pattern.compile("<div class=\"b-label__caption\">(.+?)</div>.*?<div class=\"b-label__credits\">(.+?)</div>.*?<div class=\"b-topic__body\">(.+?)</div>");
	private static final int NEWS_ALL_GROUPS = 3;
	
	@Override
	public MobileNews parse(Page page) throws MobileNewsParseException {
		String imageCaption = null;
		String imageCredits = null;
		String text = null;
		
		Iterator<List<String>> it = ParseHelper.createParser(page.getText(), NEWS_ALL, NEWS_ALL_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			imageCaption = val.get(1);
			imageCredits = val.get(2);
			text = val.get(3);
		} else {
			logger.log(Level.SEVERE, "Error parsing url='" + page.getUrl().toExternalForm() + "'");
			throw new MobileNewsParseException(page.getUrl(), NEWS_ALL.pattern());
		}
		
		return new MobileNews(imageCaption, imageCredits, text);
	}
}
