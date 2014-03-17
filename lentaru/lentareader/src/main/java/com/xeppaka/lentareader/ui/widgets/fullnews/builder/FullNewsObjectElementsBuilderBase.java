package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by nnm on 3/15/14.
 */
public abstract class FullNewsObjectElementsBuilderBase<T extends NewsObject> implements FullNewsObjectElementsBuilder<T> {
    private List<FullNewsListElement> appendTo;
    private Context context;
    private Fragment fragment;

    protected FullNewsObjectElementsBuilderBase(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    public void toCollection(List<FullNewsListElement> appendTo) {
        this.appendTo = appendTo;
    }

    public List<FullNewsListElement> getAppendTo() {
        return appendTo;
    }

    protected abstract void buildHeader(Collection<FullNewsListElement> appendTo);
    protected abstract void buildBody(Collection<FullNewsListElement> appendTo);
    protected abstract void buildFooter(Collection<FullNewsListElement> appendTo);

    @Override
    public List<FullNewsListElement> build() {
        final List<FullNewsListElement> result = getAppendTo() == null ? new ArrayList<FullNewsListElement>() : getAppendTo();

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
