package com.xeppaka.lentareader.ui.fragments;

import com.xeppaka.lentareader.data.NewsType;

/**
 * Created by nnm on 1/14/14.
 */
public interface BriefNewsListPresenterContainer {
    BriefNewsListPresenter getNewsListPresenter(NewsType newsType);
    void setNewsListPresenter(NewsType newsType, BriefNewsListPresenter newsListPresenter);
}
