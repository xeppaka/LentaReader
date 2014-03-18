package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by nnm on 3/15/14.
 */
public abstract class FullNewsObjectElementsBuilderBase<T extends NewsObject> implements FullNewsObjectElementsBuilder<T> {
    private List<FullNewsElement> appendTo;
    private Context context;
    private Fragment fragment;

    protected FullNewsObjectElementsBuilderBase(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    public void toCollection(List<FullNewsElement> appendTo) {
        this.appendTo = appendTo;
    }

    public List<FullNewsElement> getAppendTo() {
        return appendTo;
    }

    protected abstract void buildHeader(Collection<FullNewsElement> appendTo);
    protected abstract void buildBody(Collection<FullNewsElement> appendTo);
    protected abstract void buildFooter(Collection<FullNewsElement> appendTo);

    @Override
    public List<FullNewsElement> build() {
        final List<FullNewsElement> result = getAppendTo() == null ? new ArrayList<FullNewsElement>() : getAppendTo();

        buildHeader(result);
        buildBody(result);
        buildFooter(result);

        return result;
    }

    public Context getContext() {
        return context;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
