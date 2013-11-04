package com.xeppaka.lentareader.downloader;

import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;

/**
 * 
 * 
 * @author nnm
 *
 */
public class Page {
	private String url;
	private Rubrics rubric;
	private NewsType type;
	private String text;
	
	public Page(String url, Rubrics rubric, NewsType type, String text) {
		this.url = url;
		this.rubric = rubric;
		this.type = type;
		this.text = text;
	}

	public Page(String url, String text) {
		this(url, null, null, text);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
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
