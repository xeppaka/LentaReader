package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xeppaka.lentareader.data.body.items.SafeLinkMovementMethodDecorator;
import com.xeppaka.lentareader.utils.LentaTextUtils;

/**
 * Created by nnm on 3/15/14.
 */
public class FullNewsTextElement extends FullNewsListElementBase {
    private String text;

    public FullNewsTextElement(String text, Context context, Fragment fragment) {
        super(context, fragment);

        this.text = text;
    }

    @Override
    protected View createRootView(LayoutInflater inflater) {
        final TextView textView = new TextView(inflater.getContext());
        textView.setText(text);

        textView.setTextSize(LentaTextUtils.getNewsFullTextSize(getTextSize()));
        textView.setText(Html.fromHtml(text));
        textView.setTextIsSelectable(true);
        textView.setMovementMethod(SafeLinkMovementMethodDecorator.getInstance(inflater.getContext()));

        return textView;
    }
}
