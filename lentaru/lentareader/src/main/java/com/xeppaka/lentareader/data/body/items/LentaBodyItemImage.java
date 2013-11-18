package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.xeppaka.lentareader.data.dao.objects.ImageDao;

/**
 * Created by nnm on 11/18/13.
 */
public class LentaBodyItemImage implements Item {
    private String url;

    public LentaBodyItemImage(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toXml() {
        return "<image>" + url + "</image>";
    }

    @Override
    public View createView(Context context) {
        ImageView view = new ImageView(context);
        view.setImageBitmap(ImageDao.getNotAvailableImage().getBitmap());

        return view;
    }
}
