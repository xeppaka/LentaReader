package cz.fit.lentaruand.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitlePageIndicator.IndicatorStyle;

import cz.fit.lentaruand.R;
import cz.fit.lentaruand.ui.fragments.SwipeNewsObjectsListAdapter;
import cz.fit.lentaruand.utils.LentaConstants;

/**
 * This is the main activity where everything starts right after application is 
 * loaded. 
 * 
 * @author 
 * 
 */
public class NewsBriefActivity extends SherlockFragmentActivity {
	private SwipeNewsObjectsListAdapter pagerAdapter;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (LentaConstants.SDK_VER > 10 && LentaConstants.DEVELOPER_MODE) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() 
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}

		setContentView(R.layout.brief_news_activity);

		initializeActionBar();
		initializeViewPager();
		initializeViewIndicator();
	}

	private void initializeActionBar() {
		final ActionBar bar = getSupportActionBar();
		bar.setTitle(R.string.action_bar_title);
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
		indicator.setBackgroundColor(Color.parseColor("#ffffff"));
		indicator.setFooterColor(0xFFAA2222);
		indicator.setFooterLineHeight(1 * density); // 1dp
		indicator.setFooterIndicatorHeight(3 * density); // 3dp
		indicator.setFooterIndicatorStyle(IndicatorStyle.Triangle);
		indicator.setSelectedColor(Color.parseColor("#d54c39"));
		indicator.setSelectedBold(true);
		indicator.setViewPager(pager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
