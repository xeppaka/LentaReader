package com.xeppaka.lentareader.asyncloaders;

import android.content.Context;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.downloader.LentaArticleDownloader;
import com.xeppaka.lentareader.downloader.LentaNewsObjectDownloader;

public class AsyncFullArticleLoader extends AsyncFullNewsObjectLoader<Article> {

	public AsyncFullArticleLoader(Context context, Article article) {
		super(context, article);
	}
	
	@Override
	public LentaNewsObjectDownloader<Article> createDownloader() {
		return new LentaArticleDownloader();
	}
}
