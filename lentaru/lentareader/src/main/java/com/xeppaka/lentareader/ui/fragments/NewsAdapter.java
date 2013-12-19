package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;

public class NewsAdapter extends NewsObjectAdapter<News> {

    private final ImageDao imageDao;

	private static class ViewHolder {
		private final TextView newsTitle;
		private final ImageView newsImage;
		private BitmapReference imageRef;
        private int position;

        public ViewHolder(ImageView newsImage, TextView newsTitle, BitmapReference imageRef, int position) {
			this.newsImage = newsImage;
			this.newsTitle = newsTitle;
			this.imageRef = imageRef;
            this.position = position;
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

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }

	public NewsAdapter(Context context) {
		super(context);

        imageDao = ImageDao.newInstance();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		ImageView newsImageView;
		TextView newsTitleTextView;

		News news = getItem(position);
        BitmapReference bitmapRef = imageDao.read(news.getImageLink());
        ViewHolder holder;
		
		if (convertView == null) {
			view = inflater.inflate(R.layout.brief_news_list_item, null);
			newsTitleTextView = (TextView)view.findViewById(R.id.brief_news_title);
			newsImageView = (ImageView)view.findViewById(R.id.brief_news_image);
            newsImageView.setImageDrawable(null);

			view.setTag(holder = new ViewHolder(newsImageView, newsTitleTextView, bitmapRef, position));
		} else {
			view = convertView;
			holder = (ViewHolder)view.getTag();
            holder.setPosition(position);

			newsTitleTextView = holder.getNewsTitle();
			newsImageView = holder.getNewsImage();

			BitmapReference prevImageRef = holder.getImage();
			if (prevImageRef != null) {
				prevImageRef.releaseBitmap();
			}
			
			holder.setImage(bitmapRef);
		}

		newsTitleTextView.setText(news.getTitle());

		final ImageView newsImageViewForAsync = newsImageView;
        final ViewHolder holderForAsync = holder;

        bitmapRef.getBitmapAsync(new BitmapReference.Callback() {
			@Override
			public void onSuccess(Bitmap bitmap) {
                if (bitmap != null && position == holderForAsync.getPosition()) {
                    newsImageViewForAsync.setImageBitmap(bitmap);
                }
			}

            @Override
            public void onFailure() {
                newsImageViewForAsync.setImageBitmap(ImageDao.getNotAvailableImage().getBitmap());
            }
        });
		
		return view;
	}
}
