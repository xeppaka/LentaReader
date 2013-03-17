package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class NewsNotFoundException extends PageParseException {
	private static final long serialVersionUID = -2302452908827057235L;

	public NewsNotFoundException(URL url, String regex, String message) {
		super(url, regex, message);
	}

	public NewsNotFoundException(URL url, String regex) {
		this(url, regex, "Error occured during parsing page. Regex is unable to find news within news section.");
	}
}
