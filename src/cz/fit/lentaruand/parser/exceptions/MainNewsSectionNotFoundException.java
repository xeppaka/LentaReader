package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class MainNewsSectionNotFoundException extends PageParseException {
	private static final long serialVersionUID = -4707484947243495085L;

	public MainNewsSectionNotFoundException(URL url, String regex,
			String message) {
		super(url, regex, message);
	}

	public MainNewsSectionNotFoundException(URL url, String regex) {
		this(url, regex, "Error occured during parsing page. Regex is unable to find main news.");
	}
}
