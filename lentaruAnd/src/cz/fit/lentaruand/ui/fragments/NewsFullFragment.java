package cz.fit.lentaruand.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cz.fit.lentaruand.R;
import cz.fit.lentaruand.asyncloaders.AsyncFullNewsLoader;
import cz.fit.lentaruand.data.News;

public class NewsFullFragment extends Fragment implements LoaderManager.LoaderCallbacks<News> {

	private Context context;
	private News news;

	public NewsFullFragment(Context context, News news){
		this.context = context;
		this.news = news;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.full_news_fragment, null);
	}

	public NewsFullFragment() {
		super();
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

	public void showNews(News news) {
		TextView tv = (TextView) getActivity().findViewById(R.id.textView1);
		tv.setText(Html.fromHtml(news.getFullText()));
		tv.setMovementMethod(LinkMovementMethod.getInstance());
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
		showNews(arg1);
	}
}
