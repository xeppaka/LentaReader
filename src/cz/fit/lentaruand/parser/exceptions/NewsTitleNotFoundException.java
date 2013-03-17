package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;


public class NewsTitleNotFoundException extends PageParseException {
	private static final long serialVersionUID = 8016537105514853244L;

	public NewsTitleNotFoundException(URL url, String regex, String message) {
		super(url, regex, message);
	}

	public NewsTitleNotFoundException(URL url, String regex) {
		this(url, regex, "Error occured during parsing page. Regex is unable to find news title.");
	}
}
