package com.xeppaka.lentareader.ui.fragments;

import com.xeppaka.lentareader.data.NewsType;

/**
 * Created by nnm on 1/14/14.
 */
public interface RubricsSelectorContainer {
    RubricsSelector getRubricsSelector(NewsType newsType);
    void setRubricsSelector(NewsType newsType, RubricsSelector rubricsSelector);
}
