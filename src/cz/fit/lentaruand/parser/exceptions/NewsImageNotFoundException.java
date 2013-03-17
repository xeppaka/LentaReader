package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;


public class NewsImageNotFoundException extends PageParseException {
	private static final long serialVersionUID = 8016537105514853244L;

	public NewsImageNotFoundException(URL url, String regex, String message) {
		super(url, regex, message);
	}

	public NewsImageNotFoundException(URL url, String regex) {
		this(url, regex, "Error occured during parsing page. Regex hasn't found news title.");
	}
}
