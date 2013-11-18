package com.xeppaka.lentareader.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.objects.BitmapReference;
import com.xeppaka.lentareader.data.dao.objects.ImageDao;
import com.xeppaka.lentareader.data.dao.objects.NewsDao;

public class NewsFullFragment extends Fragment {
	private TextView titleView;
	private ImageView imageView;
    private LinearLayout contentView;

	private long newsId;
	private News loadedNews;
	
	public NewsFullFragment(long newsId){
		this.newsId = newsId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.full_news_fragment, null);
	}

	public NewsFullFragment() {
		super();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		titleView = (TextView) getActivity().findViewById(R.id.full_news_title);
		imageView = (ImageView) getActivity().findViewById(R.id.full_news_image);
        contentView = (LinearLayout) getActivity().findViewById(R.id.full_news_content);

		imageView.setImageBitmap(null);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		AsyncDao<News> nd = NewsDao.getInstance(getActivity().getContentResolver());
		nd.readAsync(newsId, new AsyncDao.DaoReadSingleListener<News>() {
			@Override
			public void finished(News result) {
				loadedNews = result;
				showNews(loadedNews);
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		loadedNews.getImage().releaseBitmap();
	}

	private void showNews(News news) {
		Body body = news.getBody();
		
		if (body != null) {
            for (Item item : body.getItems()) {
                contentView.addView(item.createView(getActivity()));
            }
		}
		
		titleView.setText(news.getTitle());
		
		news.getImage().getBitmapAsync(new BitmapReference.BitmapLoadListener() {
			@Override
			public void onBitmapLoaded(Bitmap bitmap) {
            if (bitmap == null) {
                bitmap = ImageDao.getNotAvailableImage().getBitmap();
            }

            imageView.setImageBitmap(bitmap);
			}
		});
	}
}
