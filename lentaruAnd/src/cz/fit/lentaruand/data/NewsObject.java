package cz.fit.lentaruand.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import cz.fit.lentaruand.data.dao.DaoObject;
import cz.fit.lentaruand.parser.rss.LentaRssItem;

public abstract class NewsObject implements Comparable<NewsObject>, DaoObject, Serializable {
	private long id;
	private String guid;
	private String title;
	private String link;
	private Date pubDate;
	private Rubrics rubric;
	private boolean rubricUpdateNeed;

	public NewsObject(long id, String guid, String title, String link, Date pubDate,
			Rubrics rubric, boolean rubricUpdateNeed) {
		setId(id);
		setGuid(guid);
		setTitle(title);
		setLink(link);
		setPubDate(pubDate);
		setRubric(rubric);
		setRubricUpdateNeed(rubricUpdateNeed);
	}
	
	public NewsObject(String guid, String title, String link, Date pubDate,
			Rubrics rubric, boolean rubricUpdateNeed) {
		this(ID_NONE, guid, title, link, pubDate, rubric, rubricUpdateNeed);
	}
	
	public NewsObject(LentaRssItem rssItem) {
		setGuid(rssItem.getGuid());
		setTitle(rssItem.getTitle());
		setLink(rssItem.getLink());
		setPubDate(rssItem.getPubDate());
		setRubric(rssItem.getRubric());
		setRubricUpdateNeed(rssItem.isRubricUpdateNeed());
	}
	
	public abstract NewsType getType();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		if (guid == null || (guid.length()==0))
			throw new IllegalArgumentException("Argument guid must not be null or empty.");
		
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null || (title.length()==0))
			throw new IllegalArgumentException("Argument title must not be null or empty.");
		
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		if (link == null || (link.length()==0))
			throw new IllegalArgumentException("Argument link must not be null or empty.");
		
		this.link = link;
	}
	
	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		if (pubDate == null)
			throw new IllegalArgumentException("Argument pubDate must not be null.");
		
		this.pubDate = pubDate;
	}
	
	public Rubrics getRubric() {
		return rubric;
	}

	public void setRubric(Rubrics rubric) {
		if (rubric == null)
			throw new IllegalArgumentException("Argument rubric must not be null.");
		
		this.rubric = rubric;
	}
	
	public boolean isRubricUpdateNeed() {
		return rubricUpdateNeed;
	}

	public void setRubricUpdateNeed(boolean rubricUpdateNeed) {
		this.rubricUpdateNeed = rubricUpdateNeed;
	}

	/**
	 * Standard comparator compares dates ==> we will have all news 
	 * sorter by date.
	 */
	@Override
	public int compareTo(NewsObject another) {
		return another.getPubDate().compareTo(getPubDate());
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		
		if (!(other instanceof NewsObject))
			return false;
		
		return getGuid().equals(((NewsObject)other).getGuid());
	}

	@Override
	public int hashCode() {
		return getGuid().hashCode();
	}

	@Override
	public String getKeyValue() {
		return getGuid();
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeLong(id);
		out.writeUTF(guid);
		out.writeUTF(title);
		out.writeUTF(link);
		out.writeLong(pubDate.getTime());
		out.writeUTF(rubric.name());
		out.writeBoolean(rubricUpdateNeed);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		id = in.readLong();
		guid = in.readUTF();
		title = in.readUTF();
		link = in.readUTF();
		pubDate = new Date(in.readLong());
		rubric = Rubrics.valueOf(in.readUTF());
		rubricUpdateNeed = in.readBoolean();
	}
		 
}
