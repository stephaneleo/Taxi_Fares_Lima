package com.example.android.taxi_fares_lima;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class ResponseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);

        Bundle i = this.getIntent().getExtras();
         String rate = i.getString("rate");
         String distance = i.getString("distance");
         String duration = i.getString("duration");
         String from = i.getString("from");
         String to = i.getString("to");

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putString("rate", rate);
            arguments.putString("distance", distance);
            arguments.putString("duration", duration);
            arguments.putString("from", from);
            arguments.putString("to", to);

            ResponseFragment fragment = new ResponseFragment();
            fragment.setArguments(arguments); //arguments from intent passed on

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.response_container, fragment)
                    .commit();
        }







    }

}
