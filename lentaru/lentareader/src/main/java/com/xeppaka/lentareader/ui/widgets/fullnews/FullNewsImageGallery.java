package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.xeppaka.lentareader.data.body.items.LentaBodyItemImageGallery;
import com.xeppaka.lentareader.ui.widgets.ImagesGallery;

/**
 * Created by kacpa01 on 3/18/14.
 */
public class FullNewsImageGallery extends FullNewsElementBase {
    private LentaBodyItemImageGallery gallery;
    private ImagesGallery imagesGallery;

    public FullNewsImageGallery(LentaBodyItemImageGallery gallery, Context context, Fragment fragment) {
        super(context, fragment);

        this.gallery = gallery;
    }

    @Override
    protected View createRootView(LayoutInflater inflater) {
        final ElementOptions options = getOptions();
        ImagesGallery imagesGallery = new ImagesGallery(inflater.getContext(), gallery, options.isDownloadImages(), options.getTextSize());

        final FrameLayout wrapper = new FrameLayout(inflater.getContext());
        final AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapper.setLayoutParams(layoutParams);
        wrapper.addView(imagesGallery);

        return wrapper;
    }
}
