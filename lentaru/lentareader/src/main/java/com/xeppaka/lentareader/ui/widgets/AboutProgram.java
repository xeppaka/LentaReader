package com.xeppaka.lentareader.ui.widgets;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by nnm on 1/25/14.
 */
public class AboutProgram extends Preference {
    public AboutProgram(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        final Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
        final Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        try {
            getContext().startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
        }
    }
}
