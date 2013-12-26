package com.xeppaka.lentareader.data;

import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;

import java.util.Date;

public class News extends NewsObject {
	private static final long serialVersionUID = 1L;

	public News(long id, String guid, String title, String link, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, boolean latest, String description,
            Body body) {
		super(id, guid, title, link, imageLink, imageCaption, imageCredits, pubDate, rubric, latest, description, body);
	}
	
	public News(String guid, String title, String link, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, boolean latest, String description,
            Body body) {
		super(guid, title, link, imageLink, imageCaption, imageCredits, pubDate, rubric, latest, description, body);
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
}
