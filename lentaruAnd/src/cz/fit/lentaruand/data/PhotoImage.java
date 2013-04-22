package cz.fit.lentaruand.data;

import cz.fit.lentaruand.data.dao.DaoObject;
import cz.fit.lentaruand.parser.MobilePhotoImage;

public class PhotoImage implements DaoObject, Comparable<PhotoImage> {
	private long id;
	private long photoId;
	private int index;
	private String url;
	private String title;
	private String credits;
	private String description;
	
	public PhotoImage(int id, int photoId, int index, String url, String title, String credits, String description) {
		setId(id);
		setPhotoId(photoId);
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
		if (url == null || url.isEmpty())
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
}
