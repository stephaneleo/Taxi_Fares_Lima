package com.example.android.taxi_fares_lima;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.taxi_fares_lima.data.TaxiContract;

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
import java.util.List;


public class MainActivity extends ActionBarActivity implements AsyncResponse {

    // These indices are tied to RATE_COLUMNS_PROJECTION.  If RATE_COLUMNS changes, these
    // must change.
    static final int COLNR_RATE_ID = 0;
    static final int COLNR_RATE_FROM_ID = 1;
    static final int COLNR_RATE_TO_ID = 2;
    static final int COLNR_RATE_RATE = 3;
    static final int COLNR_RATE_DIST = 4;
    static final int COLNR_RATE_DUR = 5;
    static final int COLNR_POI_ID = 0;
    static final int COLNR_POI_NAME_ES = 1;
    static final int COLNR_POI_NAME_EN = 2;
    static final int COLNR_POI_ADDRESS = 3;
    private static final String API_KEY = "AIzaSyCHF6yn8iOPhT9G8LzcVaO9JO_1uD5ICvA";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String LOG_TAG = "TRYINGHARD";
    private static final String GMAPS_BASE = "http://maps.googleapis.com/maps/api/directions/json?";
    Spinner origin_spinner;
    Spinner destination_spinner;
    AutoCompleteTextView autoCompView1;
    AutoCompleteTextView autoCompView2;
    private List<String> poi;
    private String rate = "";
    private String from_used = "";
    private String to_used = "";
    private String distance = "";
    private String duration = "";
    private String origin_address = "";
    private String destination_address = "";
    private boolean origin_poi_selected = false;
    private boolean destination_poi_selected = false;
    private boolean origin_address_selected = false;
    private boolean destination_address_selected = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        poi = getPoiList();

        Button get_rate_button = (Button) findViewById(R.id.get_rate_button);
        origin_spinner= (Spinner)findViewById(R.id.origin_spinner);
        destination_spinner= (Spinner)findViewById(R.id.destination_spinner);

        // check if any item has already been selected in the spinner
        if (origin_spinner.getSelectedItemPosition() > 0) {
            origin_poi_selected = true;
        }
        if (destination_spinner.getSelectedItemPosition() > 0) {
            destination_poi_selected = true;
        }

