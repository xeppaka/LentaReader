package cz.fit.lentaruand.fragments;

import java.util.List;

import cz.fit.lentaruand.NewsAdapter;
import cz.fit.lentaruand.data.News;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewsFullFragment extends Fragment implements LoaderManager.LoaderCallbacks<News>{

	private NewsAdapter newsAdapter;
	private Context context;
	private News news;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.full_news_fragment_layout, null);
	}

	public NewsFullFragment(Context context, News news) {
		this.context = context;
		this.news = news;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void showNews(List<News> news) {
		newsAdapter.setNews(news);
		newsAdapter.notifyDataSetChanged();
	}

	@Override
	public Loader<News> onCreateLoader(int id, Bundle args) {
		return new AsyncFullNewsLoader(context, news);
	}

	@Override
	public void onLoaderReset(Loader<News> loader) {
	}

	@Override
	public void onLoadFinished(Loader<News> arg0, News arg1) {
		// TODO Auto-generated method stub
		
	}

	

}
