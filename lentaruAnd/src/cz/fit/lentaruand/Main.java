package cz.fit.lentaruand;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

import cz.fit.lentaruand.ui.SwipeNewsListFragmentPagerAdapter;

/**
 * 
 * @author kacpa01
 * 
 */
public class Main extends SherlockFragmentActivity {

	//private Button showNews;
	PageIndicator mIndicator;
	SwipeNewsListFragmentPagerAdapter pagerAdapter;
	ViewPager pager;
	String date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final ActionBar bar = getSupportActionBar();
		bar.setTitle(R.string.action_bar_title);
		// date = new Date().toString();
		// date.offsetByCodePoints(0, 10);
		// bar.setSubtitle(date);

		pager = (ViewPager) findViewById(R.id.pager);
		pagerAdapter = new SwipeNewsListFragmentPagerAdapter(
				getSupportFragmentManager(), getApplicationContext());
		pager.setAdapter(pagerAdapter);

		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator = indicator;
		final float density = getResources().getDisplayMetrics().density;
		indicator.setTextColor(Color.parseColor("#151515"));
		indicator.setBackgroundColor(Color.parseColor("#ffffff"));
		indicator.setFooterColor(0xFFAA2222);
		indicator.setFooterLineHeight(1 * density); // 1dp
		indicator.setFooterIndicatorHeight(3 * density); // 3dp
		indicator.setFooterIndicatorStyle(IndicatorStyle.Triangle);
		indicator.setSelectedColor(Color.parseColor("#d54c39"));
		indicator.setSelectedBold(true);
		mIndicator.setViewPager(pager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	
}
