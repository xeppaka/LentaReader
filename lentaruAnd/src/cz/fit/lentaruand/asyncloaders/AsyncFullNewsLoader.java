package cz.fit.lentaruand.asyncloaders;

import android.content.Context;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;

public class AsyncFullNewsLoader extends AsyncFullNewsObjectLoader<News> {

	public AsyncFullNewsLoader(Context context, News news) {
		super(context, news);
	}
	
	@Override
	public LentaNewsObjectDownloader<News> createDownloader() {
		return new LentaNewsDownloader();
	}
}
