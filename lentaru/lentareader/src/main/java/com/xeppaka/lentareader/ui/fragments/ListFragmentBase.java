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
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
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
 * Created by kacpa01 on 3/19/14.
 */
public abstract class ListFragmentBase<T extends NewsObject> extends ListFragment implements AbsListView.OnScrollListener {
    private Rubrics currentRubric = Rubrics.LATEST;

    private NewsObjectAdapter<T> newsAdapter;
    private AsyncNODao<T> dao;

    private boolean scrolled;
    private String autoRefreshToast;

    // expanded items for each rubric
    protected Set<Long>[] expandedItemIds = new Set[Rubrics.values().length];
    protected ScrollerPosition[] scrollPositions = new ScrollerPosition[Rubrics.values().length];
    protected long[] latestNewsTime = new long[Rubrics.values().length];
    private ItemSelectionListener itemSelectionListener;
    private boolean active;

    public interface ItemSelectionListener {
        void onItemSelected(int position, long id);
    }

    private static class ScrollerPosition {
        private int item;
        private int top;
    }

    private Dao.Observer<T> daoObserver = new DaoObserver<T>(new Handler()) {
        @Override
        public void onDataChanged(boolean selfChange, T dataObject) {
            refresh();
        }

        @Override
        public void onDataChanged(boolean selfChange) {
            refresh();
        }
    };

    public ListFragmentBase() {
        for (Rubrics rubric : Rubrics.values()) {
            expandedItemIds[rubric.ordinal()] = new HashSet<Long>();
            scrollPositions[rubric.ordinal()] = new ScrollerPosition();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            active = savedInstanceState.getBoolean("active", false);
            currentRubric = Rubrics.valueOf(savedInstanceState.getString("rubric", Rubrics.LATEST.name()));
        }

        final Context context = getActivity();

        autoRefreshToast = createAutoRefreshToast(context);
        dao = createDao(context);
        setListAdapter(newsAdapter = createAdapter(context));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("active", isActive());
        outState.putString("rubric", getCurrentRubric().name());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnScrollListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        dao.unregisterContentObserver(daoObserver);
        saveScrollPosition();
    }

    @Override
    public void onResume() {
        super.onResume();

        dao.registerContentObserver(daoObserver);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        newsAdapter.setDownloadImages(preferences.getBoolean(PreferencesConstants.PREF_KEY_DOWNLOAD_IMAGE_THUMBNAILS, PreferencesConstants.DOWNLOAD_IMAGE_THUMBNAILS_DEFAULT));
        newsAdapter.setTextSize(preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_LIST_TEXT_SIZE, PreferencesConstants.NEWS_LIST_TEXT_SIZE_DEFAULT));

        scrolled = false;

        refresh();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        if (activity instanceof ItemSelectionListener) {
            setItemSelectionListener((ItemSelectionListener)activity);
        }
    }

    public Rubrics getCurrentRubric() {
        return currentRubric;
    }

    public void setCurrentRubric(Rubrics currentRubric) {
        saveScrollPosition();
        this.currentRubric = currentRubric;

        refresh();
    }

    public Set<Long> getExpandedItemIds() {
        return expandedItemIds[currentRubric.ordinal()];
    }

    public void saveScrollPosition() {
        final int item = getListView().getFirstVisiblePosition();
        final View childView = getListView().getChildAt(0);
        final int top = childView == null ? 0 : childView.getTop();

        scrollPositions[currentRubric.ordinal()].item = item;
        scrollPositions[currentRubric.ordinal()].top = top;
    }

    public void restoreScrollPosition() {
        if (scrollPositions[currentRubric.ordinal()].item < getListAdapter().getCount()) {
            getListView().setSelectionFromTop(scrollPositions[currentRubric.ordinal()].item, scrollPositions[currentRubric.ordinal()].top);
        } else {
            clearScrollPosition();
        }
    }

    public void clearScrollPosition() {
        scrollPositions[currentRubric.ordinal()].item = scrollPositions[currentRubric.ordinal()].top = 0;
    }

    public Long getLatestPubDate() {
        return latestNewsTime[currentRubric.ordinal()];
    }

    public void setLatestPubDate(long date) {
        latestNewsTime[currentRubric.ordinal()] = date;
    }

    public void setItemSelectionListener(ItemSelectionListener itemSelectionListener) {
        this.itemSelectionListener = itemSelectionListener;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public NewsObjectAdapter<T> getDataObjectsAdapter() {
        return newsAdapter;
    }

    public void refresh() {
        scrolled = false;

        dao.readAllIdsAsync(getCurrentRubric(), new AsyncListener<List<Long>>() {
            @Override
            public void onSuccess(List<Long> result) {
                if (!isResumed()) {
                    return;
                }

                final List<T> news = newsAdapter.getNewsObjects();
                final Iterator<T> iter = news.iterator();
                while (iter.hasNext()) {
                    final T next = iter.next();
                    final int resultIndex = result.indexOf(next.getId());

                    if (resultIndex < 0) {
                        iter.remove();
                    } else {
                        result.remove(resultIndex);
                    }
                }

                if (result.isEmpty()) {
                    // result is empty means we don't have new news
                    if (news.isEmpty()) {
                        setLatestPubDate(0);
                    } else {
                        setLatestPubDate(news.get(0).getPubDate().getTime());
                    }

                    showData(news, countAndClearUpdatedInBackground(news));
                } else {
                    dao.readBriefAsync(getCurrentRubric(), new AsyncListener<List<T>>() {
                        @Override
                        public void onSuccess(List<T> result) {
                            if (isResumed()) {
                                showData(result, countAndClearUpdatedInBackground(result));
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

    private int countAndClearUpdatedInBackground(List<T> news) {
        int result = 0;

        for (T n : news) {
            if (n.isUpdatedInBackground()) {
                ++result;
                n.setUpdatedInBackground(false);
            }
        }

        clearUpdatedInBackgroundFlag();
        return result;
    }

    private void clearUpdatedInBackgroundFlag() {
        dao.clearUpdatedInBackgroundFlagAsync(new AsyncListener<Integer>() {
            @Override
            public void onSuccess(Integer value) {}

            @Override
            public void onFailure(Exception e) {}
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (itemSelectionListener != null) {
            itemSelectionListener.onItemSelected(position, id);
        }

        final T news = newsAdapter.getItem(position);
        if (!news.isRead()) {
            news.setRead(true);
        }
    }

    private void showData(List<T> data, int updatedInBackground) {
        if (updatedInBackground <= 0 && !data.isEmpty() && getLatestPubDate() != data.get(0).getPubDate().getTime()) {
            clearScrollPosition();
        }

        newsAdapter.setNewsObjects(data, getExpandedItemIds());
        newsAdapter.notifyDataSetChanged();

        if (!scrolled) {
            restoreScrollPosition();
        }

        if (updatedInBackground > 0) {
            Toast.makeText(getActivity(), String.format(autoRefreshToast, updatedInBackground), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!scrolled) {
            scrolled = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

    protected abstract AsyncNODao<T> createDao(Context context);
    protected abstract NewsObjectAdapter<T> createAdapter(Context context);
    protected abstract String createAutoRefreshToast(Context context);
}
