package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.items.Item;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsElement;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsFooter;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsHeader;

import java.util.Collection;

/**
 * Created by nnm on 3/15/14.
 */
public class FullNewsElementsBuilder extends FullNewsObjectElementsBuilderBase<News> {
    private final News news;

    public FullNewsElementsBuilder(News news, Context context, Fragment fragment) {
        super(context, fragment);

        this.news = news;
    }

    @Override
    protected void buildHeader(Collection<FullNewsElement> appendTo) {
        final FullNewsHeader header = new FullNewsHeader(news, getContext(), getFragment());
        header.setOptions(getOptions());

        appendTo.add(header);
    }

    @Override
    protected void buildBody(Collection<FullNewsElement> appendTo) {
        final Body body = news.getBody();

        for (Item item : body.getItems()) {
            final FullNewsElement element = item.createFullNewsListElement(getContext(), getFragment());

            if (element != null) {
                element.setOptions(getOptions());
                appendTo.add(element);
            }
        }
    }

    @Override
    protected void buildFooter(Collection<FullNewsElement> appendTo) {
        final FullNewsFooter footer = new FullNewsFooter(news, getContext(), getFragment());
        footer.setOptions(getOptions());

        appendTo.add(footer);
    }
}
