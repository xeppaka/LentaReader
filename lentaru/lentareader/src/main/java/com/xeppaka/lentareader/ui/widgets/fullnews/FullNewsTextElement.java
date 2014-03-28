package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xeppaka.lentareader.data.body.items.SafeLinkMovementMethodDecorator;
import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.LentaTextUtils;

/**
 * Created by nnm on 3/15/14.
 */
public class FullNewsTextElement extends FullNewsElementBase {
    private String text;

    public FullNewsTextElement(String text, Context context, Fragment fragment) {
        super(context, fragment);

        this.text = text;
    }

    @Override
    protected View createRootView(LayoutInflater inflater, ViewGroup parent) {
        final Context context = inflater.getContext();
        final TextView textView = new TextView(context);
        final ElementOptions options = getOptions();

        textView.setTextSize(LentaTextUtils.getNewsFullTextSize(options.getTextSize()));
        textView.setText(Html.fromHtml(text));

        if (LentaConstants.SDK_VER >= 11) {
            textView.setTextIsSelectable(true);
        }

        textView.setMovementMethod(SafeLinkMovementMethodDecorator.getInstance(context));

        final int px = getPadding();
        textView.setPadding(px, 0, px, 0);

        return textView;
    }
}
