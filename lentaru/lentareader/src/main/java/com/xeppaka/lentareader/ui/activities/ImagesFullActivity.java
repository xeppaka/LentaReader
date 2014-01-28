package com.xeppaka.lentareader.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.xeppaka.lentareader.data.body.items.LentaBodyItemImage;
import com.xeppaka.lentareader.ui.widgets.ImagesSwitcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nnm on 1/25/14.
 */
public class ImagesFullActivity extends Activity {
    public static final String IMAGES_KEY = "images";
    public static final String INDEX_KEY = "index";

    private List<LentaBodyItemImage> images;
    private int index;

    private ImagesSwitcher imagesSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            images = (List<LentaBodyItemImage>)savedInstanceState.getSerializable(IMAGES_KEY);
            index = savedInstanceState.getInt(INDEX_KEY);
        } else {
            final Intent intent = getIntent();

            images = (List<LentaBodyItemImage>)intent.getSerializableExtra(IMAGES_KEY);
            index = intent.getIntExtra(INDEX_KEY, 0);
        }

        imagesSwitcher = new ImagesSwitcher(this, images, false);
        setContentView(imagesSwitcher);

        imagesSwitcher.setCurrentItem(index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(IMAGES_KEY, new ArrayList<LentaBodyItemImage>(images));
        outState.putInt(INDEX_KEY, index);
    }
}
