package com.xeppaka.lentareader.ui.widgets.fullnews;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nnm on 3/15/14.
 */
public interface FullNewsElement {
    public View getView(ViewGroup parent);
    public void becomeVisible();
    public void becomeInvisible();
    public void setOptions(ElementOptions options);
}
