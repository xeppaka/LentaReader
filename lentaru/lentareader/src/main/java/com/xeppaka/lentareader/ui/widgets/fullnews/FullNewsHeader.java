package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.body.items.SafeLinkMovementMethodDecorator;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.NewsImageKeyCreator;
import com.xeppaka.lentareader.utils.LentaTextUtils;

/**
 * Created by nnm on 3/15/14.
 */
public class FullNewsHeader extends FullNewsElementBase {
    private final String imageLink;
    private final String imageCaption;
    private final String imageCredits;
    private final String title;

    private final boolean hasImage;
    private final boolean hasImageCaption;
    private final boolean hasImageCredits;

    private ImageView imageView;
    private final ImageDao imageDao;

    public FullNewsHeader(boolean hasImage, String imageLink, boolean hasImageCaption, String imageCaption,
                          boolean hasImageCredits, String imageCredits, String title, Context context, Fragment fragment) {
        super(context, fragment);

        this.hasImage = hasImage;
        this.imageLink = imageLink;
        this.hasImageCaption = hasImageCaption;
        this.imageCaption = imageCaption;
        this.hasImageCredits = hasImageCredits;
        this.imageCredits = imageCredits;
        this.title = title;

        imageDao = ImageDao.getInstance();
    }

    public FullNewsHeader(News news, Context context, Fragment fragment) {
        this(news.hasImage(), news.getImageLink(), news.hasImageCaption(), news.getImageCaption(),
             news.hasImageCredits(), news.getImageCredits(), news.getTitle(), context, fragment);
    }

    @Override
    public void becomeVisible() {
        final boolean visible = isVisible();

        if (!visible && hasImage) {
            final ElementOptions options = getOptions();
            final BitmapReference bitmapReference = imageDao.read(imageLink, NewsImageKeyCreator.getInstance());
            final Drawable drawable = bitmapReference.getDrawableIfCached(imageView);

            if (drawable != null) {
                imageView.setImageDrawable(drawable);
                imageView.setVisibility(View.VISIBLE);
            } else if (options != null && options.isDownloadImages()) {
                bitmapReference.getDrawableAsync(imageView, new AsyncListener<Drawable>() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        if (getFragment().isResumed()) {
                            imageView.setImageDrawable(drawable);
                            imageView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (getFragment().isResumed()) {
                            imageView.setImageDrawable(null);
                            imageView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }

        super.becomeVisible();
    }

    @Override
    public void becomeInvisible() {
        final boolean visible = isVisible();

        if (visible) {
            imageView.setImageDrawable(null);
            imageView.setVisibility(View.GONE);
        }

        super.becomeInvisible();
    }

    @Override
    protected View createRootView(LayoutInflater inflater, ViewGroup parent) {
        final View header = inflater.inflate(R.layout.full_news_header, parent);
        final ElementOptions options = getOptions();

        imageView = (ImageView) header.findViewById(R.id.full_news_image);
        final TextView imageCaptionView = (TextView) header.findViewById(R.id.full_news_image_caption);
        final TextView imageCreditsView = (TextView) header.findViewById(R.id.full_news_image_credits);
        final TextView titleView = (TextView) header.findViewById(R.id.full_news_title);

        final int px = getPadding();

        if (options.isDownloadImages() && hasImageCaption) {
            imageCaptionView.setVisibility(View.VISIBLE);
            imageCaptionView.setText(Html.fromHtml(imageCaption));
            imageCaptionView.setMovementMethod(SafeLinkMovementMethodDecorator.getInstance(inflater.getContext()));
            imageCaptionView.setTextSize(LentaTextUtils.getNewsFullImageCaptionTextSize(options.getTextSize()));
            imageCaptionView.setPadding(px, 0, px, 0);
        } else {
            imageCaptionView.setVisibility(View.GONE);
        }

        if (options.isDownloadImages() && hasImageCredits) {
            imageCreditsView.setVisibility(View.VISIBLE);
            imageCreditsView.setText(Html.fromHtml(imageCredits));
            imageCreditsView.setMovementMethod(SafeLinkMovementMethodDecorator.getInstance(inflater.getContext()));
            imageCreditsView.setTextSize(LentaTextUtils.getNewsFullImageCreditsTextSize(options.getTextSize()));
            imageCreditsView.setPadding(px, 0, px, 0);
        } else {
            imageCreditsView.setVisibility(View.GONE);
        }

        titleView.setText(title);
        titleView.setTextSize(LentaTextUtils.getNewsFullTitleTextSize(options.getTextSize()));

        return header;
    }
}
