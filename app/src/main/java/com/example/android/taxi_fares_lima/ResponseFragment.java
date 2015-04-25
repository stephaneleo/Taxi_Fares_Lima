package com.example.android.taxi_fares_lima;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ResponseFragment extends Fragment {
    static final String DETAIL_URI = "URI";
    final static int START_FEE = 4;
    final static double PER_KM_UNDER_5 = 1.2;
    final static double PER_KM_UNDER_10 = 1.8;
    final static double PER_KM_OVER_10 = 2.4;
    final static double DURATION_FACTOR = 1.2;
    private static final String LOG_TAG = ResponseFragment.class.getSimpleName();
    TextView rate_value;
    TextView distance_value;
    TextView duration_value;
    TextView from_value;
    TextView to_value;
    private Uri mUri;

    public ResponseFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String rate = null;
        String distance = null;
        String duration= null;
        String from= null;
        String to= null;

        View rootView = inflater.inflate(R.layout.fragment_response, container, false);

        rate_value = (TextView) rootView.findViewById(R.id.rate_value);
        distance_value = (TextView) rootView.findViewById(R.id.distance_value);
        duration_value = (TextView) rootView.findViewById(R.id.duration_value);
        from_value = (TextView) rootView.findViewById(R.id.from_value);
        to_value = (TextView) rootView.findViewById(R.id.to_value);

        if(savedInstanceState!=null) //rotation ??
        {
            rate = savedInstanceState.getString("rate");
            distance = savedInstanceState.getString("distance");
            duration = savedInstanceState.getString("duration");
            from = savedInstanceState.getString("from");
            to = savedInstanceState.getString("to");

            updateView(rate, distance, duration, from, to);
        }


        else {  //in one-pane activities
            Bundle arguments = getArguments();
            if (arguments != null) {
                rate = arguments.getString("rate");
                distance = arguments.getString("distance");
                duration = arguments.getString("duration");
                from = arguments.getString("from");
                to = arguments.getString("to");

                updateView(rate, distance, duration, from, to);
            }
        }


        return rootView;
    }

    public void updateView(String rate, String distance, String duration, String from, String to) {

        try {
        double distance_double = Double.parseDouble(distance);
        double duration_double = Double.parseDouble(duration);

        distance = String.format("%.2f", distance_double);
        duration = String.format("%.1f", duration_double);}
        catch (NumberFormatException|NullPointerException e) {
        }



        if (rate.equals("") && !distance.equals("")) {
            double rate_double = calculate_rate(Double.parseDouble(distance));
            rate = String.format("%.1f", rate_double);
        }
        
        rate_value.setText(rate);
        distance_value.setText(distance);
        duration_value.setText(duration);
        from_value.setText(from);
        to_value.setText(to);

    };


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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("rate", rate_value.getText().toString());
        outState.putString("distance", distance_value.getText().toString());
        outState.putString("duration", duration_value.getText().toString());
        outState.putString("from", from_value.getText().toString());
        outState.putString("to", to_value.getText().toString());
    }
}
