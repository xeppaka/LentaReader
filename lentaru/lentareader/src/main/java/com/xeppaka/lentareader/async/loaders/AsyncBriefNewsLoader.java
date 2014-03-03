package com.xeppaka.lentareader.async.loaders;

import android.content.Context;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.LentaNewsDownloader;
import com.xeppaka.lentareader.downloader.LentaNewsObjectDownloader;

public class AsyncBriefNewsLoader extends AsyncBriefNewsObjectLoader<News> {

	public AsyncBriefNewsLoader(Context context) {
		super(context);
	}
	
	public AsyncBriefNewsLoader(Context context, Rubrics rubric) {
		super(context, rubric);
	}

	@Override
	public LentaNewsObjectDownloader<News> createDownloader() {
		return new LentaNewsDownloader();
	}
}
