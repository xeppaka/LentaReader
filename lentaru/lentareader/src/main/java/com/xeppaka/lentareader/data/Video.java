package com.xeppaka.lentareader.data;

import java.util.Date;

import com.xeppaka.lentareader.parser.rss.LentaRssItem;

public class Video extends NewsObject {
	private static final long serialVersionUID = 1L;
	
	public Video(String guid, String title, String link, Date pubDate,
			Rubrics rubric, boolean rubricUpdateNeed) {
		super(guid, title, link, pubDate, rubric, rubricUpdateNeed);
	}

	public Video(LentaRssItem rssItem) {
		super(rssItem);
	}

	@Override
	public NewsType getType() {
		return NewsType.VIDEO;
	}
}
