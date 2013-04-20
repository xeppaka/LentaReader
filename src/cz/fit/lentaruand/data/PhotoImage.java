package cz.fit.lentaruand.data;

import cz.fit.lentaruand.data.dao.DaoObject;

public class PhotoImage implements DaoObject {
	private int id;
	private int photoId;
	private int index;
	private String url;
	private String title;
	private String credits;
	
	public PhotoImage(int id, int photoId, int index, String url, String title, String credits) {
		setId(id);
		setPhotoId(photoId);
		setIndex(index);
		setUrl(url);
		setTitle(title);
		setCredits(credits);
	}
	
	@Override
	public String getKeyValue() {
		return String.valueOf(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
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
}
