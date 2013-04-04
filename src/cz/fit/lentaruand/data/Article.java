package cz.fit.lentaruand.data;

import java.util.Date;

import cz.fit.lentaruand.rss.LentaRssItem;

public class Article {
	private String guid;
	private String title;
	private String link;
	private String author;
	private String briefText;
	private String fullText;
	private Date pubDate;
	private String imageLink;
	private String imageCaption;
	private String imageCredits;
	private Rubrics rubric;
	private boolean rubricUpdateNeed;
	
	public Article(String guid, String title, String link, String briefText,
			String fullText, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, 
			boolean rubricUpdateNeed) {
		setGuid(guid);
		setTitle(title);
		setLink(link);
		setBriefText(briefText);
		setFullText(fullText);
		setPubDate(pubDate);
		setImageLink(imageLink);
		setImageCaption(imageCaption);
		setImageCredits(imageCredits);
		setRubric(rubric);
		setRubricUpdateNeed(rubricUpdateNeed);
	}

	public Article(LentaRssItem rssItem) {
		setGuid(rssItem.getGuid());
		setTitle(rssItem.getTitle());
		setLink(rssItem.getLink());
		setAuthor(rssItem.getAuthor());
		setBriefText(rssItem.getDescription());
		setFullText(null);
		setPubDate(rssItem.getPubDate());
		setImageLink(rssItem.getImageLink());
		setImageCaption(null);
		setImageCredits(null);
		setRubric(rssItem.getRubric());
		setRubricUpdateNeed(rssItem.isRubricUpdateNeed());
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		if (author == null || author.isEmpty())
			throw new IllegalArgumentException("Argument author must not be null or empty.");
		
		this.author = author;
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

	public Rubrics getRubric() {
		return rubric;
	}

	public void setRubric(Rubrics rubric) {
		if (rubric == null)
			throw new IllegalArgumentException("Argument rubric must not be null.");
		
		this.rubric = rubric;
	}

	public boolean isContentFull() {
		return fullText != null && !fullText.isEmpty();
	}

	public boolean isRubricUpdateNeed() {
		return rubricUpdateNeed;
	}

	public void setRubricUpdateNeed(boolean rubricUpdateNeed) {
		this.rubricUpdateNeed = rubricUpdateNeed;
	}

}
