package com.xeppaka.lentareader.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao.DaoReadMultiListener;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.DaoObserver;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.service.Callback;
import com.xeppaka.lentareader.service.ServiceHelper;
import com.xeppaka.lentareader.ui.activities.NewsFullActivity;

import java.util.Arrays;
import java.util.List;

/**
 * This is general fragment that shows list of loaded news objects (News,
 * Articles, etc.). The way how item will be shown in the list depends on the
 * used list adapter that is passed as an argument in constructor.
 * 
 * @author nnm
 */
public class NewsListFragment extends ListFragment {
	private NewsAdapter newsAdapter;
	private ServiceHelper serviceHelper;
	private AsyncNODao<News> dao;
    private Rubrics currentRubric = Rubrics.LATEST;

	private class ListFragmentServiceListener implements Callback {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onFailure() {
        }
	}
	
	private ListFragmentServiceListener serviceListener = new ListFragmentServiceListener();

    private Dao.Observer<News> newsDaoObserver = new DaoObserver<News>(new Handler()) {
        @Override
        public void onDataChanged(boolean selfChange, News dataObject) {
            newsAdapter.setNewsObjects(Arrays.asList(dataObject));
        }

        @Override
        public void onDataChanged(boolean selfChange) {
            dao.readAsync(currentRubric, new DaoReadMultiListener<News>() {
                @Override
                public void finished(List<News> result) {
                    newsAdapter.setNewsObjects(result);
                }
            });
        }
    };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        newsAdapter = new NewsAdapter(getActivity());
        serviceHelper = new ServiceHelper(this.getActivity(), new Handler());

        dao = NewsDao.getInstance(this.getActivity().getContentResolver());

		setListAdapter(newsAdapter);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

        dao.registerContentObserver(newsDaoObserver);
        refreshLocal();
	}

	@Override
	public void onPause() {
		super.onPause();

        dao.unregisterContentObserver(newsDaoObserver);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(this.getActivity(), NewsFullActivity.class);
		intent.putExtra("newsId", newsAdapter.getItem(position).getId());
		
		startActivity(intent);
	}

    public void refresh() {
        serviceHelper.updateRubric(NewsType.NEWS, currentRubric, serviceListener);
    }

    public void refreshLocal() {
        dao.readAsync(currentRubric, new DaoReadMultiListener<News>() {
            @Override
            public void finished(List<News> result) {
                newsAdapter.setNewsObjects(result);
            }
        });
    }

    public void selectRubric(Rubrics rubric) {
        currentRubric = rubric;
    }
}
