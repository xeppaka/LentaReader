package cz.fit.lentaruand.parser.exceptions;


public class ParseWithXPathException extends ParseException {
	private static final long serialVersionUID = 45892750770122306L;
	
	private String url;
	private String xpathExpression;
	
	public ParseWithXPathException(String url, String xpathExpression) {
		this(url, xpathExpression, null);
	}
	
	public ParseWithXPathException(String url, String xpathExpression, String message) {
		super(message);
		this.url = url;
		this.xpathExpression = xpathExpression;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getXPathExpression() {
		return xpathExpression;
	}
	
	public void setXPathExpression(String xpathExpression) {
		this.xpathExpression = xpathExpression;
	}

	@Override
	public String toString() {
		return "[url: " + url + ", regex: " + xpathExpression + "]";
	}
}
