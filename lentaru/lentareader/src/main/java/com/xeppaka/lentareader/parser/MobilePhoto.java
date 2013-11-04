package com.xeppaka.lentareader.parser;

import java.util.Collection;

public class MobilePhoto {
	private String secondTitle;
	private Collection<MobilePhotoImage> photos;
	
	public MobilePhoto(String secondTitle, Collection<MobilePhotoImage> photos) {
		this.secondTitle = secondTitle;
		this.photos = photos;
	}

	public String getSecondTitle() {
		return secondTitle;
	}

	public void setSecondTitle(String secondTitle) {
		this.secondTitle = secondTitle;
	}

	public Collection<MobilePhotoImage> getPhotos() {
		return photos;
	}

	public void setPhotos(Collection<MobilePhotoImage> photos) {
		this.photos = photos;
	}
}
