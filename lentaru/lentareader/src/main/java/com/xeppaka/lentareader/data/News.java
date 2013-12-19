package com.xeppaka.lentareader.data;

import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;

import java.util.Date;

public class News extends NewsObject {
	private static final long serialVersionUID = 1L;
	

	public News(long id, String guid, String title, String link, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, String description,
            Body body) {
		super(id, guid, title, link, imageLink, imageCaption, imageCredits, pubDate, rubric, description, body);
	}
	
	public News(String guid, String title, String link, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, String description,
            Body body) {
		super(guid, title, link, imageLink, imageCaption, imageCredits, pubDate, rubric, description, body);
	}

	public News(LentaRssItem rssItem) {
		super(rssItem);
		
		setImageLink(rssItem.getImageLink());
		setImageCaption(null);
		setImageCredits(null);
	}

	@Override
	public NewsType getType() {
		return NewsType.NEWS;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		
		if (!(other instanceof News))
			return false;
		
		News otherNews = (News)other;
		
		if (getDescription() != otherNews.getDescription() && (getDescription() != null && !getDescription().equals(otherNews.getDescription())))
			return false;

		if (getBody() != otherNews.getBody() && (getBody() != null && !getBody().equals(otherNews.getBody())))
			return false;
		
		if (getImageLink() != otherNews.getImageLink() && (getImageLink() != null && !getImageLink().equals(otherNews.getImageLink())))
			return false;
		
		if (getImageCaption() != otherNews.getImageCaption() && (getImageCaption() != null && !getImageCaption().equals(otherNews.getImageCaption())))
			return false;
		
		if (getImageCredits() != otherNews.getImageCredits() && (getImageCredits() != null && !getImageCredits().equals(otherNews.getImageCredits())))
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 37 * hash + (getDescription() == null ? 0 : getDescription().hashCode());
		hash = 37 * hash + (getBody() == null ? 0 : getBody().hashCode());
		hash = 37 * hash + (getImageLink() == null ? 0 : getImageLink().hashCode());
		hash = 37 * hash + (getImageCredits() == null ? 0 : getImageCredits().hashCode());
		hash = 37 * hash + (getImageCaption() == null ? 0 : getImageCaption().hashCode());

		return hash;
	}
}
