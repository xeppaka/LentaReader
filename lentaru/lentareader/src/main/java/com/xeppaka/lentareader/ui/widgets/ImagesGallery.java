package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xeppaka.lentareader.data.body.items.LentaBodyItemImage;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemImageGallery;
import com.xeppaka.lentareader.utils.LentaTextUtils;

/**
 * Created by nnm on 12/29/13.
 */
public class ImagesGallery extends LinearLayout implements ViewPager.OnPageChangeListener {
    private final TextView imageCaption;
    private final TextView imageCredits;

    private final LentaBodyItemImageGallery gallery;

    public ImagesGallery(Context context, LentaBodyItemImageGallery gallery, boolean downloadImages, int textSize) {
        super(context);

        this.gallery = gallery;

        setOrientation(VERTICAL);

        final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.getResources().getDisplayMetrics()));
        params.setMargins(0, 0, 0, px);

        setLayoutParams(params);

        if (downloadImages) {
            final ImagesSwitcher galleryView = new ImagesSwitcher(context, gallery.getImages());
            galleryView.setOnPageChangeListener(this);

            addView(galleryView);
        }

        addView(imageCaption = createDescriptionTextView(context, LentaTextUtils.getNewsFullImageCaptionTextSize(textSize)));
        addView(imageCredits = createDescriptionTextView(context, LentaTextUtils.getNewsFullImageCreditsTextSize(textSize)));

        if (!gallery.isEmpty())
            setImageDescription(gallery.getImage(0));
    }

    private TextView createDescriptionTextView(Context context, int textSize) {
        final TextView textView = new TextView(context);
        textView.setTextSize(textSize);

        return textView;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageSelected(int i) {
        setImageDescription(gallery.getImage(i));
    }

    private void setImageDescription(LentaBodyItemImage image) {
        if (image.hasCaption()) {
            imageCaption.setText(Html.fromHtml(image.getCaption()));
            imageCaption.setVisibility(VISIBLE);
        } else {
            imageCaption.setVisibility(GONE);
        }

        if (image.hasCredits()) {
            imageCredits.setText(Html.fromHtml(image.getCredits()));
            imageCredits.setVisibility(VISIBLE);
        } else {
            imageCredits.setVisibility(GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {}
}
