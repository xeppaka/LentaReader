package cz.fit.lentaruand;

import java.util.Collections;

import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.fragments.SwipeNewsListFragment;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SwipeNewsListFragmentPagerAdapter extends FragmentPagerAdapter {

	Context context;
	protected static final String[] CONTENT = new String[] { "Статьи",
			"Галлереи", "Колонки", "Видео", };
	
	private int PAGE_COUNT = 4;

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
		return PAGE_COUNT;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return SwipeNewsListFragmentPagerAdapter.CONTENT[position % CONTENT.length];
	}
	
	public void setCount(int count) {
        if (count > 0 && count <= 10) {
            PAGE_COUNT = count;
            notifyDataSetChanged();
        }
    }
}
