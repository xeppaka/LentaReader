package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class NewsDateNotFoundException extends PageParseException {
	private static final long serialVersionUID = -6825029006671102448L;

	public NewsDateNotFoundException(URL url, String regex, String message) {
		super(url, regex, message);
	}

	public NewsDateNotFoundException(URL url, String regex) {
		this(url, regex, "Error occured during parsing page. Regex is unable to find news date.");
	}
}
