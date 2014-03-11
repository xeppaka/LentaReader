package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.DaoObserver;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.ui.adapters.NewsAdapter;
import com.xeppaka.lentareader.ui.adapters.NewsObjectAdapter;
import com.xeppaka.lentareader.utils.PreferencesConstants;

import java.util.Iterator;
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
    private String autoRefreshToast;

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

        final Context context = getActivity();

        autoRefreshToast = context.getResources().getString(R.string.news_new_items_auto_refresh);

        dao = NewsDao.getInstance(context.getContentResolver());
		setListAdapter(newsAdapter = new NewsAdapter(context));
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnScrollListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        dao.unregisterContentObserver(newsDaoObserver);
        saveScrollPosition();

        setUpdateByUser(false);
    }

    @Override
	public void onResume() {
		super.onResume();

        dao.registerContentObserver(newsDaoObserver);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        newsAdapter.setDownloadImages(preferences.getBoolean(PreferencesConstants.PREF_KEY_DOWNLOAD_IMAGE_THUMBNAILS, PreferencesConstants.DOWNLOAD_IMAGE_THUMBNAILS_DEFAULT));
        newsAdapter.setTextSize(preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_LIST_TEXT_SIZE, PreferencesConstants.NEWS_LIST_TEXT_SIZE_DEFAULT));

        scrolled = false;

        setUpdateByUser(true);
        refresh();
	}

    @Override
    public void refresh() {
        scrolled = false;
        final boolean updateByUser = isUpdateByUser();
        setUpdateByUser(false);

        dao.readAllIdsAsync(getCurrentRubric(), new AsyncListener<List<Long>>() {
            @Override
            public void onSuccess(List<Long> result) {
                if (!isResumed()) {
                    return;
                }

                final List<News> news = newsAdapter.getNewsObjects();

                for (int i = 0; i < news.size(); ++i) {
                    final News n = news.get(i);

                    final int resultIndex = result.indexOf(n.getId());

                    if (resultIndex < 0) {
                        news.set(i, null);
                    } else {
                        result.remove(resultIndex);
                    }
                }

                Iterator<News> iter = news.iterator();
                while (iter.hasNext()) {
                    News next = iter.next();

                    if (next == null) {
                        iter.remove();
                    }
                }

                if (result.isEmpty()) {
                    // result is empty means we don't have new news
                    if (news.isEmpty()) {
                        setLatestPubDate(0);
                    } else {
                        setLatestPubDate(news.get(0).getPubDate().getTime());
                    }

                    showData(news, 0, updateByUser);
                } else {
                    final int newItemsCount = result.size();
                    dao.readBriefAsync(getCurrentRubric(), new AsyncListener<List<News>>() {
                        @Override
                        public void onSuccess(List<News> result) {
                            if (isResumed()) {
                                showData(result, result.size(), updateByUser);
                            }

                            if (result.isEmpty()) {
                                setLatestPubDate(0);
                            } else {
                                setLatestPubDate(result.get(0).getPubDate().getTime());
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {}
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {}
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final News news = newsAdapter.getItem(position);
        if (!news.isRead()) {
            news.setRead(true);
        }
    }

    private void showData(List<News> data, int newItemsCount, boolean updateByUser) {
        if (updateByUser && !data.isEmpty() && getLatestPubDate() != data.get(0).getPubDate().getTime()) {
            clearScrollPosition();
        }

        newsAdapter.setNewsObjects(data, getExpandedItemIds());
        newsAdapter.notifyDataSetChanged();

        if (!scrolled) {
            restoreScrollPosition();
        }

        if (!updateByUser && newItemsCount > 0) {
            Toast.makeText(getActivity(), String.format(autoRefreshToast, newItemsCount), Toast.LENGTH_SHORT).show();
        }
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
