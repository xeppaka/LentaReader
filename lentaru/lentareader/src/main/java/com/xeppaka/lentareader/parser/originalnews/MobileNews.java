package com.xeppaka.lentareader.parser.originalnews;

import com.xeppaka.lentareader.data.body.Body;

public class MobileNews {
	private String imageCaption;
	private String imageCredits;
	private String text;
	
	public MobileNews(String imageCaption, String imageCredits, String text) {
		this.imageCaption = imageCaption;
		this.imageCredits = imageCredits;
		this.text = text;
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
	
	public Body getText() {
		return null;
	}
	
	public void setText(String text) {
		this.text = text;
	}
}
