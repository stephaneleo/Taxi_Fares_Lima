package com.example.android.taxi_fares_lima;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


public class ResponseScreen extends ActionBarActivity{

    final static int START_FEE = 4;
    final static double PER_KM_UNDER_5 = 1.2;
    final static double PER_KM_UNDER_10 = 1.8;
    final static double PER_KM_OVER_10 = 2.4;
    final static double DURATION_FACTOR = 1.2;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsescreen);

        TextView rate_value = (TextView) findViewById(R.id.rate_value);
        TextView distance_value = (TextView) findViewById(R.id.distance_value);
        TextView duration_value = (TextView) findViewById(R.id.duration_value);
        TextView from_value = (TextView) findViewById(R.id.from_value);
        TextView to_value = (TextView) findViewById(R.id.to_value);

        Intent i = getIntent();


        if (i.hasExtra("rate")) { // comes from db
            rate_value.setText(String.valueOf(i.getDoubleExtra("rate", 0.00)) + " Soles");
            distance_value.setText(String.valueOf(i.getDoubleExtra("distance", 0.00)) + " km");
            duration_value.setText(String.valueOf(i.getDoubleExtra("duration", 0.00)) + " min");
            //from_value.setText(i.getStringExtra("from_matrix"));
            //to_value.setText(i.getStringExtra("to_matrix"));
        }
        else {

            String distance_str = i.getStringExtra("distance");
            String duration_str = i.getStringExtra("duration");
            double distance_double = Double.parseDouble(distance_str) / 1000; //in km
            double duration_double = Double.parseDouble(duration_str) * DURATION_FACTOR / 60; //in min
            String from_used = i.getStringExtra("from_used");
            String to_used = i.getStringExtra("to_used");

            rate_value.setText(String.format("%.1f", calculate_rate(distance_double)) + " Soles");
            distance_value.setText(String.format("%.2f", distance_double) + " km");
            duration_value.setText(String.format("%.1f", duration_double) + " min");
            from_value.setText(from_used);
            to_value.setText(to_used);
        }
    }


    private double calculate_rate(double dist) {
        double result;
        if (dist > 10) {
            result = START_FEE + 5*PER_KM_UNDER_5 + 5*PER_KM_UNDER_10 + (dist-10)*PER_KM_OVER_10;
        }
        else if (dist > 5) {
            result = START_FEE + 5*PER_KM_UNDER_5 + (dist-5)*PER_KM_UNDER_10;
        }
        else {
            result = START_FEE + dist*PER_KM_UNDER_5;
        }
        return result;
    }



}
