package com.xeppaka.lentareader.asyncloaders;

import android.content.Context;
import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.downloader.LentaArticleDownloader;
import com.xeppaka.lentareader.downloader.LentaNewsObjectDownloader;

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
