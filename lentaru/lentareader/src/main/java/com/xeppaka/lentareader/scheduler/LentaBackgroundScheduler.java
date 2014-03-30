package com.xeppaka.lentareader.scheduler;

import android.content.Context;

/**
 * Created by nnm on 3/10/14.
 */
public class LentaBackgroundScheduler {
    public static void scheduleBackgroundCheck(Context context, int intervalMinutes) {
//        final Intent in = new Intent();
//        in.setAction(LentaBroadcastReceiver.ACTION_BACKGROUND_UPDATE);
//
//        final PendingIntent pi = PendingIntent.getBroadcast(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000, 30000, pi);
    }

    public static void cancelBackgroundCheck(Context context) {
//        final Intent in = new Intent();
//        in.setAction(LentaBroadcastReceiver.ACTION_BACKGROUND_UPDATE);
//
//        final PendingIntent pi = PendingIntent.getBroadcast(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarmManager.cancel(pi);
    }
}
