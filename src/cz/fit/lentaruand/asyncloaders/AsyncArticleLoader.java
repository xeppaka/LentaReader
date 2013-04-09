package cz.fit.lentaruand.asyncloaders;

import java.util.Set;

import android.content.Context;
import cz.fit.lentaruand.data.Article;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;

public class AsyncArticleLoader extends AsyncNewsObjectLoader<Article> {

	public AsyncArticleLoader(Context context) {
		super(context);
	}
	
	public AsyncArticleLoader(Context context, Rubrics rubric, Set<String> skipGuids) {
		super(context, rubric, skipGuids);
	}

	@Override
	public LentaNewsObjectDownloader<Article> createDownloader() {
		return null;
	}
}
