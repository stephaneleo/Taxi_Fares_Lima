package com.example.android.taxi_fares_lima;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by root on 24/04/15.
 */
public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }


}
