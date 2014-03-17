package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import android.view.LayoutInflater;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsListElement;

import java.util.Collection;
import java.util.List;

/**
 * Created by nnm on 3/15/14.
 */
public interface FullNewsObjectElementsBuilder<T extends NewsObject> {
    Collection<FullNewsListElement> build();
}
