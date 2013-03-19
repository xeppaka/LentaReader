package cz.fit.lentaruand.data;

import java.util.Date;

import cz.fit.lentaruand.rss.RssItem;

public class News {
	private String guid;
	private String title;
	private String link;
	private String briefText;
	private String fullText;
	private Date pubDate;
	private String imageLink;
	private Rubrics rubric;
	private Rubrics subRubric;
	
	public News(String guid, String title, String link, String briefText, String fullText,
			Date pubDate, String imageLink, Rubrics rubric, Rubrics subRubric) {
		setGuid(guid);
		setTitle(title);
		setLink(link);
		setBriefText(briefText);
		setFullText(fullText);
		setPubDate(pubDate);
		setImageLink(imageLink);
		setRubric(rubric);
		setSubRubric(subRubric);
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		if (guid == null || guid.isEmpty())
			throw new IllegalArgumentException("Argument guid must not be null or empty.");
		
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null || title.isEmpty())
			throw new IllegalArgumentException("Argument title must not be null or empty.");
		
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		if (link == null || link.isEmpty())
			throw new IllegalArgumentException("Argument link must not be null or empty.");
		
		this.link = link;
	}

	public String getBriefText() {
		return briefText;
	}

	public void setBriefText(String briefText) {
		if (briefText == null || briefText.isEmpty())
			throw new IllegalArgumentException("Argument briefText must not be null or empty.");
		
		this.briefText = briefText;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		if (pubDate == null)
			throw new IllegalArgumentException("Argument pubDate must not be null.");
		
		this.pubDate = pubDate;
	}

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	public Rubrics getRubric() {
		return rubric;
	}

	public void setRubric(Rubrics rubric) {
		if (rubric == null)
			throw new IllegalArgumentException("Argument rubric must not be null.");
		
		this.rubric = rubric;
	}

	public Rubrics getSubRubric() {
		return subRubric;
	}

	public void setSubRubric(Rubrics subRubric) {
		this.subRubric = subRubric;
	}
	
	public static News fromRssItem(RssItem item) {
		return new News(item.getGuid(),
				item.getTitle(),
				item.getLink(),
				item.getDescription(),
				null,
				item.getPubDate(),
				item.getImageLink(),
				item.getRubric(),
				null);
	}
}
