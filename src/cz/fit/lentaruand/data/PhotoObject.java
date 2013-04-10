package cz.fit.lentaruand.data;

public class PhotoObject {
	private int index;
	private String url;
	private String caption;
	private String credits;
	
	public PhotoObject(int index, String url, String caption, String credits) {
		this.index = index;
		this.url = url;
		this.caption = caption;
		this.credits = credits;
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
		this.url = url;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public String getCredits() {
		return credits;
	}
	
	public void setCredits(String credits) {
		this.credits = credits;
	}
}
