package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.xeppaka.lentareader.ui.widgets.fullnews.builder.FullNewsElementsBuilder;

import java.util.Collection;
import java.util.List;

/**
 * Created by nnm on 3/16/14.
 */
public class NewsFullListFragment extends ListFragment {
    private long id;
    private String link;

    public NewsFullListFragment(long id) {
        this.id = id;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        final Activity activity = getActivity();
        final AsyncDao<News> dao = NewsDao.getInstance(activity.getContentResolver());
        dao.readAsync(id, new AsyncListener<News>() {
            @Override
            public void onSuccess(News value) {
                final FullNewsElementsBuilder builder = new FullNewsElementsBuilder(value, getActivity(), NewsFullListFragment.this);

                setListAdapter(new FullNewsAdapter(builder.build()));
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

    public String getLink() {
        return link;
    }
}
