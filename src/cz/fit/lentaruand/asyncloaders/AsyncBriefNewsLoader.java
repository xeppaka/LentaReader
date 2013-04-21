package cz.fit.lentaruand.asyncloaders;

import android.content.Context;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;

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
