package com.xeppaka.lentareader.data.body.items;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsElement;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsImageGallery;

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
    public FullNewsElement createFullNewsListElement(Context context, Fragment fragment) {
        return new FullNewsImageGallery(this, context, fragment);
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
