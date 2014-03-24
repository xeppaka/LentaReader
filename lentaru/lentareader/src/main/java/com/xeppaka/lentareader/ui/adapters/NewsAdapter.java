package com.xeppaka.lentareader.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.NewsImageKeyCreator;
import com.xeppaka.lentareader.utils.LentaTextUtils;

public class NewsAdapter extends NewsObjectAdapter<News> {
    public static class ViewHolder {
		private final TextView newsTitle;
        private final TextView newsDateTextView;
        private final View newsReadIndicator;
        private final ImageView newsImage;
		private BitmapReference imageRef;
        private int position;
        private String imageUrl;
        private AsyncTask asyncTask;

        public ViewHolder(ImageView newsImage, TextView newsTitle, TextView newsDateTextView, View newsReadIndicator, int position) {
			this.newsImage = newsImage;
			this.newsTitle = newsTitle;
            this.newsDateTextView = newsDateTextView;
            this.newsReadIndicator = newsReadIndicator;
            this.position = position;
		}
		
		public TextView getNewsTitle() {
			return newsTitle;
		}

        public TextView getNewsDateTextView() {
            return newsDateTextView;
        }

        public ImageView getNewsImage() {
			return newsImage;
		}

        public View getNewsReadIndicator() {
            return newsReadIndicator;
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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setAsyncTask(AsyncTask asyncTask) {
            this.asyncTask = asyncTask;
        }

        public void cancelAsyncTask() {
            if (asyncTask != null) {
                asyncTask.cancel(true);
                asyncTask = null;
            }
        }
    }

    public NewsAdapter(Context context) {
		super(context);
	}

    @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		ImageView newsImageView;
		TextView newsTitleTextView;
        View newsReadIndicator;
        TextView newsDateTextView;

        ViewHolder holder;
		
		if (convertView == null) {
			view = inflater.inflate(R.layout.brief_news_list_item, null);

            if (view == null) {
                throw new AssertionError();
            }

			newsTitleTextView = (TextView) view.findViewById(R.id.brief_news_title);
            newsDateTextView = (TextView) view.findViewById(R.id.brief_news_date);
            newsReadIndicator = view.findViewById(R.id.brief_news_read_indicator);

            newsImageView = (ImageView)view.findViewById(R.id.brief_news_image);
            newsImageView.setEnabled(false);
            newsImageView.setOnClickListener(null);

            view.setTag(holder = new ViewHolder(newsImageView, newsTitleTextView, newsDateTextView, newsReadIndicator, position));
		} else {
			view = convertView;
			holder = (ViewHolder)view.getTag();
            holder.cancelAsyncTask();

            holder.setPosition(position);

			newsTitleTextView = holder.getNewsTitle();
            newsDateTextView = holder.getNewsDateTextView();
			newsImageView = holder.getNewsImage();
            newsReadIndicator = holder.getNewsReadIndicator();

            BitmapReference prevImageRef = holder.getImage();
			if (prevImageRef != null) {
				prevImageRef.releaseImageView(holder.getNewsImage());
			}
		}

        if (newsTitleTextView.getTextSize() != LentaTextUtils.getNewsListTitleTextSize(getTextSize())) {
            newsTitleTextView.setTextSize(LentaTextUtils.getNewsListTitleTextSize(getTextSize()));
        }

        if (newsDateTextView.getTextSize() != LentaTextUtils.getNewsListDateTextSize(getTextSize())) {
            newsDateTextView.setTextSize(LentaTextUtils.getNewsListDateTextSize(getTextSize()));
        }

        News news = getItem(position);

        if (news.isRecent()) {
            view.setBackgroundResource(R.drawable.news_recent_item);
        } else {
            view.setBackgroundColor(0x00000000);
        }

        if (!news.isRead()) {
            newsReadIndicator.setVisibility(View.VISIBLE);
        } else {
            newsReadIndicator.setVisibility(View.GONE);
        }

		newsTitleTextView.setText(news.getTitle());
        newsDateTextView.setText(news.getFormattedPubDate());

        if (isDownloadImages()) {
            BitmapReference bitmapRef;

            if (news.hasImage()) {
                bitmapRef = imageDao.readThumbnail(news.getImageLink(), NewsImageKeyCreator.getInstance());
                newsImageView.setImageDrawable(ImageDao.getLoadingThumbnailImage().getDrawableIfCached());

                final ViewHolder holderForAsync = holder;
                final String imageUrl = news.getImageLink();

                holder.setImage(bitmapRef);
                holder.setImageUrl(news.getImageLink());

                final AsyncTask asyncTask = bitmapRef.getDrawableAsync(newsImageView, new AsyncListener<Drawable>() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        if (position != holderForAsync.getPosition() ||
                                (imageUrl == null && holderForAsync.getImage() != ImageDao.getNotAvailableThumbnailImage()) ||
                                (imageUrl != null && !imageUrl.equals(holderForAsync.getImageUrl()))) {
                            return;
                        }

                        final ImageView iv = holderForAsync.getNewsImage();
                        iv.setImageDrawable(drawable);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (position != holderForAsync.getPosition() ||
                                (imageUrl == null && holderForAsync.getImage() != ImageDao.getNotAvailableThumbnailImage()) ||
                                (imageUrl != null && !imageUrl.equals(holderForAsync.getImageUrl()))) {
                            return;
                        }

                        holderForAsync.getNewsImage().setImageDrawable(ImageDao.getNotAvailableThumbnailImage().getDrawableIfCached());
                    }
                });

                holder.setAsyncTask(asyncTask);
            } else {
                bitmapRef = ImageDao.getNotAvailableThumbnailImage();
                newsImageView.setImageBitmap(bitmapRef.getBitmapIfCached());

                holder.setImage(bitmapRef);
                holder.setImageUrl(news.getImageLink());
                holder.setAsyncTask(null);
            }
        } else {
            if (news.hasImage()) {
                final BitmapReference bitmapRef = imageDao.readThumbnail(news.getImageLink(), NewsImageKeyCreator.getInstance());
                final Drawable drawable;

                if ((drawable = bitmapRef.getDrawableIfCached(newsImageView)) != null) {
                    newsImageView.setImageDrawable(drawable);
                } else {
                    newsImageView.setImageDrawable(ImageDao.getTurnedOffImagesThumbnailImageRef().getDrawableIfCached());
                }
            } else {
                newsImageView.setImageBitmap(ImageDao.getTurnedOffImagesThumbnailImageRef().getBitmapIfCached());
            }
        }

		return view;
	}
}
