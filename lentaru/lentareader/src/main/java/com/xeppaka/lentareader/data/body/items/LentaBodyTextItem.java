package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.xeppaka.lentareader.data.CDataWrapper;
import com.xeppaka.lentareader.utils.LentaTextUtils;

/**
 * Created by kacpa01 on 11/4/13.
 */
public class LentaBodyTextItem implements Item {
    private String text;

    public LentaBodyTextItem(String text) {
        this.text = text;
    }

    @Override
    public String toXml() {
        return "<text>" + CDataWrapper.wrapWithCData(text) + "</text>";
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public View createView(final Context context, ItemPreferences preferences) {
        TextView view = new TextView(context);

        view.setTextSize(LentaTextUtils.getNewsFullTextSize(preferences.getTextSize()));
        view.setText(Html.fromHtml(text));
        view.setTextIsSelectable(true);
        view.setMovementMethod(SafeLinkMovementMethodDecorator.getInstance(context));

        return view;
    }
}
