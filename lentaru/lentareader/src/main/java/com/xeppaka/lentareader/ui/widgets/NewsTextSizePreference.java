package com.xeppaka.lentareader.ui.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.utils.LentaTextUtils;
import com.xeppaka.lentareader.utils.PreferencesConstants;

/**
 * Created by nnm on 1/3/14.
 */
public class NewsTextSizePreference extends DialogPreference {
    private static final int NEWS_FULL_TEXT_SIZE_MIN = 8;
    private static final int NEWS_FULL_TEXT_SIZE_MAX = 30;
    private TextView exampleText;
    private SeekBar seekBar;
    private int textSize;

    public NewsTextSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.news_text_size_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {
        final View view = super.onCreateDialogView();

        if (textSize == 0) {
            if (PreferencesConstants.PREF_KEY_NEWS_LIST_TEXT_SIZE.equals(getKey())) {
                textSize = PreferencesConstants.NEWS_LIST_TEXT_SIZE_DEFAULT;
            } else {
                textSize = PreferencesConstants.NEWS_FULL_TEXT_SIZE_DEFAULT;
            }
        }

        seekBar = (SeekBar)view.findViewById(R.id.news_text_size_seek_bar);
        seekBar.setMax(NEWS_FULL_TEXT_SIZE_MAX - NEWS_FULL_TEXT_SIZE_MIN);
        seekBar.setProgress(textSize - NEWS_FULL_TEXT_SIZE_MIN);
        exampleText = (TextView)view.findViewById(R.id.news_text_size_example);

        if (PreferencesConstants.PREF_KEY_NEWS_LIST_TEXT_SIZE.equals(getKey())) {
            exampleText.setTextSize(LentaTextUtils.getNewsListTitleTextSize(textSize));
        } else {
            exampleText.setTextSize(LentaTextUtils.getNewsFullTextSize(textSize));
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textSize = seekBar.getProgress() + NEWS_FULL_TEXT_SIZE_MIN;

                if (PreferencesConstants.PREF_KEY_NEWS_LIST_TEXT_SIZE.equals(getKey())) {
                    exampleText.setTextSize(LentaTextUtils.getNewsListTitleTextSize(textSize));
                } else {
                    exampleText.setTextSize(LentaTextUtils.getNewsFullTextSize(textSize));
                }
            }
        });

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistInt(textSize = seekBar.getProgress() + NEWS_FULL_TEXT_SIZE_MIN);

            updateUIText();
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            if (PreferencesConstants.PREF_KEY_NEWS_LIST_TEXT_SIZE.equals(getKey())) {
                textSize = this.getPersistedInt(PreferencesConstants.NEWS_LIST_TEXT_SIZE_DEFAULT);
            } else {
                textSize = this.getPersistedInt(PreferencesConstants.NEWS_FULL_TEXT_SIZE_DEFAULT);
            }
        } else {
            // Set default state from the XML attribute
            textSize = (Integer) defaultValue;
            persistInt(textSize);
        }

        updateUIText();
    }

    private void updateUIText() {
        final Resources resources = getContext().getResources();
        if (PreferencesConstants.PREF_KEY_NEWS_LIST_TEXT_SIZE.equals(getKey())) {
            setSummary(String.format(resources.getString(R.string.news_list_text_size_summary), textSize));
        } else {
            setSummary(String.format(resources.getString(R.string.news_full_text_size_summary), textSize));
        }
    }
}
