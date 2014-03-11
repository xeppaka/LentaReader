package com.xeppaka.lentareader.ui.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.ui.fragments.CommentsListFragment;
import com.xeppaka.lentareader.utils.LentaDebugUtils;

/**
 * Created by nnm on 3/1/14.
 */
public class CommentsActivity extends ActionBarActivity {
    private CommentsListFragment commentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LentaDebugUtils.strictMode();

        setTitle(null);
        //getActionBar().setIcon(R.drawable.lenta_icon);
        getSupportActionBar().setLogo(R.drawable.ab_lenta_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.comments_activity);

        final TextView nowReadCountView = (TextView) findViewById(R.id.comment_now_read_count);

        final String xid = getIntent().getStringExtra("xid");

        if (xid != null) {
            commentsFragment = new CommentsListFragment(xid, nowReadCountView);
            getSupportFragmentManager().beginTransaction().replace(R.id.comments_fragment_container, commentsFragment).commit();
        }
    }
}
