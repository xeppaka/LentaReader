package com.xeppaka.lentareader.ui.fragments;

import com.xeppaka.lentareader.data.Rubrics;

/**
 * Created by kacpa01 on 1/14/14.
 */
public interface BriefNewsListPresenter {
    Rubrics getCurrentRubric();
    void setCurrentRubric(Rubrics currentRubric);
    void selectItem(int position);
}
