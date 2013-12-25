package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.view.View;

import com.xeppaka.lentareader.ui.widgets.ImageGallery;

import java.util.Iterator;
import java.util.List;

/**
 * Created by nnm on 11/18/13.
 */
public class LentaBodyItemImageGallery implements Item, Iterable<LentaBodyItemImage> {
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
        return new ImageGallery(context, this);
    }

    @Override
    public Iterator<LentaBodyItemImage> iterator() {
        return images.iterator();
    }

    public int size() {
        return images.size();
    }

    public boolean isEmpty() {
        return images.isEmpty();
    }

    public LentaBodyItemImage getImage(int index) {
        return images.get(index);
    }
}
