package com.xeppaka.lentareader.data.dao.daoobjects.imagedaoobjects;

import android.util.Log;

import com.xeppaka.lentareader.utils.LentaConstants;
import com.xeppaka.lentareader.utils.URLHelper;

import java.net.MalformedURLException;

/**
 * Created by nnm on 3/3/14.
 */
public class NewsImageKeyCreator implements ImageKeyCreator {
    private static NewsImageKeyCreator INSTANCE;

    public static NewsImageKeyCreator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewsImageKeyCreator();
        }

        return INSTANCE;
    }

    @Override
    public String getImageKey(String newsImageUrl) throws MalformedURLException {
        if (newsImageUrl == null || newsImageUrl.isEmpty()) {
            throw new IllegalArgumentException("newsImageUrl is null or empty");
        }

        int lastSlash = newsImageUrl.lastIndexOf('/') + 1;

        if (lastSlash == -1) {
            throw new MalformedURLException("Image url has wrong format: " + newsImageUrl);
        }

        int lastDot = newsImageUrl.lastIndexOf('.');
        if (lastDot == -1)
            lastDot = newsImageUrl.length();

        if (lastDot <= lastSlash) {
            throw new MalformedURLException("Image url has wrong format: " + newsImageUrl);
        }

        return newsImageUrl.substring(lastSlash, lastDot);
    }
}
