package cz.fit.lentaruand.ui.fragments;

import java.util.Collection;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.dao.DefaultDao;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.service.UpdateService;
import cz.fit.lentaruand.ui.activities.NewsFullActivity;

/**
 * This is general fragment that shows list of loaded news objects (News, Articles, etc.).
 * The way how item will be shown in the list depends on the used list adapter that is 
 * passed as an argument in constructor.
 * 
 * @author nnm
 *
 * @param <T>
 */
public class SwipeNewsObjectsListFragment<T extends NewsObject> extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<T>> {

	private NewsObjectAdapter<T> newsObjectsAdapter;
	private Loader<List<T>> newsObjectsLoader;
	private DefaultDao<T> dataDao;

	public SwipeNewsObjectsListFragment(Loader<List<T>> newsObjectsLoader, NewsObjectAdapter<T> newsObjectsAdapter) {
		if (newsObjectsLoader == null) {
			throw new IllegalArgumentException("Argument newsObjectLoader must not be null.");
		}
		
		if (newsObjectsAdapter == null) {
			throw new IllegalArgumentException("Argument newsObjectAdapter must not be null.");
		}
		
		this.newsObjectsLoader = newsObjectsLoader;
		this.newsObjectsAdapter = newsObjectsAdapter;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(newsObjectsAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this.getActivity(),
				NewsFullActivity.class);
		intent.putExtra("NewsObject", newsObjectsAdapter.getItem(position));
		startActivity(intent);
	}

	public void showNewsObjects(Collection<T> newsObjects) {
		newsObjectsAdapter.setNewsObjects(newsObjects);
		newsObjectsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ContentResolver cr = getActivity().getContentResolver();
		dataDao = (DefaultDao<T>)new NewsDao(cr);
		dataDao.registerContentObserver(true, new MyContentObserver(new Handler()));
		
//		newsList = getListView();
//		newsList.setLongClickable(true);
//		newsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//		newsList.setOnItemLongClickListener(
//				new ActionModeHelper(this, newsList));
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//getLoaderManager().initLoader(0, null, this).forceLoad();
		getActivity().startService(new Intent(this.getActivity(), UpdateService.class));
	}

	@Override
	public Loader<List<T>> onCreateLoader(int id, Bundle args) {
		return newsObjectsLoader;
	}

	@Override
	public void onLoadFinished(Loader<List<T>> loader, List<T> data) {
		showNewsObjects(data);
	}

	@Override
	public void onLoaderReset(Loader<List<T>> loader) {
	}
	
	private class MyContentObserver extends ContentObserver {
		public MyContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public boolean deliverSelfNotifications() {
			return super.deliverSelfNotifications();
		}

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			Collection<T> news = dataDao.read();
			showNewsObjects(news);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
		}
	}
}
