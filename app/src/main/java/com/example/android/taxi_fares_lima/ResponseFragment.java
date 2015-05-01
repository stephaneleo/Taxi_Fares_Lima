package com.example.android.taxi_fares_lima;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;


public class ResponseFragment extends Fragment {
    final static int START_FEE = 4;
    final static double PER_KM_UNDER_5 = 1.2;
    final static double PER_KM_UNDER_10 = 1.8;
    final static double PER_KM_OVER_10 = 2.4;
    final static double USDPEN = 3.12; //todo : fetch them periodically
    final static double EURPEN = 3.45; //todo : fetch them periodically
    TextView min_rate_value;
    TextView max_rate_value;
    TextView distance_value;
    TextView duration_value;
    TextView from_value;
    TextView to_value;
    TextView currency_text;
    View rootView;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    String current_currency;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String rate = null;
        String distance = null;
        String duration= null;
        String from= null;
        String to= null;

        rootView = inflater.inflate(R.layout.fragment_response, container, false);

        min_rate_value = (TextView) rootView.findViewById(R.id.min_rate_value);
        max_rate_value = (TextView) rootView.findViewById(R.id.max_rate_value);
        distance_value = (TextView) rootView.findViewById(R.id.distance_value);
        duration_value = (TextView) rootView.findViewById(R.id.duration_value);
        from_value = (TextView) rootView.findViewById(R.id.from_value);
        to_value = (TextView) rootView.findViewById(R.id.to_value);
        currency_text = (TextView) rootView.findViewById(R.id.rate_unit);

        if(savedInstanceState!=null) //rotation ??
        {
           min_rate_value.setText(savedInstanceState.getString("min_rate"));
            max_rate_value.setText(savedInstanceState.getString("max_rate"));
            distance_value.setText(savedInstanceState.getString("distance"));
           duration_value.setText(savedInstanceState.getString("duration"));
           from_value.setText(savedInstanceState.getString("from"));
           to_value.setText(savedInstanceState.getString("to"));

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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        current_currency = prefs.getString("CURRENCY", "PEN");
        if (current_currency.equals("USD")) {
            currency_text.setText("USD");
        }
        else if (current_currency.equals("EUR")) {
            currency_text.setText("Euro");
        }
        else {
            currency_text.setText("Soles");
        }

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                String new_cur = prefs.getString(key, "PEN");
                double current_min_rate = 0.0;
                double current_max_rate = 0.0;

                try {current_min_rate = Double.parseDouble(min_rate_value.getText().toString());
                    current_max_rate = Double.parseDouble(max_rate_value.getText().toString());}
                catch(NumberFormatException|NullPointerException e) {};

                if (current_min_rate != 0.0) {
                    if (new_cur.equals("USD") && current_currency.equals("EUR"))
                    {min_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_min_rate*EURPEN/USDPEN));
                        max_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_max_rate*EURPEN/USDPEN));
                    }
                    else if (new_cur.equals("EUR") && current_currency.equals("USD"))
                    {min_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_min_rate/EURPEN*USDPEN));
                        max_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_max_rate/EURPEN*USDPEN));
                    }
                    else if (new_cur.equals("USD") && current_currency.equals("PEN"))
                    {min_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_min_rate/USDPEN));
                        max_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_max_rate/USDPEN));
                    }
                    else if (new_cur.equals("PEN") && current_currency.equals("USD"))
                    {min_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_min_rate*USDPEN));
                        max_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_max_rate*USDPEN));
                    }
                    else if (new_cur.equals("EUR") && current_currency.equals("PEN"))
                    {min_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_min_rate/EURPEN));
                        max_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_max_rate/EURPEN));
                    }
                    else if (new_cur.equals("PEN") && current_currency.equals("EUR"))
                    {min_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_min_rate*EURPEN));
                        max_rate_value.setText(String.format(Locale.ENGLISH,"%.1f",current_max_rate*EURPEN));
                    }
                }


                if (new_cur.equals("USD")) {
                    currency_text.setText("USD");
                    current_currency = "USD";
                }
                else if (new_cur.equals("EUR")) {
                    currency_text.setText("Euro");
                    current_currency = "EUR";
                }
                else {
                    currency_text.setText("Soles");
                    current_currency = "PEN";

                }



            }
        };

        prefs.registerOnSharedPreferenceChangeListener(listener);

        Bundle arguments = getArguments();
        if (arguments != null) {
            setHasOptionsMenu(true);
        }

        return rootView;
    }

    public void updateView(String rate, String distance, String duration, String from, String to) {

        String duration_min;
        String duration_max;
        double rate_double = 0.0;

        try {
        double distance_double = Double.parseDouble(distance);
        double duration_double = Double.parseDouble(duration);

        distance = String.format(Locale.ENGLISH,"%.1f", distance_double);
        duration_min = String.format(Locale.ENGLISH,"%.0f", duration_double*0.95);
        duration_max = String.format(Locale.ENGLISH,"%.0f", duration_double*1.25);
        duration = duration_min + "-" + duration_max;}

        catch (NumberFormatException|NullPointerException e) {
        }

        if (rate.equals("") && !distance.equals("")) {
            rate_double = calculate_rate(Double.parseDouble(distance));

        } else {
            try{rate_double = Double.parseDouble(rate);}
            catch (NumberFormatException|NullPointerException e) {};
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String cur = prefs.getString("CURRENCY", "PEN");


        if (cur.equals("USD")) {
            rate_double = rate_double/USDPEN;
            currency_text.setText("USD");
                }
        else if (cur.equals("EUR")) {
            rate_double = rate_double/EURPEN;
            currency_text.setText("Euro");
        }
        else {
            currency_text.setText("Soles");
        }

        double min_rate_double = rate_double*0.95;
        double max_rate_double = rate_double*1.2;
        String min_rate = String.format(Locale.ENGLISH,"%.1f", min_rate_double);
        String max_rate = String.format(Locale.ENGLISH,"%.1f", max_rate_double);


        min_rate_value.setText(min_rate);
        max_rate_value.setText(max_rate);
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
        outState.putString("min_rate", min_rate_value.getText().toString());
        outState.putString("max_rate", max_rate_value.getText().toString());
        outState.putString("distance", distance_value.getText().toString());
        outState.putString("duration", duration_value.getText().toString());
        outState.putString("from", from_value.getText().toString());
        outState.putString("to", to_value.getText().toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.preferences) {
            Intent intent = new Intent(getActivity().getApplicationContext(), MyPreferenceActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
