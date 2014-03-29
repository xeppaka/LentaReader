package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.ArticleDao;
import com.xeppaka.lentareader.ui.adapters.fullnews.FullArticleAdapter;
import com.xeppaka.lentareader.ui.widgets.fullnews.ElementOptions;
import com.xeppaka.lentareader.ui.widgets.fullnews.builder.FullArticleElementsBuilder;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by nnm on 3/16/14.
 */
public class ArticleFullFragment extends FullFragmentBase {
    private FullArticleAdapter adapter;
    private boolean read;
    private AsyncNODao<Article> dao;

    public ArticleFullFragment(long id) {
        super(id);
    }

    public ArticleFullFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("id", getDbId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            setDbId(savedInstanceState.getLong("id", Dao.NO_ID));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        dao = ArticleDao.getInstance(activity.getContentResolver());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = getListView();
        listView.setSelector(new ColorDrawable(0x00000000));
        listView.setDividerHeight(0);
    }

    private void loadNews() {
        setListAdapter(null);

        final long id = getDbId();

        if (id != Dao.NO_ID && dao != null) {
            dao.readAsync(id, new AsyncListener<Article>() {
                @Override
                public void onSuccess(Article article) {
                    if (isResumed()) {
                        setLink(article.getLink());

                        final FullArticleElementsBuilder builder = new FullArticleElementsBuilder(article, getActivity(), ArticleFullFragment.this);
                        builder.setOptions(getOptions());

                        setListAdapter(adapter = new FullArticleAdapter(builder.build()));
                    }

                    read = article.isRead();
                }

                @Override
                public void onFailure(Exception e) {}
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final boolean downloadImages = preferences.getBoolean(PreferencesConstants.PREF_KEY_DOWNLOAD_IMAGE_FULL, PreferencesConstants.DOWNLOAD_IMAGE_FULL_DEFAULT);
        final int textSize = preferences.getInt(PreferencesConstants.PREF_KEY_NEWS_FULL_TEXT_SIZE, PreferencesConstants.NEWS_FULL_TEXT_SIZE_DEFAULT);

        setOptions(new ElementOptions(textSize, downloadImages));

        if (adapter != null) {
            adapter.becomeVisible();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.becomeInvisible();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter == null) {
            loadNews();
        }
    }

    @Override
    public void markRead() {
        if (dao != null) {
            dao.markReadAsync(getDbId(), new AsyncListener<Integer>() {
                @Override
                public void onSuccess(Integer value) {}

                @Override
                public void onFailure(Exception e) {}
            });
        }
    }

    @Override
    public boolean copyLinkToBuffer() {
        final boolean copied = super.copyLinkToBuffer();

        if (copied) {
            Toast.makeText(getActivity(), R.string.info_article_link_copied_toast, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.error_article_link_copied_toast, Toast.LENGTH_SHORT).show();
        }

        return copied;
    }

    @Override
    public boolean openLinkInBrowser() {
        final boolean opened = super.openLinkInBrowser();

        if (!opened) {
            Toast.makeText(getActivity(), R.string.error_link_open_in_browser_toast, Toast.LENGTH_SHORT).show();
        }

        return opened;
    }

    @Override
    public void update() {
        loadNews();
    }
}
