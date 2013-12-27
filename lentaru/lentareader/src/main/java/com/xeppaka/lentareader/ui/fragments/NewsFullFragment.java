package com.xeppaka.lentareader.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
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
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;

public class NewsFullFragment extends Fragment {
	private ImageView imageView;
    private TextView imageCaption;
    private TextView imageCredits;
    private TextView titleView;
    private LinearLayout contentView;
    private TextView rubricView;

	private long newsId;
	private News loadedNews;
	
	public NewsFullFragment(long newsId){
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

		return inflater.inflate(R.layout.full_news_fragment, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

        imageView = (ImageView) getActivity().findViewById(R.id.full_news_image);
        imageCaption = (TextView) getActivity().findViewById(R.id.full_news_image_caption);
        imageCredits = (TextView) getActivity().findViewById(R.id.full_news_image_credits);
		titleView = (TextView) getActivity().findViewById(R.id.full_news_title);
        rubricView = (TextView) getActivity().findViewById(R.id.full_news_rubric);

        contentView = (LinearLayout) getActivity().findViewById(R.id.full_news_content);
	}

	@Override
	public void onResume() {
		super.onResume();

        if (loadedNews != null) {
            showNews(loadedNews);
        } else {
            AsyncDao<News> nd = NewsDao.getInstance(getActivity().getContentResolver());
            nd.readAsync(newsId, new AsyncDao.DaoReadSingleListener<News>() {
                @Override
                public void finished(News result) {
                    loadedNews = result;
                    showNews(loadedNews);
                }
            });
        }
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void showNews(final News news) {
        titleView.setText(news.getTitle());

        Body body = news.getBody();
		if (body != null) {
            for (Item item : body.getItems()) {
                contentView.addView(item.createView(getActivity()));
            }
		}

        rubricView.setText(" " + news.getRubric().getLabel());

        if (news.hasImage()) {
            BitmapReference bitmapRef = ImageDao.newInstance().read(news.getImageLink());
            bitmapRef.getBitmapAsync(new BitmapReference.Callback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);

                    if (news.hasImageCaption()) {
                        imageCaption.setText(Html.fromHtml(news.getImageCaption()));
                        imageCaption.setVisibility(View.VISIBLE);
                    }

                    if (news.hasImageCredits()) {
                        imageCredits.setText(Html.fromHtml(news.getImageCredits()));
                        imageCredits.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Exception e) {}
            });
        }
	}
}
