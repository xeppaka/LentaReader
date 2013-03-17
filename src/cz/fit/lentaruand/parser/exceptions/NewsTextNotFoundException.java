package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class NewsTextNotFoundException extends PageParseException {
	private static final long serialVersionUID = 8016537105514853244L;

	public NewsTextNotFoundException(URL url, String regex, String message) {
		super(url, regex, message);
	}

	public NewsTextNotFoundException(URL url, String regex) {
		this(url, regex, "Error occured during parsing page. Regex hasn't found news text.");
	}
}
