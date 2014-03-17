package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.ui.adapters.FullNewsAdapter;
import com.xeppaka.lentareader.ui.adapters.FullNewsAdapterBase;
import com.xeppaka.lentareader.ui.widgets.fullnews.ListElementOptions;
import com.xeppaka.lentareader.ui.widgets.fullnews.builder.FullNewsElementsBuilder;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by nnm on 3/16/14.
 */
public class NewsFullFragment extends ListFragment {
    private long id;
    private String link;
    private ListElementOptions options;

    public NewsFullFragment(long id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        final Activity activity = getActivity();
        final AsyncDao<News> dao = NewsDao.getInstance(activity.getContentResolver());
        dao.readAsync(id, new AsyncListener<News>() {
            @Override
            public void onSuccess(News news) {
                if (isResumed()) {
                    link = news.getLink();

                    final FullNewsElementsBuilder builder = new FullNewsElementsBuilder(news, getActivity(), NewsFullFragment.this);
                    builder.setOptions(options);

                    setListAdapter(new FullNewsAdapter(builder.build()));
                }
            }

            @Override
            public void onFailure(Exception e) {}
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = getListView();
        listView.setSelector(new ColorDrawable(0x00000000));
        listView.setDividerHeight(0);
    }

    @Override
    public void onStart() {
        super.onStart();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final boolean downloadImages = preferences.getBoolean(PreferencesConstants.PREF_KEY_DOWNLOAD_IMAGE_FULL, PreferencesConstants.DOWNLOAD_IMAGE_FULL_DEFAULT);
        final int textSize = preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_FULL_TEXT_SIZE, PreferencesConstants.NEWS_FULL_TEXT_SIZE_DEFAULT);

        options = new ListElementOptions(textSize, downloadImages);
    }

    public String getLink() {
        return link;
    }
}
