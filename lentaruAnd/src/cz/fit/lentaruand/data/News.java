package cz.fit.lentaruand.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import cz.fit.lentaruand.data.dao.objects.BitmapReference;
import cz.fit.lentaruand.data.dao.objects.ImageDao;
import cz.fit.lentaruand.parser.rss.LentaRssItem;

public class News extends NewsObject implements NewsObjectWithImage {
	private static final long serialVersionUID = 1L;
	
	private String briefText;
	private String fullText;
	private String imageLink;
	private String imageCaption;
	private String imageCredits;
	private Collection<Link> links;
	
	private BitmapReference imageRef;
	private BitmapReference thumbnailImageRef;

	public News(long id, String guid, String title, String link, String briefText,
			String fullText, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Collection<Link> links, Rubrics rubric, 
			boolean rubricUpdateNeed) {
		super(id, guid, title, link, pubDate, rubric, rubricUpdateNeed);
		
		setBriefText(briefText);
		setFullText(fullText);
		setImageLink(imageLink);
		setImageCaption(imageCaption);
		setImageCredits(imageCredits);
		setLinks(links);
		
		setImage(ImageDao.getNotAvailableImage());
		setThumbnailImage(ImageDao.getNotAvailableImage());
	}
	
	public News(String guid, String title, String link, String briefText,
			String fullText, Date pubDate, String imageLink,
			String imageCaption, String imageCredits, Collection<Link> links, Rubrics rubric, 
			boolean rubricUpdateNeed) {
		super(guid, title, link, pubDate, rubric, rubricUpdateNeed);
		
		setBriefText(briefText);
		setFullText(fullText);
		setImageLink(imageLink);
		setImageCaption(imageCaption);
		setImageCredits(imageCredits);
		setLinks(links);
		
		setImage(ImageDao.getNotAvailableImage());
		setThumbnailImage(ImageDao.getNotAvailableImage());
	}

	public News(LentaRssItem rssItem) {
		super(rssItem);
		
		setBriefText(rssItem.getDescription());
		setFullText(null);
		setImageLink(rssItem.getImageLink());
		setImageCaption(null);
		setImageCredits(null);
		setLinks(null);
		
		setImage(ImageDao.getNotAvailableImage());
		setThumbnailImage(ImageDao.getNotAvailableImage());
	}
	

	public String getBriefText() {
		return briefText;
	}

	public void setBriefText(String briefText) {
		if (briefText == null || briefText.length() == 0)
			throw new IllegalArgumentException("Argument briefText must not be null or empty.");
		
		this.briefText = briefText;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
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

	public Collection<Link> getLinks() {
		return links;
	}

	public void setLinks(Collection<Link> links) {
		if (links == null)
			this.links = Collections.emptyList();
		else
			this.links = links;
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
		
		if (getBriefText() != otherNews.getBriefText() && (getBriefText() != null && !getBriefText().equals(otherNews.getBriefText())))
			return false;

		if (getFullText() != otherNews.getFullText() && (getFullText() != null && !getFullText().equals(otherNews.getFullText())))
			return false;
		
		if (getImageLink() != otherNews.getImageLink() && (getImageLink() != null && !getImageLink().equals(otherNews.getImageLink())))
			return false;
		
		if (getImageCaption() != otherNews.getImageCaption() && (getImageCaption() != null && !getImageCaption().equals(otherNews.getImageCaption())))
			return false;
		
		if (getImageCredits() != otherNews.getImageCredits() && (getImageCredits() != null && !getImageCredits().equals(otherNews.getImageCredits())))
			return false;

		if (getLinks() != otherNews.getLinks() && (getLinks() != null && !getLinks().equals(otherNews.getLinks())))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 37 * hash + (getBriefText() == null ? 0 : getBriefText().hashCode());
		hash = 37 * hash + (getFullText() == null ? 0 : getFullText().hashCode());
		hash = 37 * hash + (getImageLink() == null ? 0 : getImageLink().hashCode());
		hash = 37 * hash + (getImageCredits() == null ? 0 : getImageCredits().hashCode());
		hash = 37 * hash + (getImageCaption() == null ? 0 : getImageCaption().hashCode());
		hash = 37 * hash + (getLinks() == null ? 0 : getLinks().hashCode());
		
		return hash;
	}
}
