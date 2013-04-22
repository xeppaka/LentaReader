package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class MobileNewsParseException extends PageParseException {
	private static final long serialVersionUID = -4707484947243495085L;

	public MobileNewsParseException(URL url, String regex,
			String message) {
		super(url, regex, message);
	}

	public MobileNewsParseException(URL url, String regex) {
		this(url, regex, "Error occured during parsing mobile news page.");
	}
}
