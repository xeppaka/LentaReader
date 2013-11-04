package com.xeppaka.lentareader.data;

import java.util.Date;

import com.xeppaka.lentareader.parser.rss.LentaRssItem;

public abstract class NewsObject implements Comparable<NewsObject>, DatabaseObject {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String guid;
	private String title;
	private String link;
	private Date pubDate;
	private Rubrics rubric;
	private boolean rubricUpdateNeed;

	public NewsObject(long id, String guid, String title, String link, Date pubDate,
			Rubrics rubric, boolean rubricUpdateNeed) {
		setId(id);
		setGuid(guid);
		setTitle(title);
		setLink(link);
		setPubDate(pubDate);
		setRubric(rubric);
		setRubricUpdateNeed(rubricUpdateNeed);
	}
	
	public NewsObject(String guid, String title, String link, Date pubDate,
			Rubrics rubric, boolean rubricUpdateNeed) {
		this(ID_NONE, guid, title, link, pubDate, rubric, rubricUpdateNeed);
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		if (guid == null || (guid.length()==0))
			throw new IllegalArgumentException("Argument guid must not be null or empty.");
		
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null || (title.length()==0))
			throw new IllegalArgumentException("Argument title must not be null or empty.");
		
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		if (link == null || (link.length()==0))
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
	public int compareTo(NewsObject another) {
		return another.getPubDate().compareTo(getPubDate());
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		
		if (!(other instanceof NewsObject))
			return false;
		
		NewsObject otherNewsObject = (NewsObject)other;
		
		if (getId() != otherNewsObject.getId())
			return false;
		
		if (getGuid() != otherNewsObject.getGuid() && (getGuid() != null && !getGuid().equals(otherNewsObject.getGuid())))
			return false;
		
		if (getTitle() != otherNewsObject.getTitle() && (getTitle() != null && !getTitle().equals(otherNewsObject.getTitle())))
			return false;
		
		if (getLink() != otherNewsObject.getLink() && (getLink() != null && !getLink().equals(otherNewsObject.getLink())))
			return false;
		
		if (getPubDate() != otherNewsObject.getPubDate() && (getPubDate() != null && !getPubDate().equals(otherNewsObject.getPubDate())))
			return false;
		
		if (getRubric() != otherNewsObject.getRubric() && (getRubric() != null && !getRubric().equals(otherNewsObject.getRubric())))
			return false;
		
		if (isRubricUpdateNeed() != otherNewsObject.isRubricUpdateNeed())
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = 37 * hash + (int)(getId() ^ (getId() >> 32));
		hash = 37 * hash + (getGuid() == null ? 0 : getGuid().hashCode());
		hash = 37 * hash + (getTitle() == null ? 0 : getTitle().hashCode());
		hash = 37 * hash + (getLink() == null ? 0 : getLink().hashCode());
		hash = 37 * hash + (getPubDate() == null ? 0 : getPubDate().hashCode());
		hash = 37 * hash + (getRubric() == null ? 0 : getRubric().hashCode());
		hash = 37 * hash + (isRubricUpdateNeed() ? 1 : 0);
		
		return hash;
	}

	@Override
	public String getKeyValue() {
		return getGuid();
	}

	@Override
	public long getParentId() {
		return ID_NONE;
	}
}
