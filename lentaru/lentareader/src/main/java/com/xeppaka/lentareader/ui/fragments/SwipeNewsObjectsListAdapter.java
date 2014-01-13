package com.xeppaka.lentareader.ui.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;

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
    private static final int[] TITLES_RESOURCES = new int[] { R.string.pager_title_news,
            R.string.pager_title_articles, R.string.pager_title_columns,
            R.string.pager_title_photos, R.string.pager_title_videos };

	private final String[] titles;
    private final NewsObjectListFragment[] fragments;
    private NewsObjectListFragment.ItemSelectionListener itemSelectionListener;

	public SwipeNewsObjectsListAdapter(FragmentManager fragmentManager, Context context, NewsObjectListFragment.ItemSelectionListener itemSelectionListener) {
		super(fragmentManager);

        this.itemSelectionListener = itemSelectionListener;

        titles = new String[TITLES_RESOURCES.length];
        final Resources resources = context.getResources();

        for (int i = 0; i < TITLES_RESOURCES.length; ++i) {
            titles[i] = resources.getString(TITLES_RESOURCES[i]);
        }

        fragments = new NewsObjectListFragment[TITLES_RESOURCES.length];

        final List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof NewsObjectListFragment) {
                    ((NewsObjectListFragment) fragment).setItemSelectionListener(itemSelectionListener);
                }
            }
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
	public NewsObjectListFragment getItem(int position) {
        NewsType newsType = NewsType.values()[position];

        switch (newsType) {
            case NEWS:
                if (fragments[position] == null)
                    fragments[position] = new NewsListFragment(itemSelectionListener);

                fragments[position].setItemSelectionListener(itemSelectionListener);
                return fragments[position];
            default:
                throw new AssertionError();
        }
	}

	@Override
	public int getCount() {
		return 1;//TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
        final NewsObjectListFragment currentFragment = getItem(position);

		return titles[position] + ": " + currentFragment.getCurrentRubric().getLabel();
	}
}
