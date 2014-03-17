package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;

import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.daoobjects.ArticleDao;

public class ArticleFullFragment extends NewsObjectFullFragment<Article> {
    private AsyncDao<Article> dao;

    public ArticleFullFragment() {}

    public ArticleFullFragment(long id) {
        super(id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        dao = ArticleDao.getInstance(activity.getContentResolver());
    }

    @Override
    public AsyncDao<Article> getDao() {
        return dao;
    }
}
