package com.xeppaka.lentareader.data;

import android.text.TextUtils;

import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;

import java.util.Date;

public abstract class NewsObject implements Comparable<NewsObject>, DatabaseObject {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String guid;
	private String title;
	private String link;
    private String imageLink;
    private String imageCaption;
    private String imageCredits;
	private Date pubDate;
	private Rubrics rubric;
    private String description;
    private Body body;

	public NewsObject(long id, String guid, String title, String link, String imageLink,
                      String imageCaption, String imageCredits, Date pubDate, Rubrics rubric, String description, Body body) {
		setId(id);
		setGuid(guid);
		setTitle(title);
		setLink(link);
        setImageLink(imageLink);
        setImageCaption(imageCaption);
        setImageCredits(imageCredits);
		setPubDate(pubDate);
		setRubric(rubric);
        setDescription(description);
        setBody(body);
	}
	
	public NewsObject(String guid, String title, String link, String imageLink, String imageCaption,
                      String imageCredits, Date pubDate, Rubrics rubric, String description, Body body) {
		this(ID_NONE, guid, title, link, imageLink, imageCaption, imageCredits, pubDate, rubric, description, body);
	}
	
	public NewsObject(LentaRssItem rssItem) {
		setGuid(rssItem.getGuid());
		setTitle(rssItem.getTitle());
		setLink(rssItem.getLink());
		setPubDate(rssItem.getPubDate());
		setRubric(rssItem.getRubric());
        setDescription(rssItem.getDescription());
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
		if (guid == null || (guid.length() == 0))
			throw new IllegalArgumentException("Argument guid must not be null or empty.");
		
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null || (title.length() == 0))
			throw new IllegalArgumentException("Argument title must not be null or empty.");
		
		this.title = title;
	}

	public String getLink() {
		return link;
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

    public void setLink(String link) {
		if (link == null || (link.length() == 0))
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

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean hasImage() {
        return imageLink != null && !TextUtils.isEmpty(imageLink);
    }
}
