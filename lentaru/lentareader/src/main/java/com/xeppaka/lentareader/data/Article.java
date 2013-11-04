package com.xeppaka.lentareader.data;

import java.util.Collection;
import java.util.Date;

import com.xeppaka.lentareader.parser.rss.LentaRssItem;

public class Article extends News {
	private static final long serialVersionUID = 1L;
	
	private String secondTitle;
	private String author;

	public Article(long id, String guid, String title, String secondTitle,
			String author, String link, Date pubDate, String imageLink, String imageCaption,
			String imageCredits, Rubrics rubric, String description, String body) {
		super(id, guid, title, link, pubDate, imageLink, imageCaption, imageCredits,
                rubric, description, body);
		
		setSecondTitle(secondTitle);
		setAuthor(author);
	}

	public Article(String guid, String title, String secondTitle,
			String author, String link, Date pubDate, String imageLink, String imageCaption,
			String imageCredits, Collection<Link> links, Rubrics rubric, String description, String body) {
		super(guid, title, link, pubDate, imageLink, imageCaption, imageCredits, rubric,
                description, body);
		
		setSecondTitle(secondTitle);
		setAuthor(author);
	}
	
	public Article(LentaRssItem rssItem) {
		super(rssItem);
		
		//setSecondTitle(rssItem.getAuthor());
		setAuthor(rssItem.getAuthor());
	}
	
	public String getSecondTitle() {
		return secondTitle;
	}

	public void setSecondTitle(String secondTitle) {
		this.secondTitle = secondTitle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	@Override
	public NewsType getType() {
		return NewsType.ARTICLE;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		
		if (!(other instanceof Article))
			return false;
		
		Article otherArticle = (Article)other;
		
		if (getSecondTitle() != otherArticle.getSecondTitle() && (getSecondTitle() != null && !getSecondTitle().equals(otherArticle.getSecondTitle())))
			return false;
		
		if (getAuthor() != otherArticle.getAuthor() && (getAuthor() != null && !getAuthor().equals(otherArticle.getAuthor())))
			return false;

		return true;		
	}
}
