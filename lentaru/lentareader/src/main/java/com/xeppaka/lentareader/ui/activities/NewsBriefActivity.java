package com.xeppaka.lentareader.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.viewpagerindicator.TitlePageIndicator;
import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.service.Callback;
import com.xeppaka.lentareader.service.ServiceHelper;
import com.xeppaka.lentareader.ui.fragments.NewsObjectListFragment;
import com.xeppaka.lentareader.ui.fragments.RubricsSelector;
import com.xeppaka.lentareader.ui.fragments.RubricsSelectorContainer;
import com.xeppaka.lentareader.ui.fragments.SwipeNewsObjectsListAdapter;
import com.xeppaka.lentareader.ui.widgets.SelectRubricDialog;
import com.xeppaka.lentareader.utils.LentaDebugUtils;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * This is the main activity where everything starts right after application is 
 * loaded. 
 * 
 * @author 
 * 
 */
public class NewsBriefActivity extends ActionBarActivity implements DialogInterface.OnDismissListener, NewsObjectListFragment.ItemSelectionListener, RubricsSelectorContainer {
	private SwipeNewsObjectsListAdapter pagerAdapter;
	private ViewPager pager;
    private SelectRubricDialog selectRubricDialog;

    private ServiceHelper serviceHelper;
    private TitlePageIndicator indicator;

    private RubricsSelector[] rubricsSelectors = new RubricsSelector[NewsType.values().length];

    private boolean autoRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LentaDebugUtils.strictMode();

		setContentView(R.layout.brief_news_activity);
        setTitle(null);

		initializeViewPager();
		initializeViewIndicator();

        selectRubricDialog = new SelectRubricDialog(this);
        selectRubricDialog.setOnDismissListener(this);

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());


        if (actionBarHeight != 0) {
            WindowManager.LayoutParams params = selectRubricDialog.getWindow().getAttributes();

            params.gravity = Gravity.TOP | Gravity.RIGHT;
            params.y = actionBarHeight;
        }

        //getActionBar().setIcon(R.drawable.lenta_icon);
        getSupportActionBar().setLogo(R.drawable.lenta_icon);

        serviceHelper = new ServiceHelper(this, new Handler());
	}

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoRefresh = preferences.getBoolean(PreferencesConstants.PREF_KEY_NEWS_AUTO_REFRESH, PreferencesConstants.NEWS_AUTO_REFRESH_DEFAULT);

        if (autoRefresh) {
            onRefresh();
        }
    }

    private void initializeViewPager() {
        pagerAdapter = new SwipeNewsObjectsListAdapter(getSupportFragmentManager(), this);

        pager = (ViewPager) findViewById(R.id.brief_news_pager);
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
        inflater.inflate(R.menu.menu_actions, menu);

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
            case R.id.action_preferences:
                onPreferences();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onRefresh() {
        final NewsType currentNewsType = getCurrentNewsType();
        final RubricsSelector currentRubricsSelector = getRubricsSelector(currentNewsType);

        if (currentRubricsSelector != null) {
            serviceHelper.updateRubric(currentNewsType, currentRubricsSelector.getCurrentRubric(), new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure() {
                }
            });
        }
    }

    private void onSelectRubric() {
        selectRubricDialog.show();
    }

    private void onPreferences() {
        Intent preferences = new Intent(this, PreferencesActivity.class);
        startActivity(preferences);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        final Rubrics selectedRubric = selectRubricDialog.getSelectedRubric();

        if (selectedRubric != null) {
            selectRubric(selectedRubric);
        }
    }

    private void selectRubric(Rubrics rubric) {
        final NewsType currentNewsType = getCurrentNewsType();
        final RubricsSelector currentRubricsSelector = getRubricsSelector(currentNewsType);

        if (currentRubricsSelector != null && currentRubricsSelector.getCurrentRubric() != rubric) {
            currentRubricsSelector.setCurrentRubric(rubric);
            indicator.notifyDataSetChanged();

            if (autoRefresh) {
                onRefresh();
            }
        }
    }

    @Override
    public void onItemSelected(long id) {
        final NewsType currentNewsType = pagerAdapter.getNewsType(pager.getCurrentItem());

        switch (currentNewsType) {
            case NEWS:
                openNews(id);
                break;
            default:
                throw new AssertionError();
        }
    }

    private void openNews(long id) {
		Intent intent = new Intent(this, NewsFullActivity.class);
		intent.putExtra("newsId", id);

		startActivity(intent);
    }

    @Override
    public RubricsSelector getRubricsSelector(NewsType newsType) {
        return rubricsSelectors[newsType.ordinal()];
    }

    @Override
    public void setRubricsSelector(NewsType newsType, RubricsSelector rubricsSelector) {
        rubricsSelectors[newsType.ordinal()] = rubricsSelector;
    }

    public NewsType getCurrentNewsType() {
        return pagerAdapter.getNewsType(pager.getCurrentItem());
    }

    public RubricsSelector getCurrentRubricsSelector() {
        return getRubricsSelector(getCurrentNewsType());
    }
}
