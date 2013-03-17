package cz.fit.lentaruand.data;

public enum NewsType {
	NEWS("/news"),
	ARTICLE("/articles"),
	COLUMN("/columns"),
	PHOTO("/photo"),
	VIDEO("/video");
	
	private String rssPath;
	
	private NewsType(String rssPath) {
		this.rssPath = rssPath;
	}
	
	public String getRssPath() {
		return rssPath;
	}
}
