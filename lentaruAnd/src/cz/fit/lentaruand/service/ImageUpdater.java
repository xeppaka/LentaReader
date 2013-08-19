package cz.fit.lentaruand.service;

import java.util.Collection;

import cz.fit.lentaruand.data.NewsObjectWithImage;
import cz.fit.lentaruand.data.dao.ImageDao;

public class ImageUpdater {
	public static void updateImage(ImageDao imageDao, String imageUrl, NewsObjectWithImage newsObject) {
//		try {
//			if (imageDao.checkImageInDiskCache(imageUrl)) {
//				return;
//			}
//			
//			Bitmap newBitmap = LentaHttpImageDownloader.downloadBitmap(imageLink);
//			
//			imageDao.create(imageKey, newBitmap);
//			newsObject.setImage(imageDao.read(thumbnailImageKey));
//			newsObject.setThumbnailImageRef(imageDao.readThumbnail(imageLink));
//		} catch (HttpStatusCodeException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void updateImage(Collection<NewsObjectWithImage> newsObjects) {
		
	}
}
