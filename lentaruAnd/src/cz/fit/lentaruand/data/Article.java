package cz.fit.lentaruand.data;

import java.util.Collection;
import java.util.Date;

import cz.fit.lentaruand.parser.rss.LentaRssItem;

public class Article extends News {
	private static final long serialVersionUID = 1L;
	
	private String secondTitle;
	private String author;

	public Article(long id, String guid, String title, String secondTitle,
			String author, String link, String briefText, String fullText,
			Date pubDate, String imageLink, String imageCaption,
			String imageCredits, Collection<Link> links, Rubrics rubric, boolean rubricUpdateNeed) {
		super(id, guid, title, link, briefText, fullText, pubDate, imageLink, imageCaption,
				imageCredits, links, rubric, rubricUpdateNeed);
		
		setSecondTitle(secondTitle);
		setAuthor(author);
	}

	public Article(String guid, String title, String secondTitle,
			String author, String link, String briefText, String fullText,
			Date pubDate, String imageLink, String imageCaption,
			String imageCredits, Collection<Link> links, Rubrics rubric, boolean rubricUpdateNeed) {
		super(guid, title, link, briefText, fullText, pubDate, imageLink, imageCaption,
				imageCredits, links, rubric, rubricUpdateNeed);
		
		setSecondTitle(secondTitle);
		setAuthor(author);
	}
	
	public Article(LentaRssItem rssItem) {
		super(rssItem.getGuid(), rssItem.getTitle(), rssItem.getLink(), rssItem
				.getDescription(), null, rssItem.getPubDate(), rssItem
				.getImageLink(), null, null, null, rssItem.getRubric(), rssItem
				.isRubricUpdateNeed());
		
		setSecondTitle(rssItem.getAuthor());
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
}
