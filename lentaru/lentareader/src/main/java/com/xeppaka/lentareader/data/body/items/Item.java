package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListElement;
import com.xeppaka.lentareader.ui.widgets.fullnews.ListElementOptions;

/**
 * Created by kacpa01 on 11/4/13.
 */
public interface Item {
    String toXml();
    FullNewsListElement createFullNewsListElement(Context context, Fragment fragment);
}
