package com.xeppaka.lentareader.ui.fragments;

import java.util.Collection;

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

/**
 * This is general fragment that shows list of loaded news objects (News,
 * Articles, etc.). The way how item will be shown in the list depends on the
 * used list adapter that is passed as an argument in constructor.
 * 
 * @author nnm
 */
public class SwipeNewsListFragment extends ListFragment {
	private NewsObjectAdapter<News> newsObjectsAdapter;
	private ServiceHelper serviceHelper;
	private AsyncDao<News> dao;
	
	private boolean updateRubric = true;
	
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
				public void finished(Collection<News> result) {
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
			newsObjectsAdapter.notifyDataSetChanged();
		}

		@Override
		public void onFailed(int requestId, Exception e) {
		}
	}
	
	private ListFragmentServiceListener serviceListener = new ListFragmentServiceListener();
	
	public SwipeNewsListFragment(NewsObjectAdapter<News> newsObjectsAdapter) {
		if (newsObjectsAdapter == null) {
			throw new IllegalArgumentException(
					"Argument newsObjectAdapter must not be null.");
		}

		this.newsObjectsAdapter = newsObjectsAdapter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(newsObjectsAdapter);
		
		serviceHelper = new ServiceHelper(this.getActivity(), new Handler());
		
		if (updateRubric) {
			serviceHelper.updateRubric(NewsType.NEWS, Rubrics.ROOT);
			updateRubric = false;
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		serviceHelper.addListener(serviceListener);
		
		dao = NewsDao.getInstance(this.getActivity().getContentResolver());
		dao.readAsync(new DaoReadMultiListener<News>() {
			@Override
			public void finished(Collection<News> result) {
				showNewsObjects(result);
				serviceHelper.updateRubric(NewsType.NEWS, Rubrics.ROOT);
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
		intent.putExtra("newsId", newsObjectsAdapter.getItem(position).getId());
		
		startActivity(intent);
	}

	private void showNewsObjects(Collection<News> newsObjects) {
		newsObjectsAdapter.setNewsObjects(newsObjects);
		newsObjectsAdapter.notifyDataSetChanged();
	}
}
