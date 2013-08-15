package cz.fit.lentaruand.service;

import java.io.IOException;
import java.util.Collection;

import android.graphics.Bitmap;
import cz.fit.lentaruand.data.NewsObjectWithImage;
import cz.fit.lentaruand.data.dao.ImageDao;
import cz.fit.lentaruand.downloader.LentaHttpImageDownloader;
import cz.fit.lentaruand.downloader.exceptions.HttpStatusCodeException;
import cz.fit.lentaruand.utils.URLHelper;

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
