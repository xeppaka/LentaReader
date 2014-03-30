package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xeppaka.lentareader.data.body.items.LentaBodyItemImage;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemImageGallery;
import com.xeppaka.lentareader.data.body.items.SafeLinkMovementMethodDecorator;
import com.xeppaka.lentareader.ui.activities.ImagesFullActivity;
import com.xeppaka.lentareader.utils.LentaTextUtils;

import java.util.ArrayList;

/**
 * Created by nnm on 12/29/13.
 */
public class ImagesGallery extends LinearLayout implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private final TextView imageCaption;
    private final TextView imageCredits;
    private final TextView imageNumber;
    private ImagesSwitcher galleryView;

    private final LentaBodyItemImageGallery gallery;
    private final int textPadding;

    public ImagesGallery(Context context, LentaBodyItemImageGallery gallery, boolean downloadImages, int textSize, int textPadding) {
        super(context);

        this.gallery = gallery;
        this.textPadding = textPadding;

        setOrientation(VERTICAL);

        final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.getResources().getDisplayMetrics()));
        params.setMargins(0, 0, 0, px);

        setLayoutParams(params);

        imageNumber = createDescriptionTextView(context, LentaTextUtils.getNewsFullImageCaptionTextSize(textSize));
        imageNumber.setGravity(Gravity.RIGHT);
        addView(imageNumber);

        if (downloadImages) {
            galleryView = new ImagesSwitcher(context, gallery.getImages(), this, true);
            galleryView.setOnPageChangeListener(this);
            galleryView.setOnClickListener(this);

            addView(galleryView);
        }

        addView(imageCaption = createDescriptionTextView(context, LentaTextUtils.getNewsFullImageCaptionTextSize(textSize)));
        addView(imageCredits = createDescriptionTextView(context, LentaTextUtils.getNewsFullImageCreditsTextSize(textSize)));

        if (!gallery.isEmpty()) {
            setImageDescription(gallery.getImage(0));
            setImageNumber(1, gallery.size());
        }
    }

    private TextView createDescriptionTextView(Context context, int textSize) {
        final TextView textView = new TextView(context);
        textView.setTextSize(textSize);
        textView.setMovementMethod(SafeLinkMovementMethodDecorator.getInstance(context));

        return textView;
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {}

    @Override
    public void onPageSelected(int i) {
        setImageDescription(gallery.getImage(i));
        setImageNumber(i + 1, gallery.size());
    }

    private void setImageDescription(LentaBodyItemImage image) {
        if (image.hasCaption()) {
            imageCaption.setText(Html.fromHtml(image.getCaption()));
            imageCaption.setVisibility(VISIBLE);
            imageCaption.setPadding(textPadding, 0, textPadding, 0);
        } else {
            imageCaption.setVisibility(GONE);
        }

        if (image.hasCredits()) {
            imageCredits.setText(Html.fromHtml(image.getCredits()));
            imageCredits.setVisibility(VISIBLE);
            imageCredits.setPadding(textPadding, 0, textPadding, 0);
        } else {
            imageCredits.setVisibility(GONE);
        }
    }

    private void setImageNumber(int current, int all) {
        imageNumber.setText(String.format("%d/%d  ← →", current, all));
    }

    @Override
    public void onPageScrollStateChanged(int i) {}

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent(getContext(), ImagesFullActivity.class);
        intent.putExtra(ImagesFullActivity.IMAGES_KEY, new ArrayList<LentaBodyItemImage>(gallery.getImages()));
        intent.putExtra(ImagesFullActivity.INDEX_KEY, galleryView.getCurrentItem());

        getContext().startActivity(intent);
    }

    public void becomeVisible() {
        if (galleryView != null) {
            galleryView.becomeVisible();
        }
    }

    public void becomeInvisible() {
        if (galleryView != null) {
            galleryView.becomeInvisible();
        }
    }
}
