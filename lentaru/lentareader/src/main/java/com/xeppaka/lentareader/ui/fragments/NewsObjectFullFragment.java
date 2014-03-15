package com.xeppaka.lentareader.ui.fragments;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.items.Item;
import com.xeppaka.lentareader.data.body.items.ItemPreferences;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.NewsImageKeyCreator;
import com.xeppaka.lentareader.utils.LentaTextUtils;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by nnm on 3/15/14.
 */
public abstract class NewsObjectFullFragment<T extends NewsObject> extends Fragment {
    private ScrollView scrollContainer;
    private ImageView imageView;
    private TextView imageCaption;
    private TextView imageCredits;
    private TextView titleView;
    private LinearLayout contentView;
    private TextView dateView;
    private TextView rubricViewTitle;
    private TextView rubricView;

    private long id = Dao.NO_ID;
    private T loadedNewsObject;

    private boolean downloadImages;
    private int textSize;

    private int scrollY;

    public NewsObjectFullFragment() {}

    public NewsObjectFullFragment(long id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            id = savedInstanceState.getLong("newsId");
        }

        return inflater.inflate(R.layout.full_news_fragment, null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("id", id);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollContainer = (ScrollView) view.findViewById(R.id.full_news_scroll_container);
        imageView = (ImageView) view.findViewById(R.id.full_news_image);
        imageCaption = (TextView) view.findViewById(R.id.full_news_image_caption);
        imageCredits = (TextView) view.findViewById(R.id.full_news_image_credits);
        titleView = (TextView) view.findViewById(R.id.full_news_title);
        dateView = (TextView) view.findViewById(R.id.full_news_date);
        rubricViewTitle = (TextView) view.findViewById(R.id.full_news_rubric_title);
        rubricView = (TextView) view.findViewById(R.id.full_news_rubric);
        contentView = (LinearLayout) view.findViewById(R.id.full_news_content);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        downloadImages = preferences.getBoolean(PreferencesConstants.PREF_KEY_DOWNLOAD_IMAGE_FULL, PreferencesConstants.DOWNLOAD_IMAGE_FULL_DEFAULT);
        textSize = preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_FULL_TEXT_SIZE, PreferencesConstants.NEWS_FULL_TEXT_SIZE_DEFAULT);

        if (id != Dao.NO_ID) {
            loadNews(id);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        scrollY = scrollContainer.getScrollY();
    }

    @Override
    public void onStop() {
        super.onStop();

        imageView.setImageBitmap(null);
        contentView.removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        loadedNewsObject = null;
    }

    public void loadNews(long id) {
        getDao().readAsync(id, new AsyncListener<T>() {
            @Override
            public void onSuccess(T result) {
                loadedNewsObject = result;

                if (result != null && isResumed()) {
                    if (isResumed()) {
                        renderAsync(loadedNewsObject);
                    }

                    if (!result.isRead()) {
                        result.setRead(true);

                        getDao().updateAsync(result, new AsyncListener<Integer>() {
                            @Override
                            public void onSuccess(Integer rowsUpdated) {}

                            @Override
                            public void onFailure(Exception e) {}
                        });
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    protected void renderAsync(final T news) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                renderNews(news);
            }
        });
    }

    protected void renderNews(final T newsObject) {
        titleView.setText(newsObject.getTitle());

        if (titleView.getTextSize() != LentaTextUtils.getNewsFullTitleTextSize(textSize)) {
            titleView.setTextSize(LentaTextUtils.getNewsFullTitleTextSize(textSize));
        }

        final int rubricTextSize = LentaTextUtils.getNewsFullRubricTextSize(textSize);

        if (rubricViewTitle.getTextSize() != rubricTextSize) {
            rubricViewTitle.setTextSize(rubricTextSize);
        }

        if (rubricView.getTextSize() != rubricTextSize) {
            rubricView.setTextSize(rubricTextSize);
        }

        if (dateView.getTextSize() != LentaTextUtils.getNewsFullDateTextSize(textSize)) {
            dateView.setTextSize(LentaTextUtils.getNewsFullDateTextSize(textSize));
        }

        Body body = newsObject.getBody();
        if (body != null) {
            final ItemPreferences itemPreferences = new ItemPreferences(downloadImages, textSize);

            for (Item item : body.getItems()) {
                final View itemView = item.createView(getActivity(), itemPreferences);

                if (itemView != null) {
                    contentView.addView(itemView);
                }
            }
        }

        dateView.setText(newsObject.getFormattedPubDate());
        rubricView.setText(" " + newsObject.getRubric().getLabel());

        if (newsObject.hasImage()) {
            final BitmapReference bitmapRef = ImageDao.getInstance(getActivity()).read(newsObject.getImageLink(), NewsImageKeyCreator.getInstance());

            if (downloadImages) {
                bitmapRef.getDrawableAsync(imageView, new AsyncListener<Drawable>() {
                    @Override
                    public void onSuccess(final Drawable bitmap) {
                        if (isResumed()) {
                            setNewsImage(newsObject, bitmap);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {}
                });
            } else {
                final Drawable drawable = bitmapRef.getDrawableIfCached(imageView);

                if (drawable != null) {
                    setNewsImage(newsObject, drawable);
                }
            }
        }

        scrollContainer.requestLayout();

//        new Handler().post(new Runnable() {
//            @Override
//            public void run() {
        scrollContainer.scrollTo(0, scrollY);
//            }
//        });
    }

    private void setNewsImage(T newsObject, Drawable drawable) {
        imageView.setImageDrawable(drawable);
        imageView.setVisibility(View.VISIBLE);

        if (newsObject.hasImageCaption()) {
            imageCaption.setText(Html.fromHtml(newsObject.getImageCaption()));
            imageCaption.setMovementMethod(LinkMovementMethod.getInstance());
            imageCaption.setVisibility(View.VISIBLE);

            if (imageCaption.getTextSize() != LentaTextUtils.getNewsFullImageCaptionTextSize(textSize)) {
                imageCaption.setTextSize(LentaTextUtils.getNewsFullImageCaptionTextSize(textSize));
            }
        }

        if (newsObject.hasImageCredits()) {
            imageCredits.setText(Html.fromHtml(newsObject.getImageCredits()));
            imageCredits.setMovementMethod(LinkMovementMethod.getInstance());
            imageCredits.setVisibility(View.VISIBLE);

            if (imageCredits.getTextSize() != LentaTextUtils.getNewsFullImageCreditsTextSize(textSize)) {
                imageCredits.setTextSize(LentaTextUtils.getNewsFullImageCreditsTextSize(textSize));
            }
        }
    }

    public String getLink() {
        if (loadedNewsObject != null)
            return loadedNewsObject.getLink();
        else
            return null;
    }

    public abstract AsyncDao<T> getDao();
}
