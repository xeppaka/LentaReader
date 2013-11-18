package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.xeppaka.lentareader.data.CDataWrapper;

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
    public View createView(Context context) {
        TextView view = new TextView(context);
        view.setText(Html.fromHtml(text));
        view.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}
