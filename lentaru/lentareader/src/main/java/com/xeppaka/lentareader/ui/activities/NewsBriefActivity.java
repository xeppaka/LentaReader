package com.xeppaka.lentareader.ui.activities;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.viewpagerindicator.TitlePageIndicator;
import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.service.Callback;
import com.xeppaka.lentareader.service.ServiceHelper;
import com.xeppaka.lentareader.service.commands.exceptions.NoInternetConnectionException;
import com.xeppaka.lentareader.ui.fragments.NewsFullFragment;
import com.xeppaka.lentareader.ui.fragments.NewsObjectListFragment;
import com.xeppaka.lentareader.ui.fragments.SwipeNewsObjectsListAdapter;
import com.xeppaka.lentareader.ui.widgets.SelectRubricDialog;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.LentaDebugUtils;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * This is the main activity where everything starts right after application is 
 * loaded. 
 * 
 * @author nnm
 * 
 */
public class NewsBriefActivity extends ActionBarActivity implements DialogInterface.OnDismissListener, NewsObjectListFragment.ItemSelectionListener {
	private SwipeNewsObjectsListAdapter pagerAdapter;
	private ViewPager pager;
    private SelectRubricDialog selectRubricDialog;

    private ServiceHelper serviceHelper;
    private TitlePageIndicator indicator;

    private NewsFullFragment newsFullFragment;

    private boolean autoRefresh;
    private int listFragments;

    private boolean showNoInternetToast = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LentaDebugUtils.strictMode();

		setContentView(R.layout.brief_news_activity);
        setTitle(null);
        getSupportActionBar().setLogo(R.drawable.ab_lenta_icon);

        // getSupportActionBar().setTitle(String.valueOf(LentaConstants.SDK_VER));

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

            params.gravity = Gravity.TOP/* | Gravity.RIGHT */;
            params.y = actionBarHeight;
        }

        final View fullNewsContainer = findViewById(R.id.full_news_fragment_container_large);
        if (fullNewsContainer != null) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.full_news_fragment_container_large, newsFullFragment = new NewsFullFragment()).commit();
        }

        serviceHelper = new ServiceHelper(this, new Handler());
	}

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoRefresh = preferences.getBoolean(PreferencesConstants.PREF_KEY_NEWS_AUTO_REFRESH, PreferencesConstants.NEWS_AUTO_REFRESH_DEFAULT);
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
                showNoInternetToast = true;
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
        final NewsType currentNewsType = NewsType.values()[pager.getCurrentItem()];
        final Rubrics rubric = pagerAdapter.getFragment(pager.getCurrentItem()).getCurrentRubric();

        serviceHelper.updateRubric(currentNewsType, rubric, new Callback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onFailure(Exception ex) {
                if (showNoInternetToast) {
                    showServiceErrorToast(ex);
                    showNoInternetToast = false;
                }
            }
        });
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
        final NewsObjectListFragment fragment = pagerAdapter.getFragment(pager.getCurrentItem());

        if (fragment != null && fragment.getCurrentRubric() != rubric) {
            fragment.setCurrentRubric(rubric);
            indicator.notifyDataSetChanged();

            if (autoRefresh) {
                onRefresh();
            }
        }
    }

    @Override
    public void onItemSelected(int position, long id) {
        final NewsType currentNewsType = NewsType.values()[pager.getCurrentItem()];

        switch (currentNewsType) {
            case NEWS:
                openNews(position, id);
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof NewsObjectListFragment) {
            if (++listFragments == NewsType.values().length && autoRefresh) {
                onRefresh();
            }
        }
    }

    private void openNews(int position, long id) {
        if (newsFullFragment == null) {
            final Intent intent = new Intent(this, NewsFullActivity.class);
            intent.putExtra("newsId", id);

            startActivity(intent);
        } else {
            final NewsObjectListFragment fragment = pagerAdapter.getFragment(pager.getCurrentItem());
            fragment.setSelection(position);

            newsFullFragment.showNews(id);
        }
    }

    private void showServiceErrorToast(Exception ex) {
        final int duration = 90000;

        try {
            throw ex;
        } catch (NoInternetConnectionException e) {
            Toast.makeText(this, R.string.no_internet_connection_toast, duration).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.general_error_toast, duration).show();
        }
    }
}
