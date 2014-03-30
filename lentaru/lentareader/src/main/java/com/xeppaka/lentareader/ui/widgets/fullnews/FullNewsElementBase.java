package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nnm on 3/15/14.
 */
public abstract class FullNewsElementBase implements FullNewsElement {
    private Fragment fragment;
    private LayoutInflater inflater;
    private View rootView;
    private boolean visible;
    private int padding;

    private ElementOptions options;

    public FullNewsElementBase(Context context, Fragment fragment) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fragment = fragment;

        padding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.getResources().getDisplayMetrics()));
    }

    @Override
    public View getView(ViewGroup parent) {
        if (rootView == null) {
            rootView = createRootView(inflater, parent);
            // don't need inflater anymore
            inflater = null;
        }

        return rootView;
    }

    @Override
    public void becomeVisible() {
        visible = true;
    }

    @Override
    public void becomeInvisible() {
        visible = false;
    }

    public ElementOptions getOptions() {
        return options;
    }

    public void setOptions(ElementOptions options) {
        this.options = options;
    }

    protected abstract View createRootView(LayoutInflater inflater, ViewGroup parent);

    public Fragment getFragment() {
        return fragment;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getPadding() {
        return padding;
    }
}
