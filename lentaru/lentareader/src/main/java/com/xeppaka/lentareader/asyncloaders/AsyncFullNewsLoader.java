package com.xeppaka.lentareader.asyncloaders;

import android.content.Context;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.downloader.LentaNewsDownloader;
import com.xeppaka.lentareader.downloader.LentaNewsObjectDownloader;

public class AsyncFullNewsLoader extends AsyncFullNewsObjectLoader<News> {

	public AsyncFullNewsLoader(Context context, News news) {
		super(context, news);
	}
	
	@Override
	public LentaNewsObjectDownloader<News> createDownloader() {
		return new LentaNewsDownloader();
	}
}
