package cz.fit.lentaruand.parser.rss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LentaRssDateParser {
	private final static String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
	private final static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(pattern);
		}
	};
	
	public static Date parseDate(String dateStr) throws ParseException {
		return sdf.get().parse(dateStr);
	}
}
