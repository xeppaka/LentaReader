package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.xeppaka.lentareader.data.body.items.LentaBodyItemImage;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemImageGallery;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;
import com.xeppaka.lentareader.downloader.Page;

import java.util.Collections;
import java.util.List;

/**
 * Created by nnm on 12/15/13.
 */
public class ImagesSwitcher extends ViewPager {
    private final ImageDao imageDao;
    private List<LentaBodyItemImage> images;
    private final GalleryViewPagerAdapter adapter;

    private class GalleryViewPagerAdapter extends PagerAdapter {
        private List<LentaBodyItemImage> images;
        private ImageView[] imageViews;

        private GalleryViewPagerAdapter(List<LentaBodyItemImage> images) {
            this.images = images;

            createViews();
        }

        private void createViews() {
            this.imageViews = new ImageView[images.size()];

            for (int i = 0; i < images.size(); ++i) {
                final ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setAdjustViewBounds(true);

                imageViews[i] = imageView;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final ImageView currentImageView = imageViews[position];

            container.addView(currentImageView);
            BitmapReference bitmapRef = imageDao.read(images.get(position).getPreview_url());

            Bitmap bitmap;
            if ((bitmap = bitmapRef.getBitmapIfCached()) != null) {
                setBitmap(currentImageView, bitmap);
            } else {
                final Bitmap loadingBitmap = ImageDao.getLoadingImage().getBitmapIfCached();
                setBitmap(currentImageView, loadingBitmap);

                bitmapRef.getBitmapAsync(new BitmapReference.Callback() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        setBitmap(currentImageView, bitmap);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        setBitmap(currentImageView, ImageDao.getNotAvailableImage().getBitmapIfCached());
                    }
                });
            }

            return currentImageView;
        }

        private void setBitmap(ImageView imageView, Bitmap bitmap) {
            imageView.setImageDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
            getLayoutParams().height = Math.round(getWidth() / (float) bitmap.getWidth() * bitmap.getHeight());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        public void setImages(List<LentaBodyItemImage> images) {
            this.images = images;

            createViews();
            notifyDataSetChanged();
        }
    }

    public ImagesSwitcher(Context context, List<LentaBodyItemImage> images) {
        super(context);

        this.images = images;
        this.imageDao = ImageDao.newInstance(context);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        setAdapter(adapter = new GalleryViewPagerAdapter(images));
    }

    public ImagesSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);

        images = Collections.emptyList();
        imageDao = ImageDao.newInstance(context);

        setAdapter(adapter = new GalleryViewPagerAdapter(images));
    }

    public void setImages(List<LentaBodyItemImage> images) {
        adapter.setImages(images);
    }
}
