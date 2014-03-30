package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by kacpa01 on 1/3/14.
 */
public class NewsDeleteIntervalPreference extends DeleteIntervalPreference {
    public NewsDeleteIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.delete_interval_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected int getDeleteAfterDaysDefault() {
        return PreferencesConstants.NEWS_DELETE_NEWS_DAYS_DEFAULT;
    }

    protected void updateSummaryText() {
        final Resources resources = getContext().getResources();
        final int deleteAfterDays = getDeleteAfterDays();
        if (deleteAfterDays == 1) {
            setSummary(String.format(resources.getString(R.string.pref_news_deletion_interval_summary_day), deleteAfterDays));
        } else {
            setSummary(String.format(resources.getString(R.string.pref_news_deletion_interval_summary_days), deleteAfterDays));
        }
    }

    @Override
    protected int getMinValue() {
        return 1;
    }

    @Override
    protected int getMaxValue() {
        return 7;
    }

    @Override
    protected AsyncNODao<?> getDao() {
        return NewsDao.getInstance(getContext().getContentResolver());
    }

    @Override
    protected int getTitleResId() {
        return R.string.news_delete_interval_header;
    }
}
