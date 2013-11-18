package com.xeppaka.lentareader.data;

import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.dao.objects.BitmapReference;
import com.xeppaka.lentareader.data.dao.objects.ImageDao;
import com.xeppaka.lentareader.parser.rss.LentaRssItem;

import java.util.Date;

public class News extends NewsObject implements NewsObjectWithImage {
	private static final long serialVersionUID = 1L;
	
	private String imageLink;
	private String imageCaption;
	private String imageCredits;

	private BitmapReference imageRef;
	private BitmapReference thumbnailImageRef;

	public News(long id, String guid, String title, String link, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, String description,
            Body body) {
		super(id, guid, title, link, pubDate, rubric, description, body);
		
		setImageLink(imageLink);
		setImageCaption(imageCaption);
		setImageCredits(imageCredits);

		setImage(ImageDao.getNotAvailableImage());
		setThumbnailImage(ImageDao.getNotAvailableImage());
	}
	
	public News(String guid, String title, String link, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Rubrics rubric, String description,
            Body body) {
		super(guid, title, link, pubDate, rubric, description, body);
		
		setImageLink(imageLink);
		setImageCaption(imageCaption);
		setImageCredits(imageCredits);

		setImage(ImageDao.getNotAvailableImage());
		setThumbnailImage(ImageDao.getNotAvailableImage());
	}

	public News(LentaRssItem rssItem) {
		super(rssItem);
		
		setImageLink(rssItem.getImageLink());
		setImageCaption(null);
		setImageCredits(null);

		setImage(ImageDao.getNotAvailableImage());
		setThumbnailImage(ImageDao.getNotAvailableImage());
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

	public String getImageCredits() {
		return imageCredits;
	}

	public void setImageCredits(String imageCredits) {
		this.imageCredits = imageCredits;
	}

	public BitmapReference getImage() {
		return imageRef;
	}

	public void setImage(BitmapReference imageRef) {
		this.imageRef = imageRef;
	}

	public BitmapReference getThumbnailImage() {
		return thumbnailImageRef;
	}

	public void setThumbnailImage(BitmapReference thumbnailImageRef) {
		this.thumbnailImageRef = thumbnailImageRef;
	}

	@Override
	public NewsType getType() {
		return NewsType.NEWS;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other))
			return false;
		
		if (!(other instanceof News))
			return false;
		
		News otherNews = (News)other;
		
		if (getDescription() != otherNews.getDescription() && (getDescription() != null && !getDescription().equals(otherNews.getDescription())))
			return false;

		if (getBody() != otherNews.getBody() && (getBody() != null && !getBody().equals(otherNews.getBody())))
			return false;
		
		if (getImageLink() != otherNews.getImageLink() && (getImageLink() != null && !getImageLink().equals(otherNews.getImageLink())))
			return false;
		
		if (getImageCaption() != otherNews.getImageCaption() && (getImageCaption() != null && !getImageCaption().equals(otherNews.getImageCaption())))
			return false;
		
		if (getImageCredits() != otherNews.getImageCredits() && (getImageCredits() != null && !getImageCredits().equals(otherNews.getImageCredits())))
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 37 * hash + (getDescription() == null ? 0 : getDescription().hashCode());
		hash = 37 * hash + (getBody() == null ? 0 : getBody().hashCode());
		hash = 37 * hash + (getImageLink() == null ? 0 : getImageLink().hashCode());
		hash = 37 * hash + (getImageCredits() == null ? 0 : getImageCredits().hashCode());
		hash = 37 * hash + (getImageCaption() == null ? 0 : getImageCaption().hashCode());

		return hash;
	}
}
