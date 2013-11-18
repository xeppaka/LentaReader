package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.objects.BitmapReference;
import com.xeppaka.lentareader.data.dao.objects.BitmapReference.BitmapLoadListener;
import com.xeppaka.lentareader.data.dao.objects.ImageDao;

public class NewsAdapter extends NewsObjectAdapter<News> {
	
	private static class ViewHolder {
		private final TextView newsTitle;
		private final ImageView newsImage;
		private BitmapReference imageRef;
		
		public ViewHolder(ImageView newsImage, TextView newsTitle, BitmapReference imageRef) {
			this.newsImage = newsImage;
			this.newsTitle = newsTitle;
			this.imageRef = imageRef;
		}
		
		public TextView getNewsTitle() {
			return newsTitle;
		}

		public ImageView getNewsImage() {
			return newsImage;
		}
		
		public BitmapReference getImage() {
			return imageRef;
		}
		
		public void setImage(BitmapReference imageRef) {
			this.imageRef = imageRef;
		}
	}

	public NewsAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ImageView newsImageView;
		TextView newsTitleTextView;
		BitmapReference imageRef;

		News news = getItem(position);
		imageRef = news.getThumbnailImage();
		
		if (convertView == null) {
			view = inflater.inflate(R.layout.brief_news_list_item, null);
			newsTitleTextView = (TextView)view.findViewById(R.id.brief_news_title);
			newsImageView = (ImageView)view.findViewById(R.id.brief_news_image);
			
			view.setTag(new ViewHolder(newsImageView, newsTitleTextView, imageRef));
		} else {
			view = convertView;
			ViewHolder holder = (ViewHolder)view.getTag();
			
			newsTitleTextView = holder.getNewsTitle();
			newsImageView = holder.getNewsImage();
			newsImageView.setImageBitmap(null);
			
			BitmapReference prevImageRef = holder.getImage();
			if (prevImageRef != null) {
				prevImageRef.releaseBitmap();
			}
			
			holder.setImage(imageRef);
		}

		newsTitleTextView.setText(news.getTitle());

		final ImageView newsImageViewForAsync = newsImageView;
		
		imageRef.getBitmapAsync(new BitmapLoadListener() {
			@Override
			public void onBitmapLoaded(final Bitmap bitmap) {
            if (bitmap != null) {
                newsImageViewForAsync.setImageBitmap(bitmap);
            } else {
                newsImageViewForAsync.setImageBitmap(ImageDao.getNotAvailableImage().getBitmap());
            }
			}
		});
		
		return view;
	}
}
