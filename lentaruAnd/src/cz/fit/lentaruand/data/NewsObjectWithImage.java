package cz.fit.lentaruand.data;

import cz.fit.lentaruand.data.dao.BitmapReference;

/**
 * This interface represents contract for a news object (news, article, column,
 * etc.) that contain one main image inside.
 * 
 * @author kacpa01
 * 
 */
public interface NewsObjectWithImage {
	public BitmapReference getImage();
	public void setImage(BitmapReference imageRef);
	public BitmapReference getThumbnailImage();
	public void setThumbnailImage(BitmapReference thumbnailImageRef);
}
