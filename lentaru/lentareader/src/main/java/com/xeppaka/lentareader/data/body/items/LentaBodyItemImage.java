package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by nnm on 11/18/13.
 */
public class LentaBodyItemImage implements Item {
    private String preview_url;
    private String original_url;
    private String caption;
    private String credits;

    public LentaBodyItemImage(String preview_url, String original_url, String caption, String credits) {
        this.preview_url = preview_url;
        this.original_url = original_url;
        this.caption = caption;
        this.credits = credits;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public String getOriginal_url() {
        return original_url;
    }

    public String getCaption() {
        return caption;
    }

    public String getCredits() {
        return credits;
    }

    @Override
    public String toXml() {
        StringBuilder sb = new StringBuilder("<image preview_url=\"");
        sb.append(preview_url).append("\" ").append("original_url=\"").append(original_url).append("\" ");

        if (caption != null) {
            sb.append("caption=\"").append(StringEscapeUtils.escapeXml(caption)).append("\" ");
        }

        if (credits != null) {
            sb.append("credits=\"").append(StringEscapeUtils.escapeXml(credits)).append("\" ");
        }

        return sb.append("/>").toString();
    }

    @Override
    public View createView(Context context) {
        final ImageView view = new ImageView(context);
        view.setImageBitmap(ImageDao.getNotAvailableImage().getBitmapIfCached());

        return view;
    }
}
