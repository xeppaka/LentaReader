package cz.fit.lentaruand.parser;


public class MobilePhotoImage {
	private int index;
	private String url;
	private String title;
	private String credits;
	private String description;
	
	public MobilePhotoImage(int index, String url, String title, String credits, String description) {
		this.index = index;
		this.url = url;
		this.title = title;
		this.credits = credits;
		this.description = description;
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
}
