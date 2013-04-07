package cz.fit.lentaruand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ListView;
import cz.fit.lentaruand.asyncloaders.AsyncNewsLoader;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;

public class TestActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<News>> {
	private ListView newsListView;
	private NewsAdapter newsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		newsAdapter = new NewsAdapter(getApplicationContext(), Collections.<News>emptyList());
		
		newsListView = (ListView)findViewById(R.id.newsList);
		newsListView.setAdapter(newsAdapter);
		
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<News>> onCreateLoader(int id, Bundle args) {
		return new AsyncNewsLoader(getApplicationContext(), Rubrics.RUSSIA);
	}

	@Override
	public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
		newsAdapter.setNews(data);
		newsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<List<News>> loader) {
	}
}
