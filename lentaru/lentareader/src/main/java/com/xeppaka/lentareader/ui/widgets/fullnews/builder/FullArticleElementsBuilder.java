package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsElement;

import java.util.Collection;

/**
 * Created by nnm on 3/15/14.
 */
public class FullArticleElementsBuilder extends FullNewsElementsBuilder {
    private final Article article;

    public FullArticleElementsBuilder(Article article, Context context, Fragment fragment) {
        super(article, context, fragment);

        this.article = article;
    }

    @Override
    protected void buildHeader(Collection<FullNewsElement> appendTo) {

    }

    @Override
    protected void buildBody(Collection<FullNewsElement> appendTo) {

    }

    @Override
    protected void buildFooter(Collection<FullNewsElement> appendTo) {

    }
}
