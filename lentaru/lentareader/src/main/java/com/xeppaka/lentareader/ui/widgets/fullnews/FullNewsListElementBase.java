package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by nnm on 3/15/14.
 */
public abstract class FullNewsListElementBase implements FullNewsListElement {
    private Fragment fragment;
    private LayoutInflater inflater;
    private View rootView;

    private ListElementOptions options;

    public FullNewsListElementBase(Context context, Fragment fragment) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fragment = fragment;
    }

    @Override
    public View getView() {
        if (rootView == null) {
            rootView = createRootView(inflater);
            // don't need inflater anymore
            inflater = null;
        }

        return rootView;
    }

    @Override
    public void becomeVisible() {}

    @Override
    public void becomeInvisible() {}

    public ListElementOptions getOptions() {
        return options;
    }

    public void setOptions(ListElementOptions options) {
        this.options = options;
    }

    protected abstract View createRootView(LayoutInflater inflater);

    public Fragment getFragment() {
        return fragment;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }
}
