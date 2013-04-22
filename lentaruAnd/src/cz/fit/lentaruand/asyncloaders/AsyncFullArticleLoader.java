package cz.fit.lentaruand.asyncloaders;

import android.content.Context;
import cz.fit.lentaruand.data.Article;
import cz.fit.lentaruand.downloader.LentaArticleDownloader;
import cz.fit.lentaruand.downloader.LentaNewsObjectDownloader;

public class AsyncFullArticleLoader extends AsyncFullNewsObjectLoader<Article> {

	public AsyncFullArticleLoader(Context context, Article article) {
		super(context, article);
	}
	
	@Override
	public LentaNewsObjectDownloader<Article> createDownloader() {
		return new LentaArticleDownloader();
	}
}
