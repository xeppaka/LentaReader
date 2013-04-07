package cz.fit.lentaruand.data;

import java.util.Date;

import cz.fit.lentaruand.rss.LentaRssItem;

public class Column extends NewsObject<Column> {
	
	public Column(String guid, String title, String link, Date pubDate,
			Rubrics rubric, boolean rubricUpdateNeed) {
		super(guid, title, link, pubDate, rubric, rubricUpdateNeed);
	}
	
	public Column(LentaRssItem rssItem) {
		super(rssItem);
	}

	@Override
	public NewsType getType() {
		return NewsType.COLUMN;
	}
}
