package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class UnknownNewsTypeException extends PageParseException {
	private static final long serialVersionUID = 7050539628332130934L;

	public UnknownNewsTypeException(URL url) {
		this(url, "empty");
	}
	
	public UnknownNewsTypeException(URL url, String regex) {
		this(url, regex, "Error occured during parsing news item. Parsed item is not news, article, photo or column.");
	}
	
	public UnknownNewsTypeException(URL url, String regex, String message) {
		super(url, regex, message);
	}
}
