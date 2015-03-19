package com.example.android.taxi_fares_lima;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * Created by root on 18/03/15.
 */
public class ResponseScreen extends ActionBarActivity{

    final static int START_FEE = 4;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsescreen);

        TextView response_title = (TextView) findViewById(R.id.response_title);
        TextView rate_value = (TextView) findViewById(R.id.rate_value);
        TextView distance_value = (TextView) findViewById(R.id.distance_value);
        TextView duration_value = (TextView) findViewById(R.id.duration_value);

        Intent i = getIntent();
        String distance = i.getStringExtra("distance");
        String duration = i.getStringExtra("duration");
        String from_used = i.getStringExtra("from_used");
        String to_used = i.getStringExtra("to_used");

        distance_value.setText(distance);
        duration_value.setText(duration);


    }



}
