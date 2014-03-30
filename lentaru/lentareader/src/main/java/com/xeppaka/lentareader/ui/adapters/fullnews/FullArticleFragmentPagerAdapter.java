package com.xeppaka.lentareader.ui.adapters.fullnews;

import android.support.v4.app.FragmentManager;

import com.xeppaka.lentareader.ui.fragments.ArticleFullFragment;
import com.xeppaka.lentareader.ui.fragments.FullFragmentBase;

import java.util.List;

/**
 * Created by nnm on 3/29/14.
 */
public class FullArticleFragmentPagerAdapter extends FullFragmentPagerAdapter {
    public FullArticleFragmentPagerAdapter(FragmentManager fm, List<Long> ids) {
        super(fm, ids);
    }

    @Override
    public FullFragmentBase createFragment() {
        return new ArticleFullFragment();
    }
}
