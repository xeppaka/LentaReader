package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xeppaka.lentareader.data.CDataWrapper;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsElement;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsTextElement;

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
    public FullNewsElement createFullNewsListElement(Context context, Fragment fragment) {
        return new FullNewsTextElement(text, context, fragment);
    }
}
