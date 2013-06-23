package cz.fit.lentaruand.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

import cz.fit.lentaruand.R;
import cz.fit.lentaruand.data.NewsObject;
import cz.fit.lentaruand.ui.activities.NewsFullActivity;
import cz.fit.lentaruand.ui.fragments.NewsObjectAdapter;

public class SwipeNewsObjectsListFragment<T extends NewsObject> extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<List<T>> {

	private NewsObjectAdapter<T> newsObjectsAdapter;
	private Loader<List<T>> newsObjectsLoader;

	public SwipeNewsObjectsListFragment(Loader<List<T>> newsObjectsLoader, NewsObjectAdapter<T> newsObjectsAdapter) {
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
		Intent intent = new Intent(this.getSherlockActivity(),
				NewsFullActivity.class);
		intent.putExtra("NewsObject", newsObjectsAdapter.getItem(position));
		startActivity(intent);
	}

	public void showNewsObjects(List<T> newsObjects) {
		newsObjectsAdapter.setNewsObjects(newsObjects);
		newsObjectsAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.news_list_fragment_layout,
				container, false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getLoaderManager().initLoader(0, null, this).forceLoad();
//		newsList = getListView();
//		newsList.setLongClickable(true);
//		newsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//		newsList.setOnItemLongClickListener(
//				new ActionModeHelper(this, newsList));
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

//	private ActionMode.Callback callback = new ActionMode.Callback() {
//
//		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//			mode.getMenuInflater().inflate(R.menu.context_news_list, menu);
//			return true;
//		}
//
//		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//			return false;
//		}
//
//		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//			// Log.d(LOG_TAG, "item " + item.getTitle());
//			return false;
//		}
//
//		public void onDestroyActionMode(ActionMode mode) {
//			// Log.d(LOG_TAG, "destroy");
//		}
//
//	};
	
//	public boolean performAction(int itemId, int checkedItemPosition) {
//		switch (itemId) {
//		case R.id.save:
//			dbHelper = new LentaDbHelper(context);
//			SQLiteDatabase db = dbHelper.getWritableDatabase();
//			NewsDao newsDao = new NewsDao();
//			try {
//			  newsDao.create(db, newsAdapter.getItem(checkedItemPosition));
//			} finally {
//			 db.close();
//			}
//			Toast.makeText(this.getSherlockActivity(), "TODO saving of the news", Toast.LENGTH_LONG)
//					.show();
//			return (true);
//
//		case R.id.openNews:
//			Intent intent = new Intent(this.getSherlockActivity(),
//					NewsFullActivity.class);
//			intent.putExtra("NewsObject", newsAdapter.getItem(checkedItemPosition));
//			startActivity(intent);
//			Toast.makeText(this.getSherlockActivity(), "TODO opening the news" + "position is:" + checkedItemPosition, Toast.LENGTH_LONG)
//					.show();
//			return (true);
//		}
//
//		return false;
//	}
}
