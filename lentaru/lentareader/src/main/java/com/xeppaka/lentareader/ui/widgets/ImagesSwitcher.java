package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemImage;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.ImageDao;
import com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects.NewsImageKeyCreator;

import java.util.List;
import java.util.Stack;

/**
 * Created by nnm on 12/15/13.
 */
public class ImagesSwitcher extends ViewPager {
    private final ImageDao imageDao;
    private final boolean preview;
    private final GalleryViewPagerAdapter adapter;

    private class GalleryViewPagerAdapter extends PagerAdapter {
        private List<LentaBodyItemImage> images;
        private Stack<ImageView> imageViews;
        private final OnClickListener onClickListener;

        private GalleryViewPagerAdapter(List<LentaBodyItemImage> images, OnClickListener onClickListener) {
            this.images = images;
            this.onClickListener = onClickListener;

            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics());
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

            imageViews = new Stack<ImageView>();

            imageViews.push(createImageView(onClickListener));
            imageViews.push(createImageView(onClickListener));
            imageViews.push(createImageView(onClickListener));
        }

        private ImageView createImageView(View.OnClickListener onClickListener) {
            final ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);

            imageView.setOnClickListener(onClickListener);

            return imageView;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final ImageView currentImageView = imageViews.isEmpty() ? imageViews.push(createImageView(onClickListener)) : imageViews.pop();

            container.addView(currentImageView);

            final LentaBodyItemImage curImage = images.get(position);
            String url;

            if (preview) {
                url = curImage.getPreview_url();
            } else {
                url = curImage.getOriginal_url();

                if (url == null || TextUtils.isEmpty(url)) {
                    url = curImage.getPreview_url();
                }
            }

            final BitmapReference bitmapRef = imageDao.read(url, NewsImageKeyCreator.getInstance());

            Drawable drawable;
            if ((drawable = bitmapRef.getDrawableIfCached(currentImageView)) != null) {
                currentImageView.setImageDrawable(drawable);
            } else {
                currentImageView.setImageDrawable(ImageDao.getLoadingImage().getDrawableIfCached());

                bitmapRef.getDrawableAsync(currentImageView, new AsyncListener<Drawable>() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        currentImageView.setImageDrawable(drawable);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        currentImageView.setImageDrawable(ImageDao.getNotAvailableImage().getDrawableIfCached());
                    }
                });
            }

            return currentImageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            final ImageView view = (ImageView)object;
            view.setImageBitmap(null);

            container.removeView(view);

            imageViews.push(view);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        public void becomeVisible() {
            for (ImageView imageView : imageViews) {
                imageView.setImageDrawable(null);
            }
        }

        public void becomeInvisible() {}
    }

    public ImagesSwitcher(Context context, List<LentaBodyItemImage> images, boolean preview) {
        this(context, images, null, preview);
    }

    public ImagesSwitcher(Context context, List<LentaBodyItemImage> images, OnClickListener onClickListener, boolean preview) {
        super(context);

        this.imageDao = ImageDao.getInstance(context);
        this.preview = preview;

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        setAdapter(adapter = new GalleryViewPagerAdapter(images, onClickListener));
    }

    public void becomeVisible() {
        adapter.becomeVisible();
    }

    public void becomeInvisible() {
        adapter.becomeInvisible();
    }
}
