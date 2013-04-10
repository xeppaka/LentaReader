package cz.fit.lentaruand.parser;

import java.util.Collection;

import cz.fit.lentaruand.data.PhotoObject;

public class MobilePhoto {
	private String secondTitle;
	private Collection<PhotoObject> photos;
	
	public MobilePhoto(String secondTitle, Collection<PhotoObject> photos) {
		this.secondTitle = secondTitle;
		this.photos = photos;
	}

	public String getSecondTitle() {
		return secondTitle;
	}

	public void setSecondTitle(String secondTitle) {
		this.secondTitle = secondTitle;
	}

	public Collection<PhotoObject> getPhotos() {
		return photos;
	}

	public void setPhotos(Collection<PhotoObject> photos) {
		this.photos = photos;
	}
}
