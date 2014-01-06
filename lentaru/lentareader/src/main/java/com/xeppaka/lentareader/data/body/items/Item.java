package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.view.View;

/**
 * Created by kacpa01 on 11/4/13.
 */
public interface Item {
    String toXml();
    View createView(Context context, ItemPreferences preferences);
}
