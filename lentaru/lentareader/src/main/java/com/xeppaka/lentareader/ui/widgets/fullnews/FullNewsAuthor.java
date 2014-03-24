package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xeppaka.lentareader.data.body.items.SafeLinkMovementMethodDecorator;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.LentaTextUtils;

/**
 * Created by nnm on 3/23/14.
 */
public class FullNewsAuthor extends FullNewsElementBase {
    private String author;

    public FullNewsAuthor(String author, Context context, Fragment fragment) {
        super(context, fragment);

        this.author = author;
    }

    @Override
    protected View createRootView(LayoutInflater inflater) {
        final Context context = inflater.getContext();
        final TextView textView = new TextView(context);
        final ElementOptions options = getOptions();

        textView.setText(author);
        textView.setTextSize(LentaTextUtils.getArticleAuthorTextSize(options.getTextSize()));
        textView.setTypeface(null, Typeface.BOLD);
        textView.setGravity(Gravity.RIGHT);

        if (LentaConstants.SDK_VER >= 11) {
            textView.setTextIsSelectable(true);
        }

        final int px = getPadding();
        textView.setPadding(px, 0, px, 0);

        return textView;
    }
}
