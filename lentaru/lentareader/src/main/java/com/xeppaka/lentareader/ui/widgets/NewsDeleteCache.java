package com.xeppaka.lentareader.ui.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
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

                NewsDao.getInstance(getContext().getContentResolver()).deleteAsync(new AsyncDao.DaoDeleteListener() {
                    @Override
                    public void finished(int rowsDeleted) {
                        final String result = getContext().getResources().getString(R.string.news_clear_cache_result);
                        Toast.makeText(getContext(), String.format(result, rowsDeleted), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.news_clear_cache_confirmation).setNegativeButton(android.R.string.no, null).
            setPositiveButton(android.R.string.yes, positiveListener).show();
    }
}
