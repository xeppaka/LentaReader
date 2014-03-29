package com.xeppaka.lentareader.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.viewpagerindicator.TitlePageIndicator;
import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.service.ServiceHelper;
import com.xeppaka.lentareader.service.commands.exceptions.NoInternetConnectionException;
import com.xeppaka.lentareader.ui.adapters.listnews.SwipeNewsFragmentsAdapter;
import com.xeppaka.lentareader.ui.fragments.NewsListFragment;
import com.xeppaka.lentareader.ui.fragments.ListFragmentBase;
import com.xeppaka.lentareader.ui.widgets.SelectRubricDialog;
import com.xeppaka.lentareader.utils.LentaDebugUtils;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * This is the main activity where everything starts right after application is 
 * loaded. 
 * 
 * @author nnm
 * 
 */
public class NewsBriefActivity extends ActionBarActivity implements DialogInterface.OnDismissListener, NewsListFragment.ItemSelectionListener {
	private SwipeNewsFragmentsAdapter pagerAdapter;
	private ViewPager pager;
    private SelectRubricDialog selectRubricDialog;

    private ServiceHelper serviceHelper;
    private TitlePageIndicator indicator;

    // private NewsFullFragment newsFullFragment;

    private boolean refreshing;
    private int refreshCount;
    private boolean autoRefresh;
    private int listFragments;

    private boolean showServiceErrorToast = true;

    private MenuItem actionSelectRubric;
    private MenuItem actionPreferences;
    private MenuItem actionRefresh;
    private MenuItem actionRefreshRun;
    public static boolean visible;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LentaDebugUtils.strictMode();

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

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
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        if (actionBarHeight != 0) {
            WindowManager.LayoutParams params = selectRubricDialog.getWindow().getAttributes();

            params.gravity = Gravity.TOP/* | Gravity.RIGHT */;
            params.y = actionBarHeight;
        }

        final View fullNewsContainer = findViewById(R.id.full_news_fragment_container_large);
        if (fullNewsContainer != null) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            // fragmentManager.beginTransaction().replace(R.id.full_news_fragment_container_large, newsFullFragment = new NewsFullFragment()).commit();
        }

        serviceHelper = new ServiceHelper(this, new Handler());
	}

    @Override
    protected void onResume() {
        super.onResume();

        showActionBarRefresh(false);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        autoRefresh = preferences.getBoolean(PreferencesConstants.PREF_KEY_NEWS_AUTO_REFRESH, PreferencesConstants.NEWS_AUTO_REFRESH_DEFAULT);

        visible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        visible = false;
    }

    private void initializeViewPager() {
        pagerAdapter = new SwipeNewsFragmentsAdapter(getSupportFragmentManager(), this);

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

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                pagerAdapter.clearActiveFragments();
                final ListFragmentBase fragment = pagerAdapter.getFragment(position);

                if (fragment != null) {
                    fragment.setActive(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    final int position = pager.getCurrentItem();
                    final ListFragmentBase fragment = pagerAdapter.getFragment(position);
                    fragment.getDataObjectsAdapter().notifyDataSetChanged();
                }
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_list_menu_actions, menu);

        actionPreferences = menu.findItem(R.id.action_preferences);
        actionSelectRubric = menu.findItem(R.id.action_select_rubric);
        actionRefresh = menu.findItem(R.id.action_refresh);
        actionRefreshRun = menu.findItem(R.id.action_refresh_run);

        if (refreshing) {
            showActionBarRefresh(true);
        }

        return super.onCreateOptionsMenu(menu);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                showServiceErrorToast = true;
                onRefreshClick();
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

    private void onRefreshClick() {
        final NewsType currentNewsType = NewsType.values()[pager.getCurrentItem()];
        final ListFragmentBase fragment = pagerAdapter.getFragment(pager.getCurrentItem());
        final Rubrics rubric = fragment.getCurrentRubric();

        refresh(currentNewsType, rubric);
    }

    private void refresh(NewsType newsType, Rubrics rubric) {
        refreshing = true;
        showActionBarRefresh(true);

        ++refreshCount;

        serviceHelper.updateRubric(newsType, rubric, false, new AsyncListener<Void>() {
            @Override
            public void onSuccess(Void val) {
                refreshing = false;

                if (--refreshCount == 0) {
                    showActionBarRefresh(false);
                }
            }

            @Override
            public void onFailure(Exception ex) {
                refreshing = false;

                if (--refreshCount == 0) {
                    showActionBarRefresh(false);
                }

                if (showServiceErrorToast) {
                    showServiceErrorToast(ex);
                    showServiceErrorToast = false;
                }
            }
        });

    }

    private void showActionBarRefresh(boolean refreshing) {
        if (actionRefresh != null) {
            actionPreferences.setVisible(!refreshing);
            actionSelectRubric.setVisible(!refreshing);
            actionRefresh.setVisible(!refreshing);
            actionRefreshRun.setVisible(refreshing);
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
        final ListFragmentBase fragment = pagerAdapter.getFragment(pager.getCurrentItem());

        if (fragment != null && fragment.getCurrentRubric() != rubric) {
            fragment.setCurrentRubric(rubric);
            indicator.notifyDataSetChanged();

            if (autoRefresh) {
                onRefreshClick();
            }
        }
    }

    @Override
    public void onItemSelected(int position, long id) {
        final NewsType currentNewsType = NewsType.values()[pager.getCurrentItem()];
        final Rubrics rubric = pagerAdapter.getFragment(pager.getCurrentItem()).getCurrentRubric();

        switch (currentNewsType) {
            case NEWS:
                openNews(position, id, rubric);
                break;
            case ARTICLE:
                openArticle(position, id, rubric);
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof ListFragmentBase<?>) {
            if (++listFragments == NewsType.values().length && autoRefresh) {
                refresh(NewsType.ARTICLE, Rubrics.LATEST);
                refresh(NewsType.NEWS, Rubrics.LATEST);
            }
        }
    }

    private void openNews(int position, long id, Rubrics rubric) {

//        if (newsFullFragment == null) {
            final Intent intent = new Intent(this, NewsFullActivity.class);
            intent.putExtra("rubric", rubric.name());
            intent.putExtra("newsId", id);

            startActivity(intent);
//        } else {
//            final NewsListFragment fragment = pagerAdapter.getFragment(pager.getCurrentItem());
//            fragment.setSelection(position);
//
//            newsFullFragment.loadNews(id);
//        }
    }

    private void openArticle(int position, long id, Rubrics rubric) {
        final ListFragmentBase fragment = pagerAdapter.getFragment(pager.getCurrentItem());

//        if (newsFullFragment == null) {
            final Intent intent = new Intent(this, NewsFullActivity.class);
            intent.putExtra("rubric", rubric.name());
            intent.putExtra("articleId", id);

            startActivity(intent);
//        } else {
//            final NewsListFragment fragment = pagerAdapter.getFragment(pager.getCurrentItem());
//            fragment.setSelection(position);
//
//            newsFullFragment.loadNews(id);
//        }
    }

    private void showServiceErrorToast(Exception ex) {
        try {
            throw ex;
        } catch (NoInternetConnectionException e) {
            Toast.makeText(this, R.string.error_no_internet_connection_toast, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_general_toast, Toast.LENGTH_SHORT).show();
        }
    }
}
