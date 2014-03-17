package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListElement;

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
    protected void buildHeader(Collection<FullNewsListElement> appendTo) {

    }

    @Override
    protected void buildBody(Collection<FullNewsListElement> appendTo) {

    }

    @Override
    protected void buildFooter(Collection<FullNewsListElement> appendTo) {

    }
}
