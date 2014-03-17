package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.xeppaka.lentareader.data.CDataWrapper;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListElement;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsTextElement;
import com.xeppaka.lentareader.ui.widgets.fullnews.ListElementOptions;
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
    public FullNewsListElement createFullNewsListElement(Context context, Fragment fragment) {
        return new FullNewsTextElement(text, context, fragment);
    }
}
