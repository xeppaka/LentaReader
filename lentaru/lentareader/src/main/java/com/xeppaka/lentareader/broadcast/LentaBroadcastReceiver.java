package com.xeppaka.lentareader.broadcast;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.service.ServiceHelper;

/**
 * Created by nnm on 3/9/14.
 */
public class LentaBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final ServiceHelper serviceHelper = new ServiceHelper(context, new Handler());
        serviceHelper.updateRubric(NewsType.NEWS, Rubrics.LATEST, true, null);
    }
}
