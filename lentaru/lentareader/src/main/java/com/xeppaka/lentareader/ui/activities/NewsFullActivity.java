package com.xeppaka.lentareader.ui.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.ArticleDao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.ui.adapters.fullnews.FullArticleFragmentPagerAdapter;
import com.xeppaka.lentareader.ui.adapters.fullnews.FullFragmentPagerAdapter;
import com.xeppaka.lentareader.ui.adapters.fullnews.FullNewsFragmentPagerAdapter;
import com.xeppaka.lentareader.ui.fragments.FullFragmentBase;
import com.xeppaka.lentareader.utils.LentaDebugUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsFullActivity extends ActionBarActivity {
	private FullFragmentPagerAdapter adapter;
    private ViewPager pager;

    private String idKey;
    private Rubrics rubric;

    private List<FullFragmentBase> rotatedFragments = new ArrayList<FullFragmentBase>(3);

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        LentaDebugUtils.strictMode();

        setTitle(null);
        //getActionBar().setIcon(R.drawable.lenta_icon);
        getSupportActionBar().setLogo(R.drawable.ab_lenta_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.full_news_activity);
        pager = (ViewPager)findViewById(R.id.full_news_view_pager);

        long id;

        if (savedInstanceState != null) {
            id = savedInstanceState.getLong("newsId", Dao.NO_ID);
            rubric = Rubrics.valueOf(savedInstanceState.getString("rubric", Rubrics.LATEST.name()));
        } else {
            id = getIntent().getLongExtra("newsId", Dao.NO_ID);

            final String rubricStr = getIntent().getStringExtra("rubric");
            rubric = rubricStr == null ? Rubrics.LATEST : Rubrics.valueOf(rubricStr);
        }

        if (id != Dao.NO_ID) {
            final long idAsync = id;
            idKey = "newsId";

            AsyncNODao<News> dao = NewsDao.getInstance(getContentResolver());
            dao.readAllIdsAsync(rubric, new AsyncListener<List<Long>>() {
                @Override
                public void onSuccess(List<Long> result) {
                    pager.setAdapter(adapter = new FullNewsFragmentPagerAdapter(getSupportFragmentManager(), result));

                    final int currentItem = result.indexOf(idAsync);

                    if (currentItem >= 0) {
                        pager.setCurrentItem(currentItem);
                    }
                }

                @Override
                public void onFailure(Exception e) {}
            });

            dao.markReadAsync(id, new AsyncListener<Integer>() {
                @Override
                public void onSuccess(Integer value) {}

                @Override
                public void onFailure(Exception e) {}
            });
		} else {
            id = savedInstanceState == null ? getIntent().getLongExtra("articleId", Dao.NO_ID) : savedInstanceState.getLong("articleId", Dao.NO_ID);

            if (id != Dao.NO_ID) {
                final long idAsync = id;
                idKey = "articleId";

                adapter = new FullArticleFragmentPagerAdapter(getSupportFragmentManager(), Collections.<Long>emptyList());

                AsyncNODao<Article> dao = ArticleDao.getInstance(getContentResolver());
                dao.readAllIdsAsync(rubric, new AsyncListener<List<Long>>() {
                    @Override
                    public void onSuccess(List<Long> result) {
                        adapter.setIds(result);
                        pager.setAdapter(adapter);

                        final int currentItem = result.indexOf(idAsync);

                        if (currentItem >= 0) {
                            pager.setCurrentItem(currentItem);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });

                dao.markReadAsync(id, new AsyncListener<Integer>() {
                    @Override
                    public void onSuccess(Integer value) {}

                    @Override
                    public void onFailure(Exception e) {}
                });
            }
        }

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                FullFragmentBase fragment = adapter.getItem(position);

                if (fragment != null) {
                    fragment.markRead();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_full_menu_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FullFragmentBase currentFragment = adapter.getItem(pager.getCurrentItem());

        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.action_copy_link:
                currentFragment.copyLinkToBuffer();
                break;
            case R.id.action_open_in_browser:
                currentFragment.openLinkInBrowser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FullFragmentBase currentFragment = adapter.getItem(pager.getCurrentItem());

        if (currentFragment != null) {
            outState.putLong(idKey, currentFragment.getDbId());
            outState.putString("rubric", rubric.name());
        }
    }
}
