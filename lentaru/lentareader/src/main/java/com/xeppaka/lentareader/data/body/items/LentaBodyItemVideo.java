package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.view.View;

import com.xeppaka.lentareader.data.body.VideoType;

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
    public View createView(Context context, ItemPreferences preferences) {
        return null;
    }
}
