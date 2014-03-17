package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.items.Item;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListElement;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListFooter;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListHeader;
import com.xeppaka.lentareader.ui.widgets.fullnews.ListElementOptions;

import java.util.Collection;

/**
 * Created by nnm on 3/15/14.
 */
public class FullNewsElementsBuilder extends FullNewsObjectElementsBuilderBase<News> {
    private final News news;
    private ListElementOptions options;

    public FullNewsElementsBuilder(News news, Context context, Fragment fragment) {
        super(context, fragment);

        this.news = news;
    }

    public FullNewsElementsBuilder setOptions(ListElementOptions options) {
        this.options = options;

        return this;
    }

    @Override
    protected void buildHeader(Collection<FullNewsListElement> appendTo) {
        final FullNewsListHeader header = new FullNewsListHeader(news, getContext(), getFragment());
        header.setOptions(options);

        appendTo.add(header);
    }

    @Override
    protected void buildBody(Collection<FullNewsListElement> appendTo) {
        final Body body = news.getBody();

        for (Item item : body.getItems()) {
            final FullNewsListElement element = item.createFullNewsListElement(getContext(), getFragment());
            element.setOptions(options);

            appendTo.add(element);
        }
    }

    @Override
    protected void buildFooter(Collection<FullNewsListElement> appendTo) {
        final FullNewsListFooter footer = new FullNewsListFooter(news, getContext(), getFragment());
        footer.setOptions(options);

        appendTo.add(footer);
    }
}
