package cz.fit.lentaruand.ui.fragments;

import java.util.Collection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
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
public class SwipeNewsObjectsListFragment<T extends NewsObject> extends ListFragment {
	private NewsObjectAdapter<T> newsObjectsAdapter;
	private ServiceHelper serviceHelper;

	private class ListFragmentServiceListener implements ServiceCallbackListener {
		@Override
		public void onDatabaseObjectsCreated(NewsType newsType,
				Collection<Long> newIds) {
			NewsDao.getInstance(getActivity().getContentResolver()).readAsync(new DaoReadMultiListener<News>() {
				@Override
				public void finished(Collection<News> result) {
					showNewsObjects((Collection<T>)result);
				}
			});
		}

		@Override
		public void onDatabaseObjectCreated(NewsType newsType, long newId) {
		}

		@Override
		public void onDatabaseObjectsUpdated(NewsType newsType,
				Collection<Long> ids) {
		}

		@Override
		public void onDatabaseObjectUpdated(NewsType newsType, long id) {
		}

		@Override
		public void onImageUpdated(NewsType newsType, long id) {
		}
	}
	
	private ListFragmentServiceListener serviceListener = new ListFragmentServiceListener();
	
	public SwipeNewsObjectsListFragment(NewsObjectAdapter<T> newsObjectsAdapter) {
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
		
		serviceHelper = new ServiceHelper(this.getActivity());
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this.getActivity(), NewsFullActivity.class);
		intent.putExtra("newsId", newsObjectsAdapter.getItem(position).getId());
		startActivity(intent);
	}

	private void showNewsObjects(Collection<T> newsObjects) {
		newsObjectsAdapter.setNewsObjects(newsObjects);
		newsObjectsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		serviceHelper.downloadListOfBriefNews(Rubrics.ROOT);
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
