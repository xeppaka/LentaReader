package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.xeppaka.lentareader.data.body.items.LentaBodyItemImageGallery;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;

/**
 * Created by nnm on 12/15/13.
 */
public class ImageGallery extends ImageSwitcher implements BitmapReference.Callback, View.OnClickListener {
    private LentaBodyItemImageGallery gallery;
    private int curImageIndex;
    private final ImageDao imageDao;

    private class ImageGalleryFactory implements ViewFactory {
        @Override
        public View makeView() {
            ImageView imageView = new ImageView(getContext());
            imageView.setOnClickListener(ImageGallery.this);

            return imageView;
        }
    }

    public ImageGallery(Context context, LentaBodyItemImageGallery gallery) {
        super(context);
        setFactory(new ImageGalleryFactory());

        this.gallery = gallery;

        imageDao = ImageDao.newInstance();

        if (!gallery.isEmpty()) {
            imageDao.read(gallery.getImage(curImageIndex).getPreview_url()).getBitmapAsync(this);
        } else {
            setBitmap(ImageDao.getNotAvailableImage().getBitmapIfCached());
        }
    }

    public void onNext() {
        if (gallery.isEmpty()) {
            return;
        }

        curImageIndex = (curImageIndex + 1) % gallery.size();

        imageDao.read(gallery.getImage(curImageIndex).getPreview_url()).getBitmapAsync(this);
    }

    public void onPrev() {
        if (gallery.isEmpty()) {
            return;
        }

        curImageIndex = (curImageIndex - 1) % gallery.getImages().size();

        imageDao.read(gallery.getImage(curImageIndex).getPreview_url()).getBitmapAsync(this);
    }

    public void setBitmap(Bitmap bitmap) {
        setImageDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
    }

    @Override
    public void onSuccess(Bitmap bitmap) {
        setBitmap(bitmap);
    }

    @Override
    public void onFailure(Exception e) {
        setBitmap(ImageDao.getNotAvailableImage().getBitmapIfCached());
    }

    @Override
    public void onClick(View view) {
        onNext();
    }
}
