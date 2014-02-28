package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.body.Body;
import com.xeppaka.lentareader.data.body.items.Item;
import com.xeppaka.lentareader.data.body.items.ItemPreferences;
import com.xeppaka.lentareader.data.comments.Comment;
import com.xeppaka.lentareader.data.comments.Comments;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.downloader.LentaCommentsDownloader;
import com.xeppaka.lentareader.downloader.exceptions.HttpStatusCodeException;
import com.xeppaka.lentareader.parser.comments.CommentsParser;
import com.xeppaka.lentareader.parser.exceptions.ParseException;
import com.xeppaka.lentareader.utils.LentaTextUtils;
import com.xeppaka.lentareader.utils.PreferencesConstants;

import java.io.IOException;
import java.util.List;

public class NewsFullFragment extends Fragment {
    public static final long NO_NEWS_ID = -1;

    private ScrollView scrollContainer;
	private ImageView imageView;
    private TextView imageCaption;
    private TextView imageCredits;
    private TextView titleView;
    private LinearLayout contentView;
    private TextView dateView;
    private TextView rubricViewTitle;
    private TextView rubricView;
    private Button buttonLoadComments;
    private TextView commentsView;

    private long newsId = NO_NEWS_ID;
	private News loadedNews;

    private AsyncDao<News> newsDao;

    private boolean downloadImages;
    private int textSize;
    private boolean resumed;

    private int scrollY;

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

        scrollContainer = (ScrollView) activity.findViewById(R.id.full_news_scroll_container);
        imageView = (ImageView) activity.findViewById(R.id.full_news_image);
        imageCaption = (TextView) activity.findViewById(R.id.full_news_image_caption);
        imageCredits = (TextView) activity.findViewById(R.id.full_news_image_credits);
		titleView = (TextView) activity.findViewById(R.id.full_news_title);
        dateView = (TextView) activity.findViewById(R.id.full_news_date);
        rubricViewTitle = (TextView) activity.findViewById(R.id.full_news_rubric_title);
        rubricView = (TextView) activity.findViewById(R.id.full_news_rubric);
        contentView = (LinearLayout) activity.findViewById(R.id.full_news_content);

        buttonLoadComments = (Button)view.findViewById(R.id.full_news_load_comments);
        buttonLoadComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadComments();
            }
        });
        commentsView = (TextView)view.findViewById(R.id.full_news_comments);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        downloadImages = preferences.getBoolean(PreferencesConstants.PREF_KEY_DOWNLOAD_IMAGE_FULL, PreferencesConstants.DOWNLOAD_IMAGE_FULL_DEFAULT);
        textSize = preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_FULL_TEXT_SIZE, PreferencesConstants.NEWS_FULL_TEXT_SIZE_DEFAULT);

        if (newsId >= 0) {
            loadNews(newsId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        resumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();

        scrollY = scrollContainer.getScrollY();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (loadedNews != null) {
            renderNewsAsync(loadedNews);
        }
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

        loadedNews = null;
	}

    private void renderNewsAsync(final News news) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                renderNews(news);
            }
        });
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
                final View itemView = item.createView(getActivity(), itemPreferences);

                if (itemView != null) {
                    contentView.addView(itemView);
                }
            }
		}

        dateView.setText(news.getFormattedPubDate());
        rubricView.setText(" " + news.getRubric().getLabel());

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollContainer.scrollTo(0, scrollY);
            }
        });

        if (news.hasImage()) {
            final BitmapReference bitmapRef = ImageDao.newInstance(getActivity()).read(news.getImageLink());

            if (downloadImages) {
                bitmapRef.getBitmapAsync(imageView, new BitmapReference.LoadListener() {
                    @Override
                    public void onSuccess(final Bitmap bitmap) {
                        if (isResumed()) {
                            setNewsImage(news, bitmap);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {}
                });
            } else {
                final Bitmap bitmap = bitmapRef.getBitmapIfCached(imageView);

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

    public void loadNews(long newsId) {
        newsDao.readAsync(newsId, new AsyncDao.DaoReadSingleListener<News>() {
            @Override
            public void finished(News result) {
                loadedNews = result;

                if (result != null) {
                    if (isResumed()) {
                        if (resumed) {
                            renderNewsAsync(loadedNews);
                        }
                    }

                    if (!result.isRead()) {
                        result.setRead(true);

                        newsDao.updateAsync(result, new AsyncDao.DaoUpdateListener() {
                            @Override
                            public void finished(int rowsUpdated) {
                            }
                        });
                    }
                }
            }
        });
    }

    public String getNewsLink() {
        if (loadedNews != null) {
            return loadedNews.getLink();
        }

        return null;
    }

    private class LoadCommentsAsync extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return LentaCommentsDownloader.download(loadedNews.getLink());
            } catch (HttpStatusCodeException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            CommentsParser parser = new CommentsParser();

            try {
                Comments comments = parser.parse(s);
                List<Comment> orderedComments = comments.getOrderedComments();

                StringBuilder sb = new StringBuilder();
                for (Comment comment : orderedComments) {
                    sb.append(comment.getText()).append("\n");
                }

                commentsView.setText(sb.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadComments() {
        AsyncTask<String, Void, String> tt = new LoadCommentsAsync();
        tt.execute(loadedNews.getLink());
    }
}
