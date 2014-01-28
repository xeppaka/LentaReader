package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.xeppaka.lentareader.R;

/**
 * Created by nnm on 1/26/14.
 */
public class SafeLinkMovementMethodDecorator implements MovementMethod {
    private static SafeLinkMovementMethodDecorator INSTANCE;
    private static Context context;

    private MovementMethod decorated;

    public static MovementMethod getInstance(Context context) {
        if (INSTANCE == null || SafeLinkMovementMethodDecorator.context != context) {
            INSTANCE = new SafeLinkMovementMethodDecorator(LinkMovementMethod.getInstance());
            SafeLinkMovementMethodDecorator.context = context;
        }

        return INSTANCE;
    }

    public SafeLinkMovementMethodDecorator(MovementMethod decorated) {
        this.decorated = decorated;
    }

    @Override
    public boolean canSelectArbitrarily() {
        return decorated.canSelectArbitrarily();
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        decorated.initialize(widget, text);
    }

    @Override
    public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return decorated.onKeyDown(widget, text, keyCode, event);
    }

    @Override
    public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return decorated.onKeyUp(widget, text, keyCode, event);
    }

    @Override
    public boolean onKeyOther(TextView view, Spannable text, KeyEvent event) {
        return decorated.onKeyOther(view, text, event);
    }

    @Override
    public void onTakeFocus(TextView widget, Spannable text, int direction) {
        decorated.onTakeFocus(widget, text, direction);
    }

    @Override
    public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
        return decorated.onTrackballEvent(widget, text, event);
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable text, MotionEvent event) {
        try {
            return decorated.onTouchEvent(widget, text, event);
        } catch (Exception e) {
            Toast.makeText(context, R.string.error_open_link_toast, Toast.LENGTH_SHORT).show();

            return true;
        }
    }

    @Override
    public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
        return decorated.onGenericMotionEvent(widget, text, event);
    }
}
