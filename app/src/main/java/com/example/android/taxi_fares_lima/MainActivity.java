package com.example.android.taxi_fares_lima;

import android.content.Context;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.LinkedHashMap;


public class MainActivity extends ActionBarActivity implements AsyncResponse {

    private static final String API_KEY = "AIzaSyCHF6yn8iOPhT9G8LzcVaO9JO_1uD5ICvA";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String LOG_TAG = "TRYINGHARD";
    private static final String GMAPS_BASE = "http://maps.googleapis.com/maps/api/directions/json?";


    public String rate = "";
    public String from_used = "";
    public String to_used = "";
    public String distance = "";
    public String duration = "";
    public String origin_address = "";
    public String destination_address = "";

    AutoCompleteTextView autoCompView1;
    AutoCompleteTextView autoCompView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button get_rate_button = (Button) findViewById(R.id.get_rate_button);
        String[] poi = getResources().getStringArray(R.array.poi_array);


        autoCompView1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        autoCompView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);

        autoCompView1.setVisibility(View.GONE);
        autoCompView2.setVisibility(View.GONE);

        autoCompView1.setThreshold(3);
        autoCompView2.setThreshold(3);

        autoCompView1.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                //origin_address = selection;
                Toast.makeText(getApplicationContext(), selection, Toast.LENGTH_LONG).show();

            }
        });

        autoCompView2.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                //destination_address = selection;
                Toast.makeText(getApplicationContext(), selection, Toast.LENGTH_LONG).show();

            }
        });


        ArrayAdapter<String> stringArrayAdapter= new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        poi) ;

        final MySpinner origin_spinner=
                (MySpinner)findViewById(R.id.origin_spinner);

        final MySpinner destination_spinner=
                (MySpinner)findViewById(R.id.destination_spinner);

        origin_spinner.setAdapter(stringArrayAdapter);
        destination_spinner.setAdapter(stringArrayAdapter);

        /*origin_spinner.setOnItemSelectedEvenIfUnchangedListener(new AdapterView.OnItemSelectedListener() {

             @Override
             public void onItemSelected(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                 autoCompView1.setVisibility(View.VISIBLE);
                 autoCompView1.setVisibility(View.GONE);
                 autoCompView2.setVisibility(View.GONE);
                 Log.w("jj", "siiip");
             }

             @Override
             public void onNothingSelected(AdapterView<?> arg0) {
                 autoCompView1.setVisibility(View.VISIBLE);
                 autoCompView1.setVisibility(View.GONE);
                 autoCompView2.setVisibility(View.GONE);
                 Log.w("jj", "noooop");
             }
         });*/

        get_rate_button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View v){

            rate = "";
            from_used = "";
            to_used = "";
            distance = "";
            duration = "";
            origin_address = autoCompView1.getText().toString();
            destination_address = autoCompView2.getText().toString();

            String[] keys = getResources().getStringArray(R.array.poi_array);
            String[] values = getResources().getStringArray(R.array.address_array);
            LinkedHashMap<String,String> myMap = new LinkedHashMap<String,String>();
            for (int i = 0; i < Math.min(keys.length, values.length); ++i) {
                myMap.put(keys[i], values[i]);
                Log.w("de map", keys[i] + "," + values[i]);
            }

            String origin_poi = origin_spinner.getSelectedItem().toString();
            String origin_poi_for_hm = origin_poi.toLowerCase().replace(" ", "_");
            String destination_poi = destination_spinner.getSelectedItem().toString();

            Log.w("jj", origin_poi);
            Log.w("jj", destination_poi);
            Log.w("jff", origin_address);
            Log.w("jkk", destination_address);

            if (!origin_poi.startsWith("Select") && !destination_poi.startsWith("Select")) {

                String origin_to_target = origin_poi_for_hm + "_hashmap";

                int identifier = getResources().getIdentifier(origin_to_target, "array", getPackageName());
                String[] relevant_hashstring = getResources().getStringArray(identifier);
                HashMap<String, String> relevant_hashmap = new HashMap<String, String>();

                for (int i = 0; i < relevant_hashstring.length; i = i + 2) {
                    relevant_hashmap.put(relevant_hashstring[i], relevant_hashstring[i + 1]);
                }
                rate = relevant_hashmap.get(destination_poi);
                distance = rate;

                Intent i = new Intent(getApplicationContext(), ResponseScreen.class);
                i.putExtra("distance", distance);
                startActivity(i);

            }

            else if (!origin_poi.startsWith("Select") && destination_poi.startsWith("Select")) {

                Log.w("zzzzz", "tot hier");
                String origin_poi_address = myMap.get(origin_poi);
                Log.w("jj", origin_poi_address);
                Log.w("jqqq", destination_address);
                get_google_places(origin_poi_address, destination_address);

            }

            else if (!destination_poi.startsWith("Select") && origin_poi.startsWith("Select")) {

                String destination_poi_address = myMap.get(destination_poi);
                get_google_places(origin_address, destination_poi_address);

            }

            else {

                get_google_places(origin_address, destination_address);

            }

            }

        });

    }

    public void get_google_places(String from, String to) {
        Log.w("jnaaaa", from);
        Log.w("jnaaaaa", to);

        String uri = Uri.parse(GMAPS_BASE)
                .buildUpon()
                .appendQueryParameter("origin", from)
                .appendQueryParameter("destination", to)
                .appendQueryParameter("sensor", "val")
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("region", "pe")
                .build().toString();


        AsyncTaskIO IO_Getter = new AsyncTaskIO(new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.w("jn", output);
                parse_JSON_response(output);
                Intent i = new Intent(getApplicationContext(), ResponseScreen.class);
                i.putExtra("distance", distance);
                i.putExtra("duration", duration);
                i.putExtra("from_used", from_used);
                i.putExtra("to_used", to_used);
                startActivity(i);
            }
        });
        IO_Getter.execute(uri);

    }

    public void parse_JSON_response(String JSON_response) {

        try {

            JSONObject obj = new JSONObject(JSON_response);
            JSONObject temp = obj.getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("legs")
                    .getJSONObject(0);

            from_used = temp.get("start_address").toString();
            to_used = temp.get("end_address").toString();
            duration = temp.getJSONObject("duration").get("value").toString();
            distance = temp.getJSONObject("distance").get("value").toString();

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);


        }
    }


    public void toggle_contents(View v){
        String temp = v.getTag().toString();
        if ( temp.equals("1")) {
            if(autoCompView1.getVisibility() == View.VISIBLE){
                //Fx.slide_up(this, autoCompView1);
                autoCompView1.setVisibility(View.GONE);
            }
            else{
                autoCompView1.setVisibility(View.VISIBLE);
                //Fx.slide_down(this, autoCompView1);
            }
        }
        else {
            if (autoCompView2.getVisibility() == View.VISIBLE) {
                //Fx.slide_up(this, autoCompView2);
                autoCompView2.setVisibility(View.GONE);
            } else {
                autoCompView2.setVisibility(View.VISIBLE);
                //Fx.slide_down(this, autoCompView2);
            }
        }
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





