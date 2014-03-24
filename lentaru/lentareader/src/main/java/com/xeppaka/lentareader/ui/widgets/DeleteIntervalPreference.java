package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.async.AsyncListener;
import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.Rubrics;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.async.AsyncNODao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by kacpa01 on 1/3/14.
 */
public abstract class DeleteIntervalPreference extends DialogPreference {
    private NumberPicker daysPicker;
    private int deleteAfterDays;

    public DeleteIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.delete_interval_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        deleteAfterDays = getDeleteAfterDaysDefault();

        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {
        final View view = super.onCreateDialogView();

        daysPicker = (NumberPicker)view.findViewById(R.id.delete_number_picker);
        daysPicker.setMaxValue(getMaxValue());
        daysPicker.setMinValue(getMinValue());
        daysPicker.setValue(deleteAfterDays);

        final TextView titleView = (TextView) view.findViewById(R.id.delete_news_object_title);
        titleView.setText(getTitleResId());

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            final int oldValue = deleteAfterDays;
            persistInt(deleteAfterDays = daysPicker.getValue());

            if (deleteAfterDays > oldValue) {
                final AsyncNODao<?> dao = getDao();
                dao.setUpdatedFromLatestFlagAsync(Rubrics.LATEST, new AsyncListener<Integer>() {
                    @Override
                    public void onSuccess(Integer rowsUpdated) {
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });
            }

            updateSummaryText();
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            deleteAfterDays = this.getPersistedInt(getDeleteAfterDaysDefault());
        } else {
            // Set default state from the XML attribute
            deleteAfterDays = (Integer) defaultValue;
            persistInt(deleteAfterDays);
        }

        updateSummaryText();
    }

    public int getDeleteAfterDays() {
        return deleteAfterDays;
    }

    protected abstract int getMinValue();
    protected abstract int getMaxValue();
    protected abstract int getDeleteAfterDaysDefault();
    protected abstract void updateSummaryText();
    protected abstract AsyncNODao<?> getDao();
    protected abstract int getTitleResId();
}
