package cz.fit.lentaruand.data;

import java.util.Date;

import cz.fit.lentaruand.parser.rss.LentaRssItem;

public class News extends NewsObject {
	private String briefText;
	private String fullText;
	private String imageLink;
	private String imageCaption;
	private String imageCredits;

	public News(long id, String guid, String title, String link, String briefText,
			String fullText, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, 
			boolean rubricUpdateNeed) {
		super(id, guid, title, link, pubDate, rubric, rubricUpdateNeed);
		
		setBriefText(briefText);
		setFullText(fullText);
		setImageLink(imageLink);
		setImageCaption(imageCaption);
		setImageCredits(imageCredits);
	}
	
	public News(String guid, String title, String link, String briefText,
			String fullText, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, 
			boolean rubricUpdateNeed) {
		super(guid, title, link, pubDate, rubric, rubricUpdateNeed);
		
		setBriefText(briefText);
		setFullText(fullText);
		setImageLink(imageLink);
		setImageCaption(imageCaption);
		setImageCredits(imageCredits);
	}

	public News(LentaRssItem rssItem) {
		super(rssItem);
		
		setBriefText(rssItem.getDescription());
		setFullText(null);
		setImageLink(rssItem.getImageLink());
		setImageCaption(null);
		setImageCredits(null);
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

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	public String getImageCaption() {
		return imageCaption;
	}

	public void setImageCaption(String imageCaption) {
		this.imageCaption = imageCaption;
	}

	public String getImageCredits() {
		return imageCredits;
	}

	public void setImageCredits(String imageCredits) {
		this.imageCredits = imageCredits;
	}

	public boolean isContentFull() {
		return fullText != null && !fullText.isEmpty();
	}

	@Override
	public NewsType getType() {
		return NewsType.NEWS;
	}
}
