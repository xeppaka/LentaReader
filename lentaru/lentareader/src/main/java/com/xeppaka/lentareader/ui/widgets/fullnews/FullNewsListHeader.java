package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.ImageKeyCreator;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.NewsImageKeyCreator;

/**
 * Created by nnm on 3/15/14.
 */
public class FullNewsListHeader extends FullNewsListElementBase {
    private final String imageLink;
    private final String imageCaption;
    private final String imageCredits;
    private final String title;

    private ImageView imageView;
    private final ImageDao imageDao;

    public FullNewsListHeader(String imageLink, String imageCaption, String imageCredits, String title, Context context, Fragment fragment) {
        super(context, fragment);

        this.imageLink = imageLink;
        this.imageCaption = imageCaption;
        this.imageCredits = imageCredits;
        this.title = title;

        imageDao = ImageDao.getInstance();
    }

    public FullNewsListHeader(News news, Context context, Fragment fragment) {
        this(news.getImageLink(), news.getImageCaption(), news.getImageCredits(), news.getTitle(), context, fragment);
    }

    @Override
    public void becomeVisible() {
        final ListElementOptions options = getOptions();

        if (options != null && options.isDownloadImages()) {
            final BitmapReference bitmapReference = imageDao.read(imageLink, NewsImageKeyCreator.getInstance());

            bitmapReference.getDrawableAsync(imageView, new AsyncListener<Drawable>() {
                @Override
                public void onSuccess(Drawable value) {
                    if (getFragment().isResumed()) {
                        imageView.setImageDrawable(value);
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

    @Override
    public void becomeInvisible() {
        imageView.setImageDrawable(null);
    }

    @Override
    protected View createRootView(LayoutInflater inflater) {
        final View header = inflater.inflate(R.layout.full_news_header, null);

        imageView = (ImageView) header.findViewById(R.id.full_news_image);
        final TextView imageCaptionView = (TextView) header.findViewById(R.id.full_news_image_caption);
        final TextView imageCreditsView = (TextView) header.findViewById(R.id.full_news_image_credits);
        final TextView titleView = (TextView) header.findViewById(R.id.full_news_title);

        // TODO: set text sizes
        if (imageCaption != null && !imageCaption.isEmpty()) {
            imageCaptionView.setVisibility(View.VISIBLE);
            imageCaptionView.setText(Html.fromHtml(imageCaption));
        }

        if (imageCredits != null && !imageCredits.isEmpty()) {
            imageCreditsView.setVisibility(View.VISIBLE);
            imageCreditsView.setText(Html.fromHtml(imageCredits));
        }

        titleView.setText(title);

        return header;
    }
}
