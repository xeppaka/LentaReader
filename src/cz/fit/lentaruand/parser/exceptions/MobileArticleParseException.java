package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class MobileArticleParseException extends PageParseException {
	private static final long serialVersionUID = 5055001842791449682L;

	public MobileArticleParseException(URL url, String regex,
			String message) {
		super(url, regex, message);
	}

	public MobileArticleParseException(URL url, String regex) {
		this(url, regex, "Error occured during parsing mobile article page.");
	}
}
