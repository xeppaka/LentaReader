package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class MobilePhotoParseException extends PageParseException {
	private static final long serialVersionUID = -6668060536413970434L;

	public MobilePhotoParseException(URL url, String regex,
			String message) {
		super(url, regex, message);
	}

	public MobilePhotoParseException(URL url, String regex) {
		this(url, regex, "Error occured during parsing mobile photo page.");
	}
}
