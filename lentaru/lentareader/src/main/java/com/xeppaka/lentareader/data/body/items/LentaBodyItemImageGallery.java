package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nnm on 11/18/13.
 */
public class LentaBodyItemImageGallery implements Item {
    private List<LentaBodyItemImage> images;

    public LentaBodyItemImageGallery(List<LentaBodyItemImage> images) {
        this.images = images;
    }

    public List<LentaBodyItemImage> getImages() {
        return images;
    }

    @Override
    public String toXml() {
        if (images.isEmpty()) {
            return "<images />";
        }

        StringBuilder sb = new StringBuilder("<images>");

        for (LentaBodyItemImage image : images) {
            sb.append(image.toXml());
        }

        return sb.append("</images>").toString();
    }

    @Override
    public View createView(Context context) {
        TextView view = new TextView(context);
        view.setText(Html.fromHtml("HERE IMAGE GALLERY"));
        view.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }
}
