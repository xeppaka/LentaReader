package com.xeppaka.lentareader.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.ui.fragments.LentaPreferencesFragment;

/**
 * Created by kacpa01 on 12/30/13.
 */
public class PreferencesActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_activity);

        getFragmentManager().beginTransaction().replace(R.id.preferences_fragment_container, new LentaPreferencesFragment()).commit();
    }
}
