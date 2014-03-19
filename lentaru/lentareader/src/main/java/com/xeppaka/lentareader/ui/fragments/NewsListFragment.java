package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.ArticleDao;
import com.xeppaka.lentareader.data.dao.daoobjects.DaoObserver;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.ui.adapters.NewsAdapter;
import com.xeppaka.lentareader.ui.adapters.NewsObjectAdapter;
import com.xeppaka.lentareader.utils.PreferencesConstants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by nnm on 12/27/13.
 */
public class NewsListFragment extends NewsListFragmentBase<News> {
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
