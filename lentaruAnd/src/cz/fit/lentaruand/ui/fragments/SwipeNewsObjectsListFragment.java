package cz.fit.lentaruand.ui.fragments;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.Dao;
import cz.fit.lentaruand.data.dao.DaoObserver;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.service.ServiceCallbackListener;
import cz.fit.lentaruand.service.ServiceHelper;
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
@SuppressLint("ValidFragment")
public class SwipeNewsObjectsListFragment<T extends NewsObject> extends ListFragment implements ServiceCallbackListener { 
			//implements LoaderManager.LoaderCallbacks<List<T>> {

	private NewsObjectAdapter<T> newsObjectsAdapter;
	private Loader<List<T>> newsObjectsLoader;
	//private ContentResolverDao<T> dataDao;

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
		Dao<News> dataDao= NewsDao.getInstance(cr);
		dataDao.registerContentObserver((Dao.Observer<News>)(new MyContentObserver(new Handler())));
		
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
		//getActivity().startService(new Intent(this.getActivity(), UpdateService.class));
		ServiceHelper serviceHelper = new ServiceHelper(this.getActivity());
		serviceHelper.addListener(this);
		serviceHelper.downloadListOfBriefNews(Rubrics.ECONOMICS);
	}

//	@Override
//	public Loader<List<T>> onCreateLoader(int id, Bundle args) {
//		return newsObjectsLoader;
//	}
//
//	@Override
//	public void onLoadFinished(Loader<List<T>> loader, List<T> data) {
//		showNewsObjects(data);
//	}
//
//	@Override
//	public void onLoaderReset(Loader<List<T>> loader) {
//	}
	
	private class MyContentObserver extends DaoObserver<T> {
		public MyContentObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onDataChanged(boolean selfChange, T dataObject) {
			showNewsObjects(Arrays.<T>asList(dataObject));
		}

		@Override
		public void onDataChanged(boolean selfChange, Collection<T> dataObjects) {
			showNewsObjects(dataObjects);
		}
	}

	@Override
	public void onServiceCallback(int requestId, Intent requestIntent,
			int resultCode, Bundle data) {
		Toast.makeText(getActivity(), "Callback received! data in bundle = " + data.getString("EXTRA_STRING"), Toast.LENGTH_SHORT).show();
		
	}
}
