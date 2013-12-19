package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemImage;
import com.xeppaka.lentareader.data.body.items.LentaBodyItemImageGallery;
import com.xeppaka.lentareader.data.dao.daoobjects.BitmapReference;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;

import java.util.List;

/**
 * Created by nnm on 12/15/13.
 */
public class ImageGalleryFactory {
    private Context context;

    private class ImageGalleryState {
        private LentaBodyItemImageGallery gallery;
        private int curImageIndex;

        private ImageGalleryState(LentaBodyItemImageGallery gallery) {
            this.gallery = gallery;
        }
    }

    public ImageGalleryFactory(Context context) {
        this.context = context;
    }

    public View createView(LentaBodyItemImageGallery gallery) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.id.galleryImageSwitcher, null);
        final ImageSwitcher imageSwitcher = (ImageSwitcher)layout.findViewById(R.id.galleryImageSwitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView iview = new ImageView(context);
                return iview;
            }
        });

        final ImageGalleryState galleryState = new ImageGalleryState(gallery);

        imageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNext(galleryState);
            }
        });

        BitmapReference reference = ImageDao.newInstance().read(gallery.getImages().get(0).getPreview_url());
    }

    private void onNext(ImageGalleryState galleryState) {
        
    }

    private void onPrev(ImageGalleryState galleryState) {

    }
}
