package com.xeppaka.lentareader.ui.widgets.fullnews.builder;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.ui.widgets.fullnews.FullNewsElement;

import java.util.Collection;

/**
 * Created by nnm on 3/15/14.
 */
public interface FullNewsObjectElementsBuilder<T extends NewsObject> {
    Collection<FullNewsElement> build();
}
