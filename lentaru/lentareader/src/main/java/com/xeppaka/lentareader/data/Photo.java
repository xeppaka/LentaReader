package com.xeppaka.lentareader.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.xeppaka.lentareader.parser.rss.LentaRssItem;

public class Photo extends NewsObject {
	private static final long serialVersionUID = 1L;
	
	private String secondTitle;
	private String description;
	private Collection<PhotoImage> photos;
	
	public Photo(long id, String guid, String title, String secondTitle,
			String description, Collection<PhotoImage> photos, String link,
			Date pubDate, Rubrics rubric, boolean rubricUpdateNeed) {
		super(id, guid, title, link, pubDate, rubric, rubricUpdateNeed);
		setSecondTitle(secondTitle);
		setDescription(description);
		setPhotos(photos);
	}
	
	public Photo(String guid, String title, String secondTitle,
			String description, Collection<PhotoImage> photos, String link,
			Date pubDate, Rubrics rubric, boolean rubricUpdateNeed) {
		super(guid, title, link, pubDate, rubric, rubricUpdateNeed);
		setSecondTitle(secondTitle);
		setDescription(description);
		setPhotos(photos);
	}
	
	public Photo(LentaRssItem rssItem) {
		super(rssItem);
		setDescription(rssItem.getDescription());
		photos = Collections.emptyList();
	}

	public String getSecondTitle() {
		return secondTitle;
	}

	public void setSecondTitle(String secondTitle) {
		this.secondTitle = secondTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<PhotoImage> getPhotos() {
		return photos;
	}

	public void setPhotos(Collection<PhotoImage> photos) {
		if (photos == null)
			this.photos = Collections.emptyList();
		else
			this.photos = photos;
	}

	@Override
	public NewsType getType() {
		return NewsType.PHOTO;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		
		if (!(other instanceof Photo))
			return false;
		
		Photo otherPhoto = (Photo)other;
		
		if (getSecondTitle() != otherPhoto.getSecondTitle() && (getSecondTitle() != null && !getSecondTitle().equals(otherPhoto.getSecondTitle())))
			return false;

		if (getDescription() != otherPhoto.getDescription() && (getDescription() != null && !getDescription().equals(otherPhoto.getDescription())))
			return false;

		if (getPhotos() != otherPhoto.getPhotos() && (getPhotos() != null && !getPhotos().equals(otherPhoto.getPhotos())))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 37 * hash + (getSecondTitle() == null ? 0 : getSecondTitle().hashCode());
		hash = 37 * hash + (getDescription() == null ? 0 : getDescription().hashCode());
		hash = 37 * hash + (getPhotos() == null ? 0 : getPhotos().hashCode());
		
		return hash;
	}
}