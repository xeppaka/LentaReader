package com.xeppaka.lentareader.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.ui.fragments.NewsListFragment;
import com.xeppaka.lentareader.ui.fragments.NewsObjectListFragment;

/**
 * This class represents pager adapter -> it contains all page definitions
 * that should be shown by a swipe list.
 * 
 * @author 
 *
 */
public class SwipeNewsObjectsListAdapter extends FragmentPagerAdapter {
    private static final int[] TITLES_RESOURCES = new int[] { R.string.pager_title_news,
            R.string.pager_title_articles, R.string.pager_title_columns,
            R.string.pager_title_photos, R.string.pager_title_videos };

	private final String[] titles;
    private final NewsObjectListFragment[] fragments;

	public SwipeNewsObjectsListAdapter(FragmentManager fragmentManager, Context context) {
		super(fragmentManager);

        fragments = new NewsObjectListFragment[NewsType.values().length];
        titles = new String[TITLES_RESOURCES.length];
        final Resources resources = context.getResources();

        for (int i = 0; i < TITLES_RESOURCES.length; ++i) {
            titles[i] = resources.getString(TITLES_RESOURCES[i]);
        }
    }

    @Override
	public NewsObjectListFragment getItem(int position) {
        NewsType newsType = NewsType.values()[position];

        switch (newsType) {
            case NEWS:
                return fragments[position] = new NewsListFragment();
            case ARTICLE:
                return fragments[position] = new NewsListFragment();
            default:
                throw new AssertionError();
        }
	}

	@Override
	public int getCount() {
		return 2;//TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
        final NewsObjectListFragment currentFragment = getFragment(position);

		return titles[position] + ": " + currentFragment.getCurrentRubric().getLabel();
	}

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final NewsObjectListFragment instFragment = (NewsObjectListFragment) super.instantiateItem(container, position);
        fragments[position] = instFragment;

        return instFragment;
    }

    public NewsObjectListFragment getFragment(int position) {
        return fragments[position];
    }
}
