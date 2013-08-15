package cz.fit.lentaruand.downloader.exceptions;

public class HttpStatusCodeException extends Exception {
	private static final long serialVersionUID = -8950093393867368042L;
	
	private int httpStatusCode;
	private String url;

	public HttpStatusCodeException(String url, int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
		this.url = url;
	}

	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
