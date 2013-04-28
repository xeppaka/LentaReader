package cz.fit.lentaruand.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cz.fit.lentaruand.FullNewsActivity;
import cz.fit.lentaruand.NewsAdapter;
import cz.fit.lentaruand.R;
import cz.fit.lentaruand.asyncloaders.AsyncBriefNewsLoader;
import cz.fit.lentaruand.data.News;

public class SwipeNewsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<News>> {

	int mCurrentPage;
	static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
	private String mContent = "???";
	private static final String KEY_CONTENT = "TestFragment:Content";
	private NewsAdapter newsAdapter;
	private Context context;
	TextView tv;
	
	
		
	public SwipeNewsListFragment() {
		super();
	}

	public static Fragment newInstance(int page, Context context, List<News> news) {
		SwipeNewsListFragment fragment = new SwipeNewsListFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
		fragment.mCurrentPage = page;
		fragment.newsAdapter = new NewsAdapter(context, news);
		fragment.context = context;
		switch(page){
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

//	public SwipeNewsListFragment(List<News> news, int currentPage, Context context) {
//		newsAdapter = new NewsAdapter(context, news);
//		this.mCurrentPage = currentPage;
//		this.context = context;
//	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setListAdapter(newsAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this.getActivity(), FullNewsActivity.class);
		intent.putExtra("NewsObject", newsAdapter.getItem(position));
//		newsAdapter.getItem(position);
//		Toast.makeText(getActivity(), mContent,
//				Toast.LENGTH_SHORT).show();
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
		if(mContent == "News")getLoaderManager().initLoader(0, null, this).forceLoad();
		if(mContent == "Error") {
			tv.setText(mContent);
			}
		
	}
	
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		outState.putString(KEY_CONTENT, mContent);
//	}

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
