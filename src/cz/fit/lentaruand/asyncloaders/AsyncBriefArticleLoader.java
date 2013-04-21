package cz.fit.lentaruand.asyncloaders;

import android.content.Context;
import cz.fit.lentaruand.data.Article;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaArticleDownloader;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;

public class AsyncBriefArticleLoader extends AsyncBriefNewsObjectLoader<Article> {

	public AsyncBriefArticleLoader(Context context) {
		super(context);
	}
	
	public AsyncBriefArticleLoader(Context context, Rubrics rubric) {
		super(context, rubric);
	}

	@Override
	public LentaNewsObjectDownloader<Article> createDownloader() {
		return new LentaArticleDownloader();
	}
}