        autoCompView1 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        autoCompView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);

        // sets the backgrounds of correctly filled fields to green
        setBackgrounds();

        // determines the minimal number of letters for autocomplete to launch
        autoCompView1.setThreshold(4);
        autoCompView2.setThreshold(4);

        autoCompView1.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompView1.getWindowToken(), 0);
                origin_spinner.setSelection(0);
                origin_poi_selected = false;
                origin_address_selected = true;
                setBackgrounds();
                Toast.makeText(getApplicationContext(), selection, Toast.LENGTH_SHORT).show();
                }
        });

        autoCompView2.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompView2.getWindowToken(), 0);
                destination_spinner.setSelection(0);
                destination_poi_selected = false;
                destination_address_selected = true;
                setBackgrounds();
                Toast.makeText(getApplicationContext(), selection, Toast.LENGTH_SHORT).show();

            }
        });


        ArrayAdapter<String> stringArrayAdapter= new ArrayAdapter<String>(
                        this,
                        R.layout.spinner_item,
                        poi) ;

        origin_spinner.setAdapter(stringArrayAdapter);
        destination_spinner.setAdapter(stringArrayAdapter);

        origin_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

             @Override
             public void onItemSelected(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                 if (arg2 != 0){
                 origin_poi_selected = true;
                     origin_address_selected = false;
                     autoCompView1.setText("");
                ;}
              else {origin_poi_selected = false;}
             setBackgrounds();
             }

             @Override
             public void onNothingSelected(AdapterView<?> arg0) {
             }
         });

        destination_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (arg2 != 0){
                    destination_poi_selected = true;
                    destination_address_selected = false;
                    autoCompView2.setText("");
                    }
                else {destination_poi_selected = false;
                }
                setBackgrounds();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        get_rate_button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick (View v){

            String origin_selected_position = null;
            String destination_selected_position = null;

            origin_address = autoCompView1.getText().toString();
            destination_address = autoCompView2.getText().toString();

            origin_selected_position = Integer.toString(origin_spinner.getSelectedItemPosition());
            destination_selected_position = Integer.toString(destination_spinner.getSelectedItemPosition());

                if (origin_poi_selected && destination_poi_selected) {

                String[] selectionArgs = new String[]{origin_selected_position, destination_selected_position};
                Uri uri = TaxiContract.RateEntry.buildBasicRateUri();

                Cursor cursor = getContentResolver().query(
                        uri,   // The content URI -> indicates which provider/db/table to use
                        TaxiContract.RateEntry.COLUMNS,                        // The columns to return for each row
                        null,                    // Selection criteria
                        selectionArgs,                     // Selection criteria
                        null);                        // The sort order for the returned rows

                Double rate_to_pass = null;
                Double distance_to_pass = null;
                Double duration_to_pass = null;

                if (cursor.moveToFirst()){
                    do{
                        rate_to_pass = cursor.getDouble(COLNR_RATE_RATE);
                        distance_to_pass = cursor.getDouble(COLNR_RATE_DIST);
                        duration_to_pass = cursor.getDouble(COLNR_RATE_DUR);

                    }while(cursor.moveToNext());
                }

                cursor.close();

                Intent i = new Intent(getApplicationContext(), ResponseScreen.class);
                i.putExtra("rate", rate_to_pass);
                i.putExtra("distance", distance_to_pass);
                i.putExtra("duration", duration_to_pass);

                startActivity(i);

            }

            else if (origin_poi_selected && destination_address_selected) {


                get_google_places(getAddressFromPoi(origin_spinner.getSelectedItemPosition()), destination_address);

            }

            else if (origin_address_selected && destination_poi_selected) {

                get_google_places(origin_address, getAddressFromPoi(destination_spinner.getSelectedItemPosition()));

            }

            else if (origin_address_selected && destination_address_selected) {

                get_google_places(origin_address, destination_address);

            }

            else {

                Toast.makeText(getApplicationContext(), "Select please...." , Toast.LENGTH_LONG).show();
            }

            }

        });

    }

    public void get_google_places(String from, String to) {

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

    public void processFinish(String output){
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.w("menu", "inflated");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.w("Settings","Clicked");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setBackgrounds () {
        Log.w("bools", Boolean.toString(origin_address_selected) + Boolean.toString(destination_address_selected)
        + Boolean.toString(origin_poi_selected) + Boolean.toString(destination_poi_selected));
        if (origin_address_selected) {
            autoCompView1.setBackgroundColor(getResources().getColor(R.color.lightgreen));
        } else {autoCompView1.setBackgroundColor(getResources().getColor(R.color.lightblue));}
        if (destination_address_selected) {
            autoCompView2.setBackgroundColor(getResources().getColor(R.color.lightgreen));
        } else {autoCompView2.setBackgroundColor(getResources().getColor(R.color.lightblue));}
        if (origin_poi_selected) {
            origin_spinner.setBackgroundColor(getResources().getColor(R.color.lightgreen));
        } else {origin_spinner.setBackgroundColor(getResources().getColor(R.color.lightblue));}
        if (destination_poi_selected) {
            destination_spinner.setBackgroundColor(getResources().getColor(R.color.lightgreen));
        } else {destination_spinner.setBackgroundColor(getResources().getColor(R.color.lightblue));}
    }

    private List<String> getPoiList() {

        List<String> result = new ArrayList<String>();
        result.add(getText(R.string.spinner_header).toString());

        Uri uri = TaxiContract.PoiEntry.buildBasicPoiUri();

        Cursor cursor = getContentResolver().query(
                uri,   // The content URI -> indicates which provider/db/table to use
                TaxiContract.PoiEntry.COLUMNS,                        // The columns to return for each row
                null,                    // Selection criteria
                null,                     // Selection criteria
                null);                        // The sort order for the returned rows

        String poi_name = null;

        if (cursor.moveToFirst()){
            do{
                poi_name = cursor.getString(COLNR_POI_NAME_ES);
                result.add(poi_name);

            }while(cursor.moveToNext());
        }

        cursor.close();
        return result;
    }

    private String getAddressFromPoi(int index) {

        String result=null;
        index = index -1; //account for spinner's header

        Uri uri = TaxiContract.PoiEntry.buildPoiUri(index);

        Cursor cursor = getContentResolver().query(
                uri,   // The content URI -> indicates which provider/db/table to use
                TaxiContract.PoiEntry.COLUMNS,                        // The columns to return for each row
                null,                    // Selection criteria
                null,                     // Selection criteria
                null);                        // The sort order for the returned rows

        if (cursor.moveToFirst()){
            result = cursor.getString(COLNR_POI_ADDRESS);

        }

        cursor.close();
        return result;
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





