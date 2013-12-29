package com.xeppaka.lentareader.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.viewpagerindicator.TitlePageIndicator;
import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.service.Callback;
import com.xeppaka.lentareader.service.ServiceHelper;
import com.xeppaka.lentareader.ui.fragments.NewsObjectListFragment;
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
    private View selectRubric;

    private ServiceHelper serviceHelper;
    private TitlePageIndicator indicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LentaUtils.strictMode();

		setContentView(R.layout.brief_news_activity);

		initializeViewPager();
		initializeViewIndicator();

        serviceHelper = new ServiceHelper(this, new Handler());
	}

	private void initializeViewPager() {
        selectRubric = findViewById(R.id.select_rubric_menu);
		pager = (ViewPager) findViewById(R.id.brief_news_pager);
		pagerAdapter = new SwipeNewsObjectsListAdapter(
				getSupportFragmentManager(), this);
		pager.setAdapter(pagerAdapter);
	}
	
	private void initializeViewIndicator() {
		indicator = (TitlePageIndicator) findViewById(R.id.brief_news_title_indicator);
		final float density = getResources().getDisplayMetrics().density;
        indicator.setTextSize(12 * density);
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
            case R.id.action_select_rubric:
                onSelectRubric();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onRefresh() {
        NewsObjectListFragment currentListFragment = pagerAdapter.getItem(pager.getCurrentItem());
        NewsType newsType = currentListFragment.getNewsType();
        Rubrics rubric = currentListFragment.getCurrentRubric();

        serviceHelper.updateRubric(newsType, rubric, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void onSelectRubric() {
        if (selectRubric.getVisibility() == View.INVISIBLE)
            selectRubric.setVisibility(View.VISIBLE);
        else
            selectRubric.setVisibility(View.INVISIBLE);
    }

    public void onRubricSelected(View view) {
        selectRubric.setVisibility(View.INVISIBLE);

        switch (view.getId()) {
            case R.id.button_rubric_all:
                selectRubric(Rubrics.LATEST);
                break;
            case R.id.button_rubric_russia:
                selectRubric(Rubrics.RUSSIA);
                break;
            case R.id.button_rubric_culture:
                selectRubric(Rubrics.CULTURE);
                break;
            case R.id.button_rubric_economics:
                selectRubric(Rubrics.ECONOMICS);
                break;
            case R.id.button_rubric_internet:
                selectRubric(Rubrics.MEDIA);
                break;
            case R.id.button_rubric_life:
                selectRubric(Rubrics.LIFE);
                break;
            case R.id.button_rubric_science:
                selectRubric(Rubrics.SCIENCE);
                break;
            case R.id.button_rubric_sport:
                selectRubric(Rubrics.SPORT);
                break;
            case R.id.button_rubric_ussr:
                selectRubric(Rubrics.USSR);
                break;
            case R.id.button_rubric_world:
                selectRubric(Rubrics.WORLD);
                break;
        }
    }

    private void selectRubric(Rubrics rubric) {
        final NewsObjectListFragment currentFragment = pagerAdapter.getItem(pager.getCurrentItem());

        if (currentFragment.getCurrentRubric() != rubric) {
            currentFragment.setCurrentRubric(rubric);
        }

        indicator.notifyDataSetChanged();
    }
}
