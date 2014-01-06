package com.xeppaka.lentareader.data.body.items;

/**
 * Created by nnm on 1/4/14.
 */
public class ItemPreferences {
    private boolean downloadImages;
    private int textSize;

    public ItemPreferences(boolean downloadImages, int textSize) {
        this.downloadImages = downloadImages;
        this.textSize = textSize;
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
