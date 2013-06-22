package cz.fit.lentaruand.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import cz.fit.lentaruand.R;
import cz.fit.lentaruand.asyncloaders.AsyncBriefNewsLoader;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.data.db.LentaDbHelper;
import cz.fit.lentaruand.ui.activities.NewsFullActivity;
import cz.fit.lentaruand.ui.fragments.NewsAdapter;

public class SwipeNewsListFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<List<News>> {

	int mCurrentPage;
	private LentaDbHelper dbHelper;
	static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
	private String mContent = "???";
	private NewsAdapter newsAdapter;
	private Context context;
	ListView newsList;
	TextView tv;

	public SwipeNewsListFragment() {
		super();
	}

	public static Fragment newInstance(int page, Context context,
			List<News> news) {
		SwipeNewsListFragment fragment = new SwipeNewsListFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
		fragment.mCurrentPage = page;
		fragment.newsAdapter = new NewsAdapter(context, news);
		fragment.context = context;
		switch (page) {
		case 0:
			fragment.mContent = "News";
			break;
		case 1:
			fragment.mContent = "Galleries";
			break;
		case 2:
			fragment.mContent = "Columns";
			break;
		case 3:
			fragment.mContent = "Video";
			break;
		default:
			fragment.mContent = "Error";
			break;
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter(newsAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this.getSherlockActivity(),
				NewsFullActivity.class);
		intent.putExtra("NewsObject", newsAdapter.getItem(position));
		startActivity(intent);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	public void showNews(List<News> news) {
		newsAdapter.setNews(news);
		newsAdapter.notifyDataSetChanged();
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
		
		if (mContent == "News")
			getLoaderManager().initLoader(0, null, this).forceLoad();
		if (mContent == "Error") {
			tv.setText(mContent);
		}
		newsList = getListView();
		newsList.setLongClickable(true);
		newsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		newsList.setOnItemLongClickListener(
				new ActionModeHelper(this, newsList));
	}

	// @Override
	// public void onSaveInstanceState(Bundle outState) {
	// super.onSaveInstanceState(outState);
	// outState.putString(KEY_CONTENT, mContent);
	// }

	@Override
	public Loader<List<News>> onCreateLoader(int id, Bundle args) {
		return new AsyncBriefNewsLoader(context);
	}

	@Override
	public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
		showNews(data);
	}

	@Override
	public void onLoaderReset(Loader<List<News>> loader) {
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
	
	public boolean performAction(int itemId, int checkedItemPosition) {
		switch (itemId) {
		case R.id.save:
			dbHelper = new LentaDbHelper(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			NewsDao newsDao = new NewsDao();
			try {
			  newsDao.create(db, newsAdapter.getItem(checkedItemPosition));
			} finally {
			 db.close();
			}
			Toast.makeText(this.getSherlockActivity(), "TODO saving of the news", Toast.LENGTH_LONG)
					.show();
			return (true);

		case R.id.openNews:
			Intent intent = new Intent(this.getSherlockActivity(),
					NewsFullActivity.class);
			intent.putExtra("NewsObject", newsAdapter.getItem(checkedItemPosition));
			startActivity(intent);
			Toast.makeText(this.getSherlockActivity(), "TODO opening the news" + "position is:" + checkedItemPosition, Toast.LENGTH_LONG)
					.show();
			return (true);
		}

		return false;
	}
}
