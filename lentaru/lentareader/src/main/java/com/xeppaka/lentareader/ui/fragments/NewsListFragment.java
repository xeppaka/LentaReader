package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.ui.adapters.NewsAdapter;
import com.xeppaka.lentareader.ui.adapters.NewsObjectAdapter;

/**
 * Created by nnm on 12/27/13.
 */
public class NewsListFragment extends ListFragmentBase<News> {
    public NewsListFragment() {
        setActive(true);
    }

    @Override
    protected AsyncNODao<News> createDao(Context context) {
        return NewsDao.getInstance(context.getContentResolver());
    }

    @Override
    protected NewsObjectAdapter<News> createAdapter(Context context) {
        return new NewsAdapter(context);
    }

    @Override
    protected String createAutoRefreshToast(Context context) {
        return context.getResources().getString(R.string.news_new_items_auto_refresh);
    }
}
