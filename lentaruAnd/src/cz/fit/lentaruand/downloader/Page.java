package cz.fit.lentaruand.downloader;

import java.net.URL;

import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;

/**
 * 
 * 
 * @author nnm
 *
 */
public class Page {
	private URL url;
	private Rubrics rubric;
	private NewsType type;
	private String text;
	
	public Page(URL url, Rubrics rubric, NewsType type, String text) {
		this.url = url;
		this.rubric = rubric;
		this.type = type;
		this.text = text;
	}

	public Page(URL url, String text) {
		this(url, null, null, text);
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

	public Rubrics getRubric() {
		return rubric;
	}

	public void setRubric(Rubrics rubric) {
		this.rubric = rubric;
	}

	public NewsType getType() {
		return type;
	}

	public void setType(NewsType type) {
		this.type = type;
	}
}
