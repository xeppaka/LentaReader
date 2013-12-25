package com.xeppaka.lentareader.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.viewpagerindicator.TitlePageIndicator;
import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.ui.fragments.SwipeNewsObjectsListAdapter;
import com.xeppaka.lentareader.utils.LentaUtils;

/**
 * This is the main activity where everything starts right after application is 
 * loaded. 
 * 
 * @author 
 * 
 */
public class NewsBriefActivity extends ActionBarActivity {
	private SwipeNewsObjectsListAdapter pagerAdapter;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LentaUtils.strictMode();

		setContentView(R.layout.brief_news_activity);

		initializeViewPager();
		initializeViewIndicator();
	}

	private void initializeViewPager() {
		pager = (ViewPager) findViewById(R.id.brief_news_pager);
		pagerAdapter = new SwipeNewsObjectsListAdapter(
				getSupportFragmentManager(), this);
		pager.setAdapter(pagerAdapter);
	}
	
	private void initializeViewIndicator() {
		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.brief_news_title_indicator);
		final float density = getResources().getDisplayMetrics().density;
		indicator.setTextColor(Color.parseColor("#151515"));
//		indicator.setBackgroundColor(Color.parseColor("#ffffff"));
		indicator.setFooterColor(0xFFAA2222);
		indicator.setFooterLineHeight(1 * density); // 1dp
		indicator.setFooterIndicatorHeight(3 * density); // 3dp
		indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Triangle);
		indicator.setSelectedColor(Color.parseColor("#d54c39"));
		indicator.setSelectedBold(true);
		indicator.setViewPager(pager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.brief_activity_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onRefresh() {
        pagerAdapter.refresh(pager.getCurrentItem());
    }
}
