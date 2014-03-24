package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
        imagesGallery = new ImagesGallery(inflater.getContext(), gallery, options.isDownloadImages(), options.getTextSize(), getPadding());

        final LinearLayout wrapper = new LinearLayout(inflater.getContext());
        final AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapper.setLayoutParams(layoutParams);
        wrapper.addView(imagesGallery);

        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, inflater.getContext().getResources().getDisplayMetrics());
        wrapper.setPadding(0, 0, 0, value);

        return wrapper;
    }

    @Override
    public void becomeVisible() {
        final boolean visible = isVisible();

        if (!visible && imagesGallery != null) {
            imagesGallery.becomeVisible();
        }

        super.becomeVisible();
    }

    @Override
    public void becomeInvisible() {
        super.becomeInvisible();

        if (imagesGallery != null) {
            imagesGallery.becomeInvisible();
        }
    }
}
