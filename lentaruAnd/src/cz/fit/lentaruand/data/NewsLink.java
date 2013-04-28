package cz.fit.lentaruand.data;

import java.util.Date;

import cz.fit.lentaruand.data.dao.DaoObject;

public class NewsLink implements DaoObject, Comparable<NewsLink> {
	private long id;
	private String idStr;
	private long newsId;
	private String url;
	private String title;
	private Date date;
	
	public NewsLink(long id, long newsId, String url, String title, Date date) {
		super();
		setId(id);
		setNewsId(newsId);
		setUrl(url);
		setTitle(title);
		setDate(date);
	}

	public NewsLink(long newsId, String url, String title, Date date) {
		super();
		setId(ID_NONE);
		setNewsId(newsId);
		setUrl(url);
		setTitle(title);
		setDate(date);
	}
	
	public NewsLink(String url, String title, Date date) {
		super();
		setId(ID_NONE);
		setNewsId(ID_NONE);
		setUrl(url);
		setTitle(title);
		setDate(date);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
		this.idStr = String.valueOf(id);
	}
	
	public long getNewsId() {
		return newsId;
	}
	
	public void setNewsId(long newsId) {
		this.newsId = newsId;
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
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int compareTo(NewsLink other) {
		return getDate().compareTo(other.getDate());
	}

	@Override
	public String getKeyValue() {
		return idStr;
	}
}
