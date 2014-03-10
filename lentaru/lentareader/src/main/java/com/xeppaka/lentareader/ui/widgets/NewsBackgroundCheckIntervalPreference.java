package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.utils.PreferencesConstants;

import java.util.Arrays;

/**
 * Created by kacpa01 on 1/3/14.
 */
public class NewsBackgroundCheckIntervalPreference extends DialogPreference {
    private NumberPicker minutesPicker;
    private int backgroundCheckMinutes = PreferencesConstants.NEWS_BACKGROUND_CHECK_MINUTES_DEFAULT;

    private int[] values = new int[]{5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115, 120};

    public NewsBackgroundCheckIntervalPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.news_background_check_interval_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {
        final View view = super.onCreateDialogView();

        final String[] displayedValues = new String[values.length];

        for (int i = 0; i < values.length; ++i) {
            displayedValues[i] = String.valueOf(values[i]);
        }

        minutesPicker = (NumberPicker)view.findViewById(R.id.news_background_check_interval_number_picker);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(values.length - 1);
        minutesPicker.setDisplayedValues(displayedValues);
        minutesPicker.setValue(Arrays.binarySearch(values, backgroundCheckMinutes));

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(backgroundCheckMinutes = values[minutesPicker.getValue()]);

            updateSummaryText();
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            backgroundCheckMinutes = this.getPersistedInt(PreferencesConstants.NEWS_BACKGROUND_CHECK_MINUTES_DEFAULT);
        } else {
            // Set default state from the XML attribute
            backgroundCheckMinutes = (Integer) defaultValue;
            persistInt(backgroundCheckMinutes);
        }

        updateSummaryText();
    }

    private void updateSummaryText() {
        final Resources resources = getContext().getResources();
        setSummary(String.format(resources.getString(R.string.pref_news_background_check_interval_summary), backgroundCheckMinutes));
    }
}
