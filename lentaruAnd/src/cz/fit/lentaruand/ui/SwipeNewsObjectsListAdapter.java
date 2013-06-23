package cz.fit.lentaruand.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cz.fit.lentaruand.R;
import cz.fit.lentaruand.asyncloaders.AsyncBriefNewsLoader;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.ui.fragments.NewsAdapter;

/**
 * This class represents pager adapter -> it contains all page definitions
 * that should be shown by a swipe list.
 * 
 * @author 
 *
 */
public class SwipeNewsObjectsListAdapter extends FragmentPagerAdapter {
	private Context context;
	private static final int[] TITLES = new int[] { R.string.pager_title_news,
			R.string.pager_title_articles, R.string.pager_title_columns,
			R.string.pager_title_photos, R.string.pager_title_videos };
	
	private final List<SwipeNewsObjectsListFragment<?>> fragments;
	
	public SwipeNewsObjectsListAdapter(FragmentManager fragmentManager, Context context) {
		super(fragmentManager);
		this.context = context;
		
		fragments = new ArrayList<SwipeNewsObjectsListFragment<?>>();
		fragments.add(new SwipeNewsObjectsListFragment<News>(new AsyncBriefNewsLoader(context), new NewsAdapter(context)));
		fragments.add(new SwipeNewsObjectsListFragment<News>(new AsyncBriefNewsLoader(context), new NewsAdapter(context)));
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return 2;//TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return context.getResources().getText(SwipeNewsObjectsListAdapter.TITLES[position]);
	}
}
