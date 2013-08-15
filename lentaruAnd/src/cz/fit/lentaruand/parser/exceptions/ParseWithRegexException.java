package cz.fit.lentaruand.parser.exceptions;

import java.net.URL;

public class ParseWithRegexException extends ParseException {
	private static final long serialVersionUID = 45892750770122306L;
	
	private URL url;
	private String regex;
	
	public ParseWithRegexException(URL url, String regex) {
		this(url, regex, null);
	}
	
	public ParseWithRegexException(URL url, String regex, String message) {
		super(message);
		this.url = url;
		this.regex = regex;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}
	
	public String getRegex() {
		return regex;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}

	@Override
	public String toString() {
		return "[url: " + url.toExternalForm() + ", regex: " + regex + "]";
	}
}
