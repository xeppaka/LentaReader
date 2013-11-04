package com.xeppaka.lentareader.data;

import com.xeppaka.lentareader.parser.MobilePhotoImage;

public class PhotoImage implements DatabaseObject, Comparable<PhotoImage> {
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long photoId;
	private int index;
	private String url;
	private String title;
	private String credits;
	private String description;
	
	public PhotoImage(long id, long photoId, int index, String url, String title, String credits, String description) {
		setId(id);
		setPhotoId(photoId);
		setIndex(index);
		setUrl(url);
		setTitle(title);
		setCredits(credits);
		setDescription(description);
	}
	
	public PhotoImage(long photoId, int index, String url, String title, String credits, String description) {
		setId(ID_NONE);
		setPhotoId(photoId);
		setIndex(index);
		setUrl(url);
		setTitle(title);
		setCredits(credits);
		setDescription(description);
	}
	
	public PhotoImage(int index, String url, String title, String credits, String description) {
		setId(ID_NONE);
		setPhotoId(ID_NONE);
		setIndex(index);
		setUrl(url);
		setTitle(title);
		setCredits(credits);
		setDescription(description);
	}
	
	public PhotoImage(MobilePhotoImage mobilePhotoImage) {
		setIndex(mobilePhotoImage.getIndex());
		setUrl(mobilePhotoImage.getUrl());
		setTitle(mobilePhotoImage.getTitle());
		setCredits(mobilePhotoImage.getCredits());
		setDescription(mobilePhotoImage.getDescription());
	}
	
	@Override
	public String getKeyValue() {
		return String.valueOf(id);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(long photoId) {
		this.photoId = photoId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		if (url == null || url.length() <= 0)
			throw new IllegalArgumentException("Argument url must not be null or empty.");
		
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCredits() {
		return credits;
	}
	
	public void setCredits(String credits) {
		this.credits = credits;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(PhotoImage another) {
		if (another.getIndex() < getIndex())
			return -1;
		
		if (another.getIndex() > getIndex())
			return 1;
		
		return 0;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		
		if (!(other instanceof PhotoImage))
			return false;
		
		PhotoImage otherPhotoImage = (PhotoImage)other;
		
		if (getId() != otherPhotoImage.getId())
			return false;

		if (getPhotoId() != otherPhotoImage.getPhotoId())
			return false;
		
		if (getIndex() != otherPhotoImage.getIndex())
			return false;
		
		if (getUrl() != otherPhotoImage.getUrl() && (getUrl() != null && !getUrl().equals(otherPhotoImage.getUrl())))
			return false;
		
		if (getTitle() != otherPhotoImage.getTitle() && (getTitle() != null && !getTitle().equals(otherPhotoImage.getTitle())))
			return false;

		if (getCredits() != otherPhotoImage.getCredits() && (getCredits() != null && !getCredits().equals(otherPhotoImage.getCredits())))
			return false;
		
		if (getDescription() != otherPhotoImage.getDescription() && (getDescription() != null && !getDescription().equals(otherPhotoImage.getDescription())))
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 37 * hash + (int)(getId() ^ (getId() >> 32));
		hash = 37 * hash + (int)(getPhotoId() ^ (getPhotoId() >> 32));
		hash = 37 * hash + (int)(getIndex() ^ (getIndex() >> 32));
		hash = 37 * hash + (getUrl() == null ? 0 : getUrl().hashCode());
		hash = 37 * hash + (getTitle() == null ? 0 : getTitle().hashCode());
		hash = 37 * hash + (getCredits() == null ? 0 : getCredits().hashCode());
		hash = 37 * hash + (getDescription() == null ? 0 : getDescription().hashCode());
		
		return hash;
	}

	@Override
	public long getParentId() {
		return photoId;
	}
}
