package cz.fit.lentaruand.site;

import java.net.URL;

/**
 * 
 * 
 * @author nnm
 *
 */
public class Page {
	private URL url;
	private String text;
	
	public Page(URL url, String text) {
		this.url = url;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
}
