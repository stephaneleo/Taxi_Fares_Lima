package com.example.android.taxi_fares_lima;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity implements MainFragment.OnCalculatedListener {

    private static final String DETAILFRAGMENT_TAG = "DFTAG_2_PANE";
    public boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.response_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.response_container, new ResponseFragment(), DETAILFRAGMENT_TAG)
                        //.addToBackStack(null)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    public void onCalculated(String rate, String distance, String duration, String from, String to) {
        ResponseFragment resFrag = (ResponseFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);


        if (resFrag != null) {
            // If article frag is available, we're in two-pane layout...
            // Call a method in the ResponseFragment to update its content
            resFrag.updateView(rate, distance, duration, from, to);
        }

        else {
            // start Response activity
            Intent i = new Intent(this, ResponseActivity.class);
            i.putExtra("rate", rate);
            i.putExtra("distance", distance);
            i.putExtra("duration", duration);
            i.putExtra("from", from);
            i.putExtra("to", to);
            startActivity(i);
        }
    };

}





