package cz.fit.lentaruand.ui;

import java.util.Collections;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cz.fit.lentaruand.data.News;

/**
 * This class represents pager adapter -> it contains all page definitions
 * that should be shown by a swipe list.
 * 
 * @author 
 *
 */
public class SwipeNewsListFragmentPagerAdapter extends FragmentPagerAdapter {
	private Context context;
	private static final String[] CONTENT = new String[] { "Статьи",
			"Галлереи", "Колонки", "Видео" };

	public SwipeNewsListFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
		
	}

	@Override
	public Fragment getItem(int position) {
		return SwipeNewsListFragment.newInstance(position, context, Collections.<News>emptyList());
	}

	@Override
	public int getCount() {
		return CONTENT.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return SwipeNewsListFragmentPagerAdapter.CONTENT[position % CONTENT.length];
	}
}
