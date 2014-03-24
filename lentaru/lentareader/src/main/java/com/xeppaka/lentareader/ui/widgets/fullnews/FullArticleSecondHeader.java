package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.body.items.SafeLinkMovementMethodDecorator;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.LentaTextUtils;

/**
 * Created by nnm on 3/15/14.
 */
public class FullArticleSecondHeader extends FullNewsElementBase {
    private String secondHeader;

    public FullArticleSecondHeader(String secondHeader, Context context, Fragment fragment) {
        super(context, fragment);

        this.secondHeader = secondHeader;
    }

    @Override
    protected View createRootView(LayoutInflater inflater) {
        final TextView textView = new TextView(inflater.getContext());
        final ElementOptions options = getOptions();

        textView.setTextSize(LentaTextUtils.getArticleSecondTitleTextSize(options.getTextSize()));
        textView.setTypeface(null, Typeface.ITALIC);
        textView.setText(Html.fromHtml(secondHeader));

        if (LentaConstants.SDK_VER >= 11) {
            textView.setTextIsSelectable(true);
        }
        textView.setMovementMethod(SafeLinkMovementMethodDecorator.getInstance(inflater.getContext()));

        final int px = getPadding();
        textView.setPadding(0, px, 0, px);

        return textView;
    }
}
