package cz.fit.lentaruand.ui.fragments;

import java.util.Collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.AsyncDao;
import cz.fit.lentaruand.data.dao.AsyncDao.DaoReadMultiListener;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.service.ServiceCallbackListener;
import cz.fit.lentaruand.service.ServiceHelper;
import cz.fit.lentaruand.ui.activities.NewsFullActivity;

/**
 * This is general fragment that shows list of loaded news objects (News,
 * Articles, etc.). The way how item will be shown in the list depends on the
 * used list adapter that is passed as an argument in constructor.
 * 
 * @author nnm
 * 
 * @param <T>
 */
public class SwipeNewsListFragment extends ListFragment {
	private NewsObjectAdapter<News> newsObjectsAdapter;
	private ServiceHelper serviceHelper;
	private AsyncDao<News> dao;

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
		public void onFailed(int requestId, Exception e, boolean rootAction) {
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		serviceHelper.updateRubric(NewsType.NEWS, Rubrics.ROOT);
	}

	@Override
	public void onResume() {
		super.onResume();
		serviceHelper.addListener(serviceListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		serviceHelper.removeListener(serviceListener);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
}
