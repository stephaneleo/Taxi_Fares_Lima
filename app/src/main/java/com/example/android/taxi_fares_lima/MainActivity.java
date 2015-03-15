package com.example.android.taxi_fares_lima;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] poi = getResources().getStringArray(R.array.poi_array);
        EditText origin_address = (EditText) findViewById(R.id.origin_address);
        EditText destination_address = (EditText) findViewById(R.id.destination_address);
        final TextView normal_rate = (TextView) findViewById(R.id.normal_rate);
        Button get_rate_button = (Button) findViewById(R.id.get_rate_button);
        final String LOG_TAG = "IN MAIN";


        ArrayAdapter<String> stringArrayAdapter=
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        poi) ;

        final Spinner origin_spinner=
                (Spinner)findViewById(R.id.origin_spinner);

        final Spinner destination_spinner=
                (Spinner)findViewById(R.id.destination_spinner);

        origin_spinner.setAdapter(stringArrayAdapter);
        destination_spinner.setAdapter(stringArrayAdapter);

        get_rate_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String origin = origin_spinner.getSelectedItem().toString().toLowerCase().replace(" ", "_");
                String destination = destination_spinner.getSelectedItem().toString();

                String origin_to_target = origin + "_hashmap";

                int identifier = getResources().getIdentifier(origin_to_target, "array", getPackageName());
                String[] relevant_hashstring = getResources().getStringArray(identifier);
                HashMap<String, String> relevant_hashmap = new HashMap<String, String>();
                for (int i=0; i<relevant_hashstring.length; i=i+2) {
                    relevant_hashmap.put(relevant_hashstring[i], relevant_hashstring[i+1]);
                    normal_rate.setText(relevant_hashmap.get(destination));
            }
        }
        }); //extra comment





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
