package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.ArticleDao;
import com.xeppaka.lentareader.ui.adapters.listnews.NewsAdapter;
import com.xeppaka.lentareader.ui.adapters.listnews.NewsObjectAdapter;

/**
 * Created by nnm on 12/27/13.
 */
public class ArticleListFragment extends ListFragmentBase<Article> {
    @Override
    protected AsyncNODao<Article> createDao(Context context) {
        return ArticleDao.getInstance(context.getContentResolver());
    }

    @Override
    protected NewsObjectAdapter createAdapter(Context context) {
        return new NewsAdapter(context);
    }

    @Override
    protected String createAutoRefreshToast(Context context) {
        return context.getResources().getString(R.string.articles_new_items_auto_refresh);
    }
}
