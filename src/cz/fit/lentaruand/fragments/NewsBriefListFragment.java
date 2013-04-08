package cz.fit.lentaruand.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import cz.fit.lentaruand.NewsAdapter;
import cz.fit.lentaruand.data.News;

public class NewsBriefListFragment extends ListFragment {
	
	private NewsAdapter newsAdapter;
	
	public NewsBriefListFragment(Context context, List<News> news) {
		newsAdapter = new NewsAdapter(context, news);
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
}
