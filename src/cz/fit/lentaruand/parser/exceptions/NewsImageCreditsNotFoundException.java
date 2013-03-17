package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;


public class NewsImageCreditsNotFoundException extends PageParseException {
	private static final long serialVersionUID = 8016537105514853244L;

	public NewsImageCreditsNotFoundException(URL url, String regex, String message) {
		super(url, regex, message);
	}

	public NewsImageCreditsNotFoundException(URL url, String regex) {
		this(url, regex, "Error occured during parsing page. Regex is unable to find news image credits.");
	}
}
