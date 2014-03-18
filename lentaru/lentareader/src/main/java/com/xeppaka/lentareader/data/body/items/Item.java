package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsElement;

/**
 * Created by kacpa01 on 11/4/13.
 */
public interface Item {
    String toXml();
    FullNewsElement createFullNewsListElement(Context context, Fragment fragment);
}
