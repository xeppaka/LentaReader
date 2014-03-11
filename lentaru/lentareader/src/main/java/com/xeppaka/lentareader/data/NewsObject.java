package com.xeppaka.lentareader.data;

import android.text.TextUtils;

import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;
import com.xeppaka.lentareader.utils.LentaConstants;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class NewsObject implements Comparable<NewsObject>, DatabaseObject {
	private static final long serialVersionUID = 1L;

    private static final SimpleDateFormat dateFormat;

	private long id;
	private String guid;
	private String title;
	private String link;
    private String imageLink;
    private String imageCaption;
    private String imageCredits;
	private Date pubDate;
    private String formattedPubDate;
	private Rubrics rubric;
    private String description;
    private boolean read;
    private boolean updatedFromLatest;
    private boolean updatedInBackground;
    private boolean recent;
    private Body body;

    static {
        Locale rusLocale = new Locale("ru-ru");
        DateFormatSymbols dfs = new DateFormatSymbols(rusLocale);
        dfs.setMonths(LentaConstants.MONTHS_RUS);
        dfs.setShortMonths(LentaConstants.MONTHS_SHORT_RUS);
        dfs.setWeekdays(LentaConstants.DAYS_RUS);
        dfs.setShortWeekdays(LentaConstants.DAYS_SHORT_RUS);

        dateFormat = new SimpleDateFormat("EE, d MMMM yyyy HH:mm:ss", rusLocale);
        dateFormat.setDateFormatSymbols(dfs);
        dateFormat.setTimeZone(TimeZone.getDefault());
    }


    public NewsObject(long id, String guid, String title, String link, String imageLink,
                      String imageCaption, String imageCredits, Date pubDate, Rubrics rubric, String description,
                      boolean read, boolean updatedFromLatest, boolean updatedInBackground, boolean recent, Body body) {
		setId(id);
		setGuid(guid);
		setTitle(title);
		setLink(link);
        setImageLink(imageLink);
        setImageCaption(imageCaption);
        setImageCredits(imageCredits);
		setPubDate(pubDate);
		setRubric(rubric);
        setDescription(description);
        setBody(body);
        setUpdatedFromLatest(updatedFromLatest);
        setUpdatedInBackground(updatedInBackground);
        setRecent(recent);
        setRead(read);

        setFormattedPubDate(dateFormat.format(getPubDate()));
	}
	
	public NewsObject(String guid, String title, String link, String imageLink, String imageCaption,
                      String imageCredits, Date pubDate, Rubrics rubric, String description,
                      boolean read, boolean updatedFromLatest, boolean updatedInBackground, boolean recent, Body body) {
		this(ID_NONE, guid, title, link, imageLink, imageCaption, imageCredits, pubDate, rubric, description,
                read, updatedFromLatest, updatedInBackground, recent, body);
	}
	
	public NewsObject(LentaRssItem rssItem) {
		setGuid(rssItem.getGuid());
		setTitle(rssItem.getTitle());
		setLink(rssItem.getLink());
		setPubDate(rssItem.getPubDate());
		setRubric(rssItem.getRubric());
        setDescription(rssItem.getDescription());

        setFormattedPubDate(dateFormat.format(getPubDate()));
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
		if (guid == null || (guid.length() == 0))
			throw new IllegalArgumentException("Argument guid must not be null or empty.");
		
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title == null || (title.length() == 0))
			throw new IllegalArgumentException("Argument title must not be null or empty.");
		
		this.title = title;
	}

	public String getLink() {
		return link;
	}

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public boolean hasImageCaption() {
        return getImageCaption() != null && !TextUtils.isEmpty(getImageCaption());
    }

    public String getImageCredits() {
        return imageCredits;
    }

    public void setImageCredits(String imageCredits) {
        this.imageCredits = imageCredits;
    }

    public boolean hasImageCredits() {
        return getImageCredits() != null && !TextUtils.isEmpty(getImageCredits());
    }

    public void setLink(String link) {
		if (link == null || (link.length() == 0))
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

    public String getFormattedPubDate() {
        return formattedPubDate;
    }

    public void setFormattedPubDate(String formattedPubDate) {
        this.formattedPubDate = formattedPubDate;
    }

    public Rubrics getRubric() {
		return rubric;
	}

	public void setRubric(Rubrics rubric) {
		if (rubric == null)
			throw new IllegalArgumentException("Argument rubric must not be null.");
		
		this.rubric = rubric;
	}

    public boolean isUpdatedFromLatest() {
        return updatedFromLatest;
    }

    public void setUpdatedFromLatest(boolean updatedFromLatest) {
        this.updatedFromLatest = updatedFromLatest;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRecent() {
        return recent;
    }

    public void setRecent(boolean recent) {
        this.recent = recent;
    }

    public boolean isUpdatedInBackground() {
        return updatedInBackground;
    }

    public void setUpdatedInBackground(boolean updatedInBackground) {
        this.updatedInBackground = updatedInBackground;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewsObject that = (NewsObject) o;

        if (id != that.id) return false;
        if (updatedFromLatest != that.updatedFromLatest) return false;
        if (!body.equals(that.body)) return false;
        if (!description.equals(that.description)) return false;
        if (!guid.equals(that.guid)) return false;
        if (imageCaption != null ? !imageCaption.equals(that.imageCaption) : that.imageCaption != null)
            return false;
        if (imageCredits != null ? !imageCredits.equals(that.imageCredits) : that.imageCredits != null)
            return false;
        if (imageLink != null ? !imageLink.equals(that.imageLink) : that.imageLink != null)
            return false;
        if (!link.equals(that.link)) return false;
        if (!pubDate.equals(that.pubDate)) return false;
        if (rubric != that.rubric) return false;
        if (!title.equals(that.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + guid.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + link.hashCode();
        result = 31 * result + (imageLink != null ? imageLink.hashCode() : 0);
        result = 31 * result + (imageCaption != null ? imageCaption.hashCode() : 0);
        result = 31 * result + (imageCredits != null ? imageCredits.hashCode() : 0);
        result = 31 * result + pubDate.hashCode();
        result = 31 * result + rubric.hashCode();
        result = 31 * result + (updatedFromLatest ? 1 : 0);
        result = 31 * result + description.hashCode();
        result = 31 * result + body.hashCode();

        return result;
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
	public String getKeyValue() {
		return getGuid();
	}

	@Override
	public long getParentId() {
		return ID_NONE;
	}

    public boolean hasImage() {
        return imageLink != null && !TextUtils.isEmpty(imageLink);
    }
}
