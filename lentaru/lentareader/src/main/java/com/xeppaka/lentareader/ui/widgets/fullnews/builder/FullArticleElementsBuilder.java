package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullArticleSecondHeader;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsAuthor;
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
        super.buildHeader(appendTo);

        final String secondTitle = article.getSecondTitle();

        if (secondTitle != null && !secondTitle.isEmpty()) {
            final FullArticleSecondHeader secondHeader = new FullArticleSecondHeader(secondTitle, getContext(), getFragment());
            secondHeader.setOptions(getOptions());

            appendTo.add(secondHeader);
        }
    }

    @Override
    protected void buildBody(Collection<FullNewsElement> appendTo) {
        super.buildBody(appendTo);
    }

    @Override
    protected void buildFooter(Collection<FullNewsElement> appendTo) {
        final String author = article.getAuthor();

        if (author != null && !author.isEmpty()) {
            final FullNewsAuthor authorElement = new FullNewsAuthor(author, getContext(), getFragment());
            authorElement.setOptions(getOptions());

            appendTo.add(authorElement);
        }

        super.buildFooter(appendTo);
    }
}
