package cz.fit.lentaruand.data;

import java.util.Date;

import cz.fit.lentaruand.parser.rss.LentaRssItem;

public class Photo extends NewsObject {

	public Photo(String guid, String title, String link, Date pubDate,
			Rubrics rubric, boolean rubricUpdateNeed) {
		super(guid, title, link, pubDate, rubric, rubricUpdateNeed);
	}
	
	public Photo(LentaRssItem rssItem) {
		super(rssItem);
	}

	@Override
	public NewsType getType() {
		return NewsType.PHOTO;
	}
}