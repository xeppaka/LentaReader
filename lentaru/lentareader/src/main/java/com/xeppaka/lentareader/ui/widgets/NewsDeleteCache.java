package com.xeppaka.lentareader.ui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xeppaka.lentareader.R;

/**
 * Created by nnm on 1/23/14.
 */
public class NewsDeleteCache extends DialogPreference {
    public NewsDeleteCache(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        final FrameLayout layout = new FrameLayout(getContext());
        layout.setLayoutParams(new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        final TextView textView = new TextView(getContext());
        textView.setText(R.string.news_clear_cache_confirmation);
        textView.setGravity(Gravity.CENTER);

        final ViewGroup.MarginLayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, getContext().getResources().getDisplayMetrics());
        textView.setLayoutParams(params);

        layout.addView(textView);

        return layout;
    }
}
