package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xeppaka.lentareader.data.body.items.LentaBodyItemImage;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;

import java.util.List;

/**
 * Created by nnm on 12/15/13.
 */
public class ImagesSwitcher extends ViewPager {
    private final ImageDao imageDao;
    private final boolean preview;

    private class GalleryViewPagerAdapter extends PagerAdapter {
        private List<LentaBodyItemImage> images;
        private ImageView[] imageViews;

        private GalleryViewPagerAdapter(List<LentaBodyItemImage> images, OnClickListener onClickListener) {
            this.images = images;

            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220, getResources().getDisplayMetrics());
            setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

            createViews(onClickListener);
        }

        private void createViews(View.OnClickListener onClickListener) {
            this.imageViews = new ImageView[images.size()];

            for (int i = 0; i < images.size(); ++i) {
                final ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setAdjustViewBounds(true);

                imageView.setOnClickListener(onClickListener);

                imageViews[i] = imageView;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final ImageView currentImageView = imageViews[position];

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

            final BitmapReference bitmapRef = imageDao.read(url);

            Bitmap bitmap;
            if ((bitmap = bitmapRef.getBitmapIfCached(currentImageView)) != null) {
                currentImageView.setImageBitmap(bitmap);
            } else {
                currentImageView.setImageBitmap(ImageDao.getLoadingImage().getBitmapIfCached());

                bitmapRef.getBitmapAsync(currentImageView, new BitmapReference.LoadListener() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        currentImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        currentImageView.setImageBitmap(ImageDao.getNotAvailableImage().getBitmapIfCached());
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
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

    public ImagesSwitcher(Context context, List<LentaBodyItemImage> images, boolean preview) {
        this(context, images, null, preview);
    }

    public ImagesSwitcher(Context context, List<LentaBodyItemImage> images, OnClickListener onClickListener, boolean preview) {
        super(context);

        this.imageDao = ImageDao.newInstance(context);
        this.preview = preview;

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        setAdapter(new GalleryViewPagerAdapter(images, onClickListener));
    }
}
