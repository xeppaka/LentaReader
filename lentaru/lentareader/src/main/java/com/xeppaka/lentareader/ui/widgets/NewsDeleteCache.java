package com.xeppaka.lentareader.ui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.Article;
import com.xeppaka.lentareader.data.dao.daoobjects.ArticleDao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;

/**
 * Created by nnm on 1/23/14.
 */
public class NewsDeleteCache extends Preference {
    public NewsDeleteCache(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        final DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();

                NewsDao.getInstance(getContext().getContentResolver()).deleteAsync(new AsyncListener<Integer>() {
                    @Override
                    public void onSuccess(final Integer newsDeleted) {
                        ArticleDao.getInstance(getContext().getContentResolver()).deleteAsync(new AsyncListener<Integer>() {
                            @Override
                            public void onSuccess(Integer articlesDeleted) {
                                final String result = getContext().getResources().getString(R.string.clear_cache_result);
                                Toast.makeText(getContext(), String.format(result, newsDeleted, articlesDeleted), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Exception e) {}
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {}
                });
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.news_clear_cache_confirmation).setNegativeButton(android.R.string.no, null).
            setPositiveButton(android.R.string.yes, positiveListener).show();
    }
}
