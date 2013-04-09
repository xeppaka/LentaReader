package cz.fit.lentaruand.asyncloaders;

import java.util.Set;

import android.content.Context;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaNewsDownloader;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;

public class AsyncNewsLoader extends AsyncNewsObjectLoader<News> {

	public AsyncNewsLoader(Context context) {
		super(context);
	}
	
	public AsyncNewsLoader(Context context, Rubrics rubric, Set<String> skipGuids) {
		super(context, rubric, skipGuids);
	}

	@Override
	public LentaNewsObjectDownloader<News> createDownloader() {
		return new LentaNewsDownloader();
	}
}
