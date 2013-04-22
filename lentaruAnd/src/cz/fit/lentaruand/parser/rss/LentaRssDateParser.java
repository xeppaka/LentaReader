package cz.fit.lentaruand.parser.rss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * LentaRssDateParser is the utility class for parsing date string on Lenta.ru site.
 * 
 * @author kacpa01
 *
 */
public class LentaRssDateParser {
	private final static String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
	private final static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(pattern);
		}
	};

	/**
	 * Parses date string from Lenta.ru site.
	 * 
	 * @param dateStr is the string to parse.
	 * @return Date object instance with proper date and time set.
	 * @throws ParseException if date cannot be parsed.
	 */
	public static Date parseDate(String dateStr) throws ParseException {
		return sdf.get().parse(dateStr);
	}
}
