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
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao.DaoReadMultiListener;
import com.xeppaka.lentareader.data.dao.objects.NewsDao;
import com.xeppaka.lentareader.service.ServiceCallbackListener;
import com.xeppaka.lentareader.service.ServiceHelper;
import com.xeppaka.lentareader.ui.activities.NewsFullActivity;

import java.util.Collection;
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
	private AsyncDao<News> dao;
    private Rubrics currentRubric = Rubrics.SCIENCE;
	
	private class ListFragmentServiceListener implements ServiceCallbackListener {

		@Override
		public void onDatabaseObjectsCreate(int requestId, NewsType newsType,
				Collection<Long> newIds) {
			if (newIds.size() <= 0) {
				return;
			}
			
			AsyncDao<News> newsDao = NewsDao.getInstance(getActivity().getContentResolver());
			newsDao.readAsync(new DaoReadMultiListener<News>() {
				@Override
				public void finished(List<News> result) {
					showNewsObjects(result);
				}
			});
		}

		@Override
		public void onDatabaseObjectsUpdate(int requestId, NewsType newsType,
				Collection<Long> ids) {
		}

		@Override
		public void onImagesUpdate(int requestId, NewsType newsType,
				Collection<Long> ids) {
            newsAdapter.notifyDataSetChanged();
		}

		@Override
		public void onFailed(int requestId, Exception e) {
		}
	}
	
	private ListFragmentServiceListener serviceListener = new ListFragmentServiceListener();
	
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
		
		serviceHelper.addListener(serviceListener);
		dao.readAsync(new DaoReadMultiListener<News>() {
			@Override
			public void finished(List<News> result) {
                showNewsObjects(result);
                serviceHelper.updateRubric(NewsType.NEWS, currentRubric);
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		
		serviceHelper.removeListener(serviceListener);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = new Intent(this.getActivity(), NewsFullActivity.class);
		intent.putExtra("newsId", newsAdapter.getItem(position).getId());
		
		startActivity(intent);
	}

	private void showNewsObjects(List<News> newsObjects) {
		newsAdapter.setNewsObjects(newsObjects);
	}

    public void refresh() {
        serviceHelper.updateRubric(NewsType.NEWS, currentRubric);
    }
}
