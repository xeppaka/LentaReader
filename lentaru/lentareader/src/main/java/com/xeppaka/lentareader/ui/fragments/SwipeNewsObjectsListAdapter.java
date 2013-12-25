package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;

import com.xeppaka.lentareader.R;

import java.util.ArrayList;
import java.util.List;

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
	
	private final List<NewsListFragment> fragments;
	
	public SwipeNewsObjectsListAdapter(FragmentManager fragmentManager, Context context) {
		super(fragmentManager);
		this.context = context;
		
		fragments = new ArrayList<NewsListFragment>();
		fragments.add(new NewsListFragment());
	}

	@Override
	public NewsListFragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return 1;//TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return context.getResources().getText(SwipeNewsObjectsListAdapter.TITLES[position]);
	}

    public void refresh(int position) {
        getItem(position).refresh();
    }
}
