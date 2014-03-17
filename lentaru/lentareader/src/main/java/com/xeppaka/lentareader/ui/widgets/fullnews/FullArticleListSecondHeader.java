package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;

import com.xeppaka.lentareader.data.Article;

/**
 * Created by nnm on 3/15/14.
 */
public class FullArticleListSecondHeader extends FullNewsListElementBase {
    public FullArticleListSecondHeader(Article article, Context context, Fragment fragment) {
        super(context, fragment);
    }

    @Override
    protected View createRootView(LayoutInflater inflater) {
        return null;
    }
}
