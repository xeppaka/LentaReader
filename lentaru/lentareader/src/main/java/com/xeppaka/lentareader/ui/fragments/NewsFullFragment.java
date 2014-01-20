package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.items.Item;
import com.xeppaka.lentareader.data.body.items.ItemPreferences;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.utils.LentaTextUtils;
import com.xeppaka.lentareader.utils.PreferencesConstants;

public class NewsFullFragment extends Fragment {
    public static final long NO_NEWS_ID = -1;

	private ImageView imageView;
    private TextView imageCaption;
    private TextView imageCredits;
    private TextView titleView;
    private LinearLayout contentView;
    private TextView dateView;
    private TextView rubricViewTitle;
    private TextView rubricView;

	private long newsId = NO_NEWS_ID;
	private News loadedNews;

    private AsyncDao<News> newsDao;

    private boolean downloadImages;
    private int textSize;

    public NewsFullFragment() {}

    public NewsFullFragment(long newsId) {
        this.newsId = newsId;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("newsId", newsId);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            newsId = savedInstanceState.getLong("newsId");
        }

        newsDao = NewsDao.getInstance(getActivity().getContentResolver());

        return inflater.inflate(R.layout.full_news_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

        final Activity activity = getActivity();

        imageView = (ImageView) activity.findViewById(R.id.full_news_image);
        imageCaption = (TextView) activity.findViewById(R.id.full_news_image_caption);
        imageCredits = (TextView) activity.findViewById(R.id.full_news_image_credits);
		titleView = (TextView) activity.findViewById(R.id.full_news_title);
        dateView = (TextView) activity.findViewById(R.id.full_news_date);
        rubricViewTitle = (TextView) activity.findViewById(R.id.full_news_rubric_title);
        rubricView = (TextView) activity.findViewById(R.id.full_news_rubric);
        contentView = (LinearLayout) activity.findViewById(R.id.full_news_content);
    }

    @Override
    public void onResume() {
        super.onResume();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        downloadImages = preferences.getBoolean(PreferencesConstants.PREF_KEY_DOWNLOAD_IMAGE_FULL, PreferencesConstants.DOWNLOAD_IMAGE_FULL_DEFAULT);
        textSize = preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_FULL_TEXT_SIZE, PreferencesConstants.NEWS_FULL_TEXT_SIZE_DEFAULT);

        if (newsId >= 0) {
            showNews(newsId);
        }
    }

    @Override
	public void onDestroy() {
		super.onDestroy();

        loadedNews = null;
	}

	private void renderNews(final News news) {
        titleView.setText(news.getTitle());

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

        Body body = news.getBody();
		if (body != null) {
            final ItemPreferences itemPreferences = new ItemPreferences(downloadImages, textSize);

            for (Item item : body.getItems()) {
                contentView.addView(item.createView(getActivity(), itemPreferences));
            }
		}

        dateView.setText(news.getFormattedPubDate());
        rubricView.setText(" " + news.getRubric().getLabel());

        if (news.hasImage()) {
            BitmapReference bitmapRef = ImageDao.newInstance(getActivity()).read(news.getImageLink());

            if (downloadImages) {
                bitmapRef.getBitmapAsync(new BitmapReference.Callback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        if (isResumed()) {
                            setNewsImage(news, bitmap);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {}
                });
            } else {
                final Bitmap bitmap = bitmapRef.getBitmapIfCached();

                if (bitmap != null) {
                    setNewsImage(news, bitmap);
                }
            }
        }
	}

    private void setNewsImage(News news, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);

        if (news.hasImageCaption()) {
            imageCaption.setText(Html.fromHtml(news.getImageCaption()));
            imageCaption.setMovementMethod(LinkMovementMethod.getInstance());
            imageCaption.setVisibility(View.VISIBLE);

            if (imageCaption.getTextSize() != LentaTextUtils.getNewsFullImageCaptionTextSize(textSize)) {
                imageCaption.setTextSize(LentaTextUtils.getNewsFullImageCaptionTextSize(textSize));
            }
        }

        if (news.hasImageCredits()) {
            imageCredits.setText(Html.fromHtml(news.getImageCredits()));
            imageCredits.setMovementMethod(LinkMovementMethod.getInstance());
            imageCredits.setVisibility(View.VISIBLE);

            if (imageCredits.getTextSize() != LentaTextUtils.getNewsFullImageCreditsTextSize(textSize)) {
                imageCredits.setTextSize(LentaTextUtils.getNewsFullImageCreditsTextSize(textSize));
            }
        }
    }

    public void showNews(long newsId) {
        if (loadedNews == null || loadedNews.getId() != newsId) {
            newsDao.readAsync(newsId, new AsyncDao.DaoReadSingleListener<News>() {
                @Override
                public void finished(News result) {
                    if (isResumed()) {
                        loadedNews = result;
                        renderNews(loadedNews);

                        if (!loadedNews.isRead()) {
                            loadedNews.setRead(true);

                            newsDao.updateAsync(loadedNews, new AsyncDao.DaoUpdateListener() {
                                @Override
                                public void finished(int rowsUpdated) {
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
