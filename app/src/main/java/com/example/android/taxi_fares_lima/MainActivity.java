package com.example.android.taxi_fares_lima;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements AsyncResponse {

    private static final String API_KEY = "AIzaSyCHF6yn8iOPhT9G8LzcVaO9JO_1uD5ICvA";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String LOG_TAG = "TRYINGHARD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoCompleteTextView autoCompView1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        AutoCompleteTextView autoCompView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);

        autoCompView1.setThreshold(3);
        autoCompView2.setThreshold(3);


        autoCompView1.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                //TODO Do something with the selected text
                Toast.makeText(getApplicationContext(), selection, Toast.LENGTH_LONG).show();

            }
        });

        autoCompView2.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                //TODO Do something with the selected text
                Toast.makeText(getApplicationContext(), selection, Toast.LENGTH_LONG).show();

            }
        });

        String[] poi = getResources().getStringArray(R.array.poi_array);

        final TextView normal_rate = (TextView) findViewById(R.id.normal_rate);
        Button get_rate_button = (Button) findViewById(R.id.get_rate_button);
        final String LOG_TAG = "IN MAIN";
        final String GMAPS_BASE = "http://maps.googleapis.com/maps/api/directions/json?";

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
                /*String origin = origin_spinner.getSelectedItem().toString().toLowerCase().replace(" ", "_");
                String destination = destination_spinner.getSelectedItem().toString();

                String origin_to_target = origin + "_hashmap";

                int identifier = getResources().getIdentifier(origin_to_target, "array", getPackageName());
                String[] relevant_hashstring = getResources().getStringArray(identifier);
                HashMap<String, String> relevant_hashmap = new HashMap<String, String>();
                for (int i=0; i<relevant_hashstring.length; i=i+2) {
                    relevant_hashmap.put(relevant_hashstring[i], relevant_hashstring[i+1]);
                    normal_rate.setText(relevant_hashmap.get(destination));*/


                AutoCompleteTextView origin_address_field = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
                AutoCompleteTextView destination_address_field = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);

                String origin_address = origin_address_field.getText().toString();
                String destination_address = destination_address_field.getText().toString();

                String uri = Uri.parse(GMAPS_BASE)
                        .buildUpon()
                        .appendQueryParameter("origin", origin_address)
                        .appendQueryParameter("destination", destination_address)
                        .appendQueryParameter("sensor", "val")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("region", "pe")
                        .build().toString();


                AsyncTaskIO IO_Getter = new AsyncTaskIO(new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        Log.w("jn", output);
                    }
                });
                IO_Getter.execute(uri);

            }

        });


    }

    public void processFinish(String output){
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


    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:pe");
            sb.append("&location=-12.108880,-77.029276");
            sb.append("&radius=20000");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}





