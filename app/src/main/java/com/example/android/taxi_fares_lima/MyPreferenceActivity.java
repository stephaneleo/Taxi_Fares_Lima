package com.example.android.taxi_fares_lima;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by root on 24/04/15.
 */
public class MyPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content (without headers/grouping).
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new MyPreferenceFragment()).commit();

    }
}
