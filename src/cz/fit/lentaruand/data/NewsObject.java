package cz.fit.lentaruand.data;

import java.util.Date;

import cz.fit.lentaruand.rss.LentaRssItem;

public abstract class NewsObject<T extends NewsObject<T>> implements Comparable<T> {
	private String guid;
	private String title;
	private String link;
	private Date pubDate;
	private Rubrics rubric;
	private boolean rubricUpdateNeed;
	
	public NewsObject(String guid, String title, String link, Date pubDate,
			Rubrics rubric, boolean rubricUpdateNeed) {
		setGuid(guid);
		setTitle(title);
		setLink(link);
		setPubDate(pubDate);
		setRubric(rubric);
		setRubricUpdateNeed(rubricUpdateNeed);
	}
	
	public NewsObject(LentaRssItem rssItem) {
		setGuid(rssItem.getGuid());
		setTitle(rssItem.getTitle());
		setLink(rssItem.getLink());
		setPubDate(rssItem.getPubDate());
		setRubric(rssItem.getRubric());
		setRubricUpdateNeed(rssItem.isRubricUpdateNeed());
	}
	
	public abstract NewsType getType();
	
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
	
	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		if (pubDate == null)
			throw new IllegalArgumentException("Argument pubDate must not be null.");
		
		this.pubDate = pubDate;
	}
	
	public Rubrics getRubric() {
		return rubric;
	}

	public void setRubric(Rubrics rubric) {
		if (rubric == null)
			throw new IllegalArgumentException("Argument rubric must not be null.");
		
		this.rubric = rubric;
	}
	
	public boolean isRubricUpdateNeed() {
		return rubricUpdateNeed;
	}

	public void setRubricUpdateNeed(boolean rubricUpdateNeed) {
		this.rubricUpdateNeed = rubricUpdateNeed;
	}

	/**
	 * Standard comparator compares dates ==> we will have all news 
	 * sorter by date.
	 */
	@Override
	public int compareTo(T another) {
		return getPubDate().compareTo(another.getPubDate());
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		
		if (!(other instanceof NewsObject<?>))
			return false;
		
		return getGuid().equals(((NewsObject<?>)other).getGuid());
	}

	@Override
	public int hashCode() {
		return getGuid().hashCode();
	}
}
