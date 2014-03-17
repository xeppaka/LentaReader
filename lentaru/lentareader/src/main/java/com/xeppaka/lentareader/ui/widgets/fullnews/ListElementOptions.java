package com.xeppaka.lentareader.ui.widgets.fullnews;

/**
 * Created by nnm on 3/16/14.
 */
public class ListElementOptions {
    private int textSize;
    private boolean downloadImages;

    public ListElementOptions(int textSize, boolean downloadImages) {
        this.textSize = textSize;
        this.downloadImages = downloadImages;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public boolean isDownloadImages() {
        return downloadImages;
    }

    public void setDownloadImages(boolean downloadImages) {
        this.downloadImages = downloadImages;
    }
}
