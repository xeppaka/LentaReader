package com.xeppaka.lentareader.parser;

public class MobileArticle extends MobileNews {
	private String secondTitle;
	
	public MobileArticle(String secondTitle, String imageCaption, String imageCredits, String text) {
		super(imageCaption, imageCredits, text);
		this.secondTitle = secondTitle;
	}
	
	public String getSecondTitle() {
		return secondTitle;
	}
}
