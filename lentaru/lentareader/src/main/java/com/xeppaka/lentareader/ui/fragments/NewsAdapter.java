package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;
import com.xeppaka.lentareader.utils.LentaTextUtils;

import java.util.Set;

public class NewsAdapter extends NewsObjectAdapter<News> {
    private static class ViewHolder {
		private final TextView newsTitle;
        private final TextView newsDescription;
        private final TextView newsDate;
        private final TextView newsImageCaption;
        private final TextView newsImageCredits;
        private final TextView newsRubric;
        private final TextView newsRubricTitle;
        private final View newsDescriptionPanel;
        private final View newsReadIndicator;
        private final ImageView newsImage;
		private BitmapReference imageRef;
        private int position;
        private String imageUrl;
        private AsyncTask asyncTask;

        public ViewHolder(ImageView newsImage, TextView newsTitle, TextView newsDescription, TextView newsDate,
                          TextView newsImageCaption, TextView newsImageCredits, TextView newsRubricTitle, TextView newsRubric, View newsDescriptionPanel,
                          View newsReadIndicator, int position) {
			this.newsImage = newsImage;
			this.newsTitle = newsTitle;
            this.newsDescription = newsDescription;
            this.newsDate = newsDate;
            this.newsImageCaption = newsImageCaption;
            this.newsImageCredits = newsImageCredits;
            this.newsRubricTitle = newsRubricTitle;
            this.newsRubric = newsRubric;
            this.newsDescriptionPanel = newsDescriptionPanel;
            this.newsReadIndicator = newsReadIndicator;
            this.position = position;
		}
		
		public TextView getNewsTitle() {
			return newsTitle;
		}

        public TextView getNewsDescription() {
            return newsDescription;
        }

        public TextView getNewsDate() {
            return newsDate;
        }

        public TextView getNewsImageCaption() {
            return newsImageCaption;
        }

        public TextView getNewsImageCredits() {
            return newsImageCredits;
        }

        public TextView getNewsRubricTitle() {
            return newsRubricTitle;
        }

        public TextView getNewsRubric() {
            return newsRubric;
        }

        public ImageView getNewsImage() {
			return newsImage;
		}

