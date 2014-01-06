package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

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
        TextView view = new TextView(context);
        view.setText(Html.fromHtml("VIDEO HERE from " + url));
        view.setMovementMethod(LinkMovementMethod.getInstance());

        return view;

    }
}
