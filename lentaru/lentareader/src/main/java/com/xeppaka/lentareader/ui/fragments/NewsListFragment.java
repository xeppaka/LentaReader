package com.xeppaka.lentareader.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao.DaoReadMultiListener;
import com.xeppaka.lentareader.data.dao.async.AsyncDao.DaoReadSingleListener;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.DaoObserver;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.service.Callback;
import com.xeppaka.lentareader.service.ServiceHelper;
import com.xeppaka.lentareader.ui.activities.NewsFullActivity;
import com.xeppaka.lentareader.utils.PreferencesConstants;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;

/**
 * This is general fragment that shows list of loaded news objects (News,
 * Articles, etc.). The way how item will be shown in the list depends on the
 * used list adapter that is passed as an argument in constructor.
 * 
 * @author nnm
 */
public class NewsListFragment extends NewsObjectListFragment implements AbsListView.OnScrollListener {
	private NewsAdapter newsAdapter;
	private AsyncNODao<News> dao;

    private boolean scrolled;
    private boolean autoRefresh;

    private Dao.Observer<News> newsDaoObserver = new DaoObserver<News>(new Handler()) {
        @Override
        public void onDataChanged(boolean selfChange, News dataObject) {
            refresh();
        }

        @Override
        public void onDataChanged(boolean selfChange) {
            refresh();
        }
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        dao = NewsDao.getInstance(getActivity().getContentResolver());
		setListAdapter(newsAdapter = new NewsAdapter(getActivity()));
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnScrollListener(this);
    }

    @Override
	public void onResume() {
		super.onResume();

        dao.registerContentObserver(newsDaoObserver);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        newsAdapter.setDownloadImages(preferences.getBoolean(PreferencesConstants.PREF_KEY_DOWNLOAD_IMAGE_THUMBNAILS, PreferencesConstants.DOWNLOAD_IMAGE_THUMBNAILS_DEFAULT));
        newsAdapter.setTextSize(preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_LIST_TEXT_SIZE, PreferencesConstants.NEWS_LIST_TEXT_SIZE_DEFAULT));
        autoRefresh = preferences.getBoolean(PreferencesConstants.PREF_KEY_NEWS_AUTO_REFRESH, PreferencesConstants.NEWS_AUTO_REFRESH_DEFAULT);

        scrolled = false;

        refresh();
	}

    @Override
    public void refresh() {
        scrolled = false;

        dao.readAsync(getCurrentRubric(), new DaoReadMultiListener<News>() {
            @Override
            public void finished(List<News> result) {
                if (isResumed()) {
                    if (!result.isEmpty() && getLatestPubDate() != result.get(0).getPubDate().getTime()) {
                        clearScrollPosition();
                    }

                    newsAdapter.setNewsObjects(result, getExpandedItemIds());
                    newsAdapter.notifyDataSetChanged();

                    if (!scrolled) {
                        restoreScrollPosition();
                    }
                }

                if (result.isEmpty()) {
                    setLatestPubDate(0);
                } else {
                    setLatestPubDate(result.get(0).getPubDate().getTime());
                }
            }
        });
    }

	@Override
	public void onPause() {
		super.onPause();

        dao.unregisterContentObserver(newsDaoObserver);
        saveScrollPosition();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(this.getActivity(), NewsFullActivity.class);
		intent.putExtra("newsId", newsAdapter.getItem(position).getId());
		
		startActivity(intent);
	}

    @Override
    public NewsType getNewsType() {
        return NewsType.NEWS;
    }

    @Override
    public NewsObjectAdapter getDataObjectsAdapter() {
        return newsAdapter;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!scrolled) {
            scrolled = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
}