        public View getNewsDescriptionPanel() {
            return newsDescriptionPanel;
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

    private boolean downloadImages;
    private int textSize;

    public NewsAdapter(Context context) {
		super(context);
	}

    @Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		ImageView newsImageView;
		TextView newsTitleTextView;
        TextView newsDescriptionTextView;
        TextView newsDateTextView;
        TextView newsCaptionTextView;
        TextView newsCreditsTextView;
        TextView newsRubricTitle;
        TextView newsRubric;
        View newsDescriptionPanel;
        View newsReadIndicator;

        ViewHolder holder;
		
		if (convertView == null) {
			view = inflater.inflate(R.layout.brief_news_list_item, null);

            if (view == null) {
                throw new AssertionError();
            }

			newsTitleTextView = (TextView)view.findViewById(R.id.brief_news_title);
            newsDateTextView = (TextView)view.findViewById(R.id.brief_news_date);
            newsCaptionTextView = (TextView)view.findViewById(R.id.brief_news_image_caption);
            newsCreditsTextView = (TextView)view.findViewById(R.id.brief_news_image_credits);
            newsRubricTitle = (TextView)view.findViewById(R.id.brief_news_rubric_title);
            newsRubric = (TextView)view.findViewById(R.id.brief_news_rubric);
            newsDescriptionTextView = (TextView)view.findViewById(R.id.brief_news_description);
            newsDescriptionPanel = view.findViewById(R.id.brief_news_description_panel);
            newsReadIndicator = view.findViewById(R.id.brief_news_read_indicator);

            newsCaptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
            newsCreditsTextView.setMovementMethod(LinkMovementMethod.getInstance());

            newsCaptionTextView.setFocusable(false);
            newsCreditsTextView.setFocusable(false);

            newsImageView = (ImageView)view.findViewById(R.id.brief_news_image);

            view.setTag(holder = new ViewHolder(newsImageView, newsTitleTextView, newsDescriptionTextView, newsDateTextView,
                    newsCaptionTextView, newsCreditsTextView, newsRubricTitle, newsRubric, newsDescriptionPanel, newsReadIndicator, position));

            final ViewHolder holderForAsync = holder;

            newsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final View currentNewsDescriptionTextView = holderForAsync.getNewsDescriptionPanel();
                    final int currentPosition = holderForAsync.getPosition();
                    final News n = getItem(currentPosition);

                    if (currentNewsDescriptionTextView.getVisibility() == View.GONE) {
                        currentNewsDescriptionTextView.setVisibility(View.VISIBLE);

                        if (n != null && getExpandedItems() != null) {
                            getExpandedItems().add(n.getId());
                        }
                    } else {
                        currentNewsDescriptionTextView.setVisibility(View.GONE);

                        if (n != null && getExpandedItems() != null) {
                            getExpandedItems().remove(n.getId());
                        }
                    }
                }
            });
		} else {
			view = convertView;
			holder = (ViewHolder)view.getTag();
            holder.cancelAsyncTask();

            holder.setPosition(position);

			newsTitleTextView = holder.getNewsTitle();
            newsDescriptionTextView = holder.getNewsDescription();
            newsDateTextView = holder.getNewsDate();
            newsCaptionTextView = holder.getNewsImageCaption();
            newsCreditsTextView = holder.getNewsImageCredits();
            newsRubricTitle = holder.getNewsRubricTitle();
            newsRubric = holder.getNewsRubric();
			newsImageView = holder.getNewsImage();
            newsDescriptionPanel = holder.getNewsDescriptionPanel();
            newsReadIndicator = holder.getNewsReadIndicator();

//            BitmapReference prevImageRef = holder.getImage();
//			if (prevImageRef != null) {
//				prevImageRef.releaseBitmap();
//			}
		}

        if (newsTitleTextView.getTextSize() != LentaTextUtils.getNewsListTitleTextSize(textSize)) {
            newsTitleTextView.setTextSize(LentaTextUtils.getNewsListTitleTextSize(textSize));
        }

        if (newsDescriptionTextView.getTextSize() != LentaTextUtils.getNewsListDescriptionTextSize(textSize)) {
            newsDescriptionTextView.setTextSize(LentaTextUtils.getNewsListDescriptionTextSize(textSize));
        }

        if (newsDateTextView.getTextSize() != LentaTextUtils.getNewsListDateTextSize(textSize)) {
            newsDateTextView.setTextSize(LentaTextUtils.getNewsListDateTextSize(textSize));
        }

        if (newsCaptionTextView.getTextSize() != LentaTextUtils.getNewsListImageCaptionTextSize(textSize)) {
            newsCaptionTextView.setTextSize(LentaTextUtils.getNewsListImageCaptionTextSize(textSize));
        }

        if (newsCreditsTextView.getTextSize() != LentaTextUtils.getNewsListImageCreditsTextSize(textSize)) {
            newsCreditsTextView.setTextSize(LentaTextUtils.getNewsListImageCreditsTextSize(textSize));
        }

        final int rubricTextSize = LentaTextUtils.getNewsListRubricTextSize(textSize);
        if (newsRubricTitle.getTextSize() != rubricTextSize) {
            newsRubricTitle.setTextSize(rubricTextSize);
        }

        if (newsRubric.getTextSize() != rubricTextSize) {
            newsRubric.setTextSize(rubricTextSize);
        }

        News news = getItem(position);

        if (news.isRead()) {
            newsReadIndicator.setVisibility(View.GONE);
        } else {
            newsReadIndicator.setVisibility(View.VISIBLE);
        }

		newsTitleTextView.setText(news.getTitle());
        newsDescriptionTextView.setText(Html.fromHtml(news.getDescription()));

        if (getExpandedItems() != null && getExpandedItems().contains(news.getId())) {
            newsDescriptionPanel.setVisibility(View.VISIBLE);
        } else {
            newsDescriptionPanel.setVisibility(View.GONE);
        }

        if (!news.hasImageCaption()) {
            newsCaptionTextView.setVisibility(View.GONE);
        } else {
            newsCaptionTextView.setText(Html.fromHtml(news.getImageCaption()));
            newsCaptionTextView.setVisibility(View.VISIBLE);
        }

        if (!news.hasImageCredits()) {
            newsCreditsTextView.setVisibility(View.GONE);
        } else {
            newsCreditsTextView.setText(Html.fromHtml(news.getImageCredits()));
            newsCreditsTextView.setVisibility(View.VISIBLE);
        }

        newsRubric.setText(" " + news.getRubric().getLabel());
        newsDateTextView.setText(news.getFormattedPubDate());

        if (isDownloadImages()) {
            BitmapReference bitmapRef;

            if (news.hasImage()) {
                bitmapRef = imageDao.readThumbnail(news.getImageLink());
                newsImageView.setImageBitmap(ImageDao.getLoadingThumbnailImage().getBitmapIfCached());

                final ViewHolder holderForAsync = holder;
                final String imageUrl = news.getImageLink();

                holder.setImage(bitmapRef);
                holder.setImageUrl(news.getImageLink());

                final AsyncTask asyncTask = bitmapRef.getBitmapAsync(new BitmapReference.Callback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        if (position != holderForAsync.getPosition() ||
                                (imageUrl == null && holderForAsync.getImage() != ImageDao.getNotAvailableThumbnailImage()) ||
                                (imageUrl != null && !imageUrl.equals(holderForAsync.getImageUrl()))) {
                            return;
                        }

                        holderForAsync.getNewsImage().setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (position != holderForAsync.getPosition() ||
                                (imageUrl == null && holderForAsync.getImage() != ImageDao.getNotAvailableThumbnailImage()) ||
                                (imageUrl != null && !imageUrl.equals(holderForAsync.getImageUrl()))) {
                            return;
                        }

                        holderForAsync.getNewsImage().setImageBitmap(ImageDao.getNotAvailableThumbnailImage().getBitmapIfCached());
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
                final BitmapReference bitmapRef = imageDao.readThumbnail(news.getImageLink());
                final Bitmap bitmap;

                if ((bitmap = bitmapRef.getBitmapIfCached()) != null) {
                    newsImageView.setImageBitmap(bitmap);
                } else {
                    newsImageView.setImageBitmap(ImageDao.getTurnedOffImagesThumbnailImageRef().getBitmapIfCached());
                }
            } else {
                newsImageView.setImageBitmap(ImageDao.getTurnedOffImagesThumbnailImageRef().getBitmapIfCached());
            }
        }

		return view;
	}

    public boolean isDownloadImages() {
        return downloadImages;
    }

    public void setDownloadImages(boolean downloadImages) {
        this.downloadImages = downloadImages;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
