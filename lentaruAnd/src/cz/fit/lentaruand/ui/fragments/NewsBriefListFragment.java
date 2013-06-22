package cz.fit.lentaruand.ui.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import cz.fit.lentaruand.asyncloaders.AsyncBriefNewsLoader;
import cz.fit.lentaruand.data.News;

public class NewsBriefListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<News>> {
	
	private NewsAdapter newsAdapter;
	private Context context;
	
	public NewsBriefListFragment(Context context, List<News> news) {
		newsAdapter = new NewsAdapter(context, news);
		this.context = context;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(newsAdapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
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
}
