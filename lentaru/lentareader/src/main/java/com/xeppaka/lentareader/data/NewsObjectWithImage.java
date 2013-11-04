package com.xeppaka.lentareader.data;

import com.xeppaka.lentareader.data.dao.objects.BitmapReference;

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
