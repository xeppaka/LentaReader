package com.xeppaka.lentareader.async.loaders;

import android.content.Context;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.downloader.LentaArticlesDownloader;
import com.xeppaka.lentareader.downloader.LentaNewsObjectDownloader;

public class AsyncFullArticleLoader extends AsyncFullNewsObjectLoader<Article> {

	public AsyncFullArticleLoader(Context context, Article article) {
		super(context, article);
	}
	
	@Override
	public LentaNewsObjectDownloader<Article> createDownloader() {
		return new LentaArticlesDownloader();
	}
}
