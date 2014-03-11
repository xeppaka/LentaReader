package com.xeppaka.lentareader.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.scheduler.LentaBackgroundScheduler;
import com.xeppaka.lentareader.ui.fragments.LentaPreferencesFragment;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by kacpa01 on 12/30/13.
 */
public class PreferencesActivity extends ActionBarActivity {
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(null);
        //getActionBar().setIcon(R.drawable.lenta_icon);
        getSupportActionBar().setLogo(R.drawable.ab_lenta_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.preferences_activity);

        getFragmentManager().beginTransaction().replace(R.id.preferences_fragment_container, new LentaPreferencesFragment()).commit();

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (PreferencesConstants.PREF_KEY_NEWS_DELETE_NEWS.equals(key)) {
                    final boolean delete = sharedPreferences.getBoolean(key, PreferencesConstants.NEWS_DELETE_NEWS_DEFAULT);

                    if (!delete) {
                        AsyncNODao<News> nd = NewsDao.getInstance(getContentResolver());
                        nd.setUpdatedFromLatestFlagAsync(Rubrics.LATEST, new AsyncListener<Integer>() {
                            @Override
                            public void onSuccess(Integer rowsUpdated) {
                            }

                            @Override
                            public void onFailure(Exception e) {
                            }
                        });
                    }
                } else if (PreferencesConstants.PREF_KEY_NEWS_BACKGROUND_UPDATE.equals(key)) {
                    final boolean update = sharedPreferences.getBoolean(key, PreferencesConstants.NEWS_BACKGROUND_CHECK_DEFAULT);
                    final int interval = sharedPreferences.getInt(PreferencesConstants.PREF_KEY_NEWS_BACKGROUND_UPDATE_INTERVAL, PreferencesConstants.NEWS_BACKGROUND_CHECK_MINUTES_DEFAULT);

                    if (update) {
                        LentaBackgroundScheduler.scheduleBackgroundCheck(PreferencesActivity.this, interval);
                    } else {
                        LentaBackgroundScheduler.cancelBackgroundCheck(PreferencesActivity.this);
                    }
                } else if (PreferencesConstants.PREF_KEY_NEWS_BACKGROUND_UPDATE_INTERVAL.equals(key)) {
                    final int interval = sharedPreferences.getInt(PreferencesConstants.PREF_KEY_NEWS_BACKGROUND_UPDATE_INTERVAL, PreferencesConstants.NEWS_BACKGROUND_CHECK_MINUTES_DEFAULT);
                    LentaBackgroundScheduler.scheduleBackgroundCheck(PreferencesActivity.this, interval);
                }
            }
        };

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
