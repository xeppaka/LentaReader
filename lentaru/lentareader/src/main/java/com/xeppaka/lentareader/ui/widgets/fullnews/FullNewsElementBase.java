package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * Created by nnm on 3/15/14.
 */
public abstract class FullNewsElementBase implements FullNewsElement {
    private Fragment fragment;
    private LayoutInflater inflater;
    private View rootView;
    private boolean visible;

    private ElementOptions options;
    // private final AbsListView.LayoutParams defaultLayoutParams;


    public FullNewsElementBase(Context context, Fragment fragment) {
        // this.defaultLayoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fragment = fragment;
    }

    @Override
    public View getView() {
        if (rootView == null) {
            rootView = createRootView(inflater);
            // rootView.setLayoutParams(defaultLayoutParams);
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

    protected abstract View createRootView(LayoutInflater inflater);

    public Fragment getFragment() {
        return fragment;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public boolean isVisible() {
        return visible;
    }
}
