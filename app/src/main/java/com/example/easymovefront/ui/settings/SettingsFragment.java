package com.example.easymovefront.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.easymovefront.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.layout.activity_settings, rootKey);
    }
}

