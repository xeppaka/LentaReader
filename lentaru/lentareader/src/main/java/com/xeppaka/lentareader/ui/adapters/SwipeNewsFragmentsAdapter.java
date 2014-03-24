package com.xeppaka.lentareader.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.ui.fragments.ArticleListFragment;
import com.xeppaka.lentareader.ui.fragments.NewsListFragment;
import com.xeppaka.lentareader.ui.fragments.ListFragmentBase;

/**
 * This class represents pager adapter -> it contains all page definitions
 * that should be shown by a swipe list.
 * 
 * @author 
 *
 */
public class SwipeNewsFragmentsAdapter extends FragmentPagerAdapter {
    private static final int[] TITLES_RESOURCES = new int[] { R.string.pager_title_news,
            R.string.pager_title_articles, R.string.pager_title_columns,
            R.string.pager_title_photos, R.string.pager_title_videos };

	private final String[] titles;
    private final ListFragmentBase[] fragments;

	public SwipeNewsFragmentsAdapter(FragmentManager fragmentManager, Context context) {
		super(fragmentManager);

        fragments = new ListFragmentBase[NewsType.values().length];
        titles = new String[TITLES_RESOURCES.length];

        final Resources resources = context.getResources();

        for (int i = 0; i < TITLES_RESOURCES.length; ++i) {
            titles[i] = resources.getString(TITLES_RESOURCES[i]);
        }
    }

    @Override
	public ListFragmentBase getItem(int position) {
        if (fragments[position] != null) {
            return fragments[position];
        } else {
            switch (position) {
                case 0:
                    return fragments[0] = new NewsListFragment();
                case 1:
                    return fragments[1] = new ArticleListFragment();
            }
        }

        throw new IllegalStateException("No fragment found");
	}

	@Override
	public int getCount() {
		return 2;//TITLES.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
        final ListFragmentBase currentFragment = getFragment(position);

		return currentFragment.isActive() ? (titles[position] + ": " + currentFragment.getCurrentRubric().getLabel()) :
               titles[position];
	}

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ListFragmentBase instFragment = (ListFragmentBase) super.instantiateItem(container, position);
        fragments[position] = instFragment;

        return instFragment;
    }

    public ListFragmentBase getFragment(int position) {
        return fragments[position];
    }

    public void clearActiveFragments() {
        for (ListFragmentBase fragment : fragments) {
            if (fragment != null) {
                fragment.setActive(false);
            }
        }
    }
}
