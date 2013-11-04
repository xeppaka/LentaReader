package com.xeppaka.lentareader.data;

public enum NewsType {
	NEWS("/news", "/NEWS"),
	ARTICLE("/articles", "/ARTICLE"),
	COLUMN("/columns", "/COLUMN"),
	PHOTO("/photo", "/PHOTO"),
	VIDEO("/video", "/VIDEO");
	
	private String rssSubpath;
    private String xmlSubpath;
	
	private NewsType(String rssSubpath, String xmlSubpath) {
		this.rssSubpath = rssSubpath;
        this.xmlSubpath = xmlSubpath;
	}
	
	public String getRssSubpath() {
		return rssSubpath;
	}
    public String getXmlSubpath() { return xmlSubpath; }
}
