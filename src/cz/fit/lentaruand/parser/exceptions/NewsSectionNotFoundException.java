package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class NewsSectionNotFoundException extends PageParseException {
	private static final long serialVersionUID = -2490367658978653626L;

	public NewsSectionNotFoundException(URL url, String regex, String message) {
		super(url, regex, message);
	}

	public NewsSectionNotFoundException(URL url, String regex) {
		this(url, regex, "Error occured during parsing page. News section is not found.");
	}
}
