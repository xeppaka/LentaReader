package cz.fit.lentaruand;

import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import cz.fit.lentaruand.asyncloaders.AsyncNewsLoader;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.fragments.NewsBriefListFragment;

public class TestActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<News>> {
	private NewsBriefListFragment briefFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		briefFragment = new NewsBriefListFragment(getApplicationContext(), Collections.<News>emptyList());
		
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, briefFragment).commit();
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<List<News>> onCreateLoader(int id, Bundle args) {
		return new AsyncNewsLoader(getApplicationContext());
	}

	@Override
	public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
		briefFragment.showNews(data);
	}

	@Override
	public void onLoaderReset(Loader<List<News>> loader) {
	}
}
