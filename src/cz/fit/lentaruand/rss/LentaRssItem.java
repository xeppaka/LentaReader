package cz.fit.lentaruand.rss;

import java.util.Date;

import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;

public class LentaRssItem {
	private String guid;
	private NewsType type;
	private String title;
	private String link;
	private String description;
	private Date pubDate;
	private String imageLink;
	private Rubrics rubric;

	public LentaRssItem(String guid, String title, String link,
			String description, Date pubDate, String imageLink) {
		this(guid, null, title, link, description, pubDate, imageLink, null);
	}
	
	public LentaRssItem(String guid, NewsType type, String title, String link,
			String description, Date pubDate, String imageLink, Rubrics rubric) {
		if (guid == null || guid.isEmpty())
			throw new IllegalArgumentException("Argument guid must not be null or empty");
		
		if (title == null || title.isEmpty())
			throw new IllegalArgumentException("Argument title must not be null or empty");
		
		if (link == null || link.isEmpty())
			throw new IllegalArgumentException("Argument link must not be null or empty");
		
		if (description == null || description.isEmpty())
			throw new IllegalArgumentException("Argument description must not be null or empty");
		
		if (pubDate == null)
			throw new IllegalArgumentException("Argument pubDate must not be null");
		
		this.guid = guid;
		this.type = type;
		this.title = title;
		this.link = link;
		this.description = description;
		this.pubDate = pubDate;
		this.imageLink = imageLink;
		this.rubric = rubric;
	}

	public String getGuid() {
		return guid;
	}
	
	public NewsType getType() {
		return type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getLink() {
		return link;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Date getPubDate() {
		return pubDate;
	}
	
	public String getImageLink() {
		return imageLink;
	}
	
	public Rubrics getRubric() {
		return rubric;
	}
}
