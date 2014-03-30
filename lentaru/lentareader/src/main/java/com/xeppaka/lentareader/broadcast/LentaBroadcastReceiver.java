package com.xeppaka.lentareader.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.scheduler.LentaBackgroundScheduler;
import com.xeppaka.lentareader.service.ServiceHelper;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by nnm on 3/9/14.
 */
public class LentaBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_BACKGROUND_UPDATE = "intent.action.BACKGROUND_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (ACTION_BACKGROUND_UPDATE.equals(action)) {
            final ServiceHelper serviceHelper = new ServiceHelper(context, new Handler());
            serviceHelper.updateRubric(NewsType.NEWS, Rubrics.LATEST, true, null);
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            startBackgroundUpdate(context);
        } else if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            startBackgroundUpdate(context);
        }
    }

    private void startBackgroundUpdate(Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean update = sharedPreferences.getBoolean(PreferencesConstants.PREF_KEY_NEWS_BACKGROUND_UPDATE, PreferencesConstants.NEWS_BACKGROUND_CHECK_DEFAULT);

        if (update) {
            final int interval = sharedPreferences.getInt(PreferencesConstants.PREF_KEY_NEWS_BACKGROUND_UPDATE_INTERVAL, PreferencesConstants.NEWS_BACKGROUND_CHECK_MINUTES_DEFAULT);
            LentaBackgroundScheduler.scheduleBackgroundCheck(context, interval);
        }
    }
}
