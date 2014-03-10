package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by kacpa01 on 1/3/14.
 */
public class NewsDeleteIntervalPreference extends DialogPreference {
    private NumberPicker daysPicker;
    private int deleteAfterDays = PreferencesConstants.NEWS_DELETE_NEWS_DAYS_DEFAULT;

    public NewsDeleteIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.news_delete_interval_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {
        final View view = super.onCreateDialogView();

        daysPicker = (NumberPicker)view.findViewById(R.id.news_delete_number_picker);
        daysPicker.setMaxValue(7);
        daysPicker.setMinValue(1);
        daysPicker.setValue(deleteAfterDays);

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            final int oldValue = deleteAfterDays;
            persistInt(deleteAfterDays = daysPicker.getValue());

            if (deleteAfterDays > oldValue) {
                final AsyncNODao<News> nd = NewsDao.getInstance(getContext().getContentResolver());
                nd.setLatestFlagAsync(Rubrics.LATEST, new AsyncListener<Integer>() {
                    @Override
                    public void onSuccess(Integer rowsUpdated) {}

                    @Override
                    public void onFailure(Exception e) {}
                });
            }

            updateSummaryText();
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            deleteAfterDays = this.getPersistedInt(PreferencesConstants.NEWS_DELETE_NEWS_DAYS_DEFAULT);
        } else {
            // Set default state from the XML attribute
            deleteAfterDays = (Integer) defaultValue;
            persistInt(deleteAfterDays);
        }

        updateSummaryText();
    }

    private void updateSummaryText() {
        final Resources resources = getContext().getResources();
        if (deleteAfterDays == 1) {
            setSummary(String.format(resources.getString(R.string.pref_news_deletion_interval_summary_day), deleteAfterDays));
        } else {
            setSummary(String.format(resources.getString(R.string.pref_news_deletion_interval_summary_days), deleteAfterDays));
        }
    }
}
