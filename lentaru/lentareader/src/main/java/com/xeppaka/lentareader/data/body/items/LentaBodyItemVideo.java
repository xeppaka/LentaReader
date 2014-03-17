package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xeppaka.lentareader.data.body.VideoType;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListElement;
import com.xeppaka.lentareader.ui.widgets.fullnews.ListElementOptions;

/**
 * Created by nnm on 11/18/13.
 */
public class LentaBodyItemVideo implements Item {
    private String url;
    private VideoType type;

    public LentaBodyItemVideo(String url, VideoType type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public VideoType getType() {
        return type;
    }

    @Override
    public String toXml() {
        return "<video url=\"" + url + "\" type=\"" + type + "\" />";
    }

    @Override
    public FullNewsListElement createFullNewsListElement(Context context, Fragment fragment) {
        return null;
    }
}
