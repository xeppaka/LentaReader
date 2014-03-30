package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.ArticleDao;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by kacpa01 on 1/3/14.
 */
public class ArticleDeleteIntervalPreference extends DeleteIntervalPreference {
    public ArticleDeleteIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.delete_interval_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected int getDeleteAfterDaysDefault() {
        return PreferencesConstants.ARTICLE_DELETE_ARTICLES_DAYS_DEFAULT;
    }

    protected void updateSummaryText() {
        final Resources resources = getContext().getResources();
        final int deleteAfterDays = getDeleteAfterDays();
        if (deleteAfterDays == 1) {
            setSummary(String.format(resources.getString(R.string.pref_article_deletion_interval_summary_day), deleteAfterDays));
        } else {
            setSummary(String.format(resources.getString(R.string.pref_article_deletion_interval_summary_days), deleteAfterDays));
        }
    }

    @Override
    protected int getMinValue() {
        return 1;
    }

    @Override
    protected int getMaxValue() {
        return 30;
    }

    @Override
    protected AsyncNODao<?> getDao() {
        return ArticleDao.getInstance(getContext().getContentResolver());
    }

    @Override
    protected int getTitleResId() {
        return R.string.article_delete_interval_header;
    }
}
