package com.xeppaka.lentareader.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.xeppaka.lentareader.R;

/**
 * Created by kacpa01 on 12/30/13.
 */
public class LentaPreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
