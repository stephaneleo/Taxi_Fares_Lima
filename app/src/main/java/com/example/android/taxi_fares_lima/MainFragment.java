package com.example.android.taxi_fares_lima;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Locale;


public class MainFragment extends Fragment implements AsyncResponse, LoaderManager.LoaderCallbacks<Cursor> {

    public static final int SPINNER_LOADER = 0; // Loader identifier for populating spinners
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
    final static double DURATION_FACTOR = 1;
    final static String LOG_TAG = "MainFragment";
    private static final String API_KEY = "AIzaSyCHF6yn8iOPhT9G8LzcVaO9JO_1uD5ICvA";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String GMAPS_BASE = "http://maps.googleapis.com/maps/api/directions/json?";
    SimpleCursorAdapter simpleCursorAdapter;
    Spinner origin_spinner;
    Spinner destination_spinner;
    AutoCompleteTextView autoCompView1;
    AutoCompleteTextView autoCompView2;
    Context myC;
    String lan;
    OnCalculatedListener mCallback;
    private List<String> poi;
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

    // method for address autocompletion
    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:pe");
            sb.append("&location=-12.108880,-77.029276"); //Lima coordinates
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        myC = getActivity().getApplicationContext();

        lan = Locale.getDefault().getLanguage();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        getLoaderManager().initLoader(SPINNER_LOADER, null, this);

        if(savedInstanceState!=null)
        {
            origin_poi_selected = savedInstanceState.getBoolean("origin_poi_selected");
            destination_poi_selected = savedInstanceState.getBoolean("destination_poi_selected");
            origin_address_selected = savedInstanceState.getBoolean("origin_address_selected");
            destination_address_selected = savedInstanceState.getBoolean("destination_address_selected");
        }

        Button get_rate_button = (Button) rootView.findViewById(R.id.get_rate_button);
        origin_spinner= (Spinner) rootView.findViewById(R.id.origin_spinner);
        destination_spinner= (Spinner) rootView.findViewById(R.id.destination_spinner);


        autoCompView1 = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView1);
        autoCompView2 = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView2);
        final View parent_layout = (View) rootView.findViewById(R.id.parent_layout);

        if(!NetworkStatus.isInternetAvailable(myC)) // !!get activity only valid after attach
        {
            autoCompView1.setVisibility(View.GONE);
            autoCompView2.setVisibility(View.GONE);
            Toast.makeText(myC, getResources().getString(R.string.connection_toast), Toast.LENGTH_LONG).show();

        }


        // sets the backgrounds of correctly filled fields to green
        setBackgrounds();

        // determines the minimal number of letters for autocomplete to launch
        autoCompView1.setThreshold(4);
        autoCompView2.setThreshold(4);

        autoCompView1.setAdapter(new GooglePlacesAutocompleteAdapter(myC, R.layout.list_item));
        autoCompView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompView1.getWindowToken(), 0);
                origin_spinner.setSelection(0);
                origin_poi_selected = false;
                origin_address_selected = true;
                setBackgrounds();
                autoCompView1.clearFocus();
                parent_layout.requestFocus();
                //Toast.makeText(myC, selection, Toast.LENGTH_SHORT).show();
            }
        });

        autoCompView2.setAdapter(new GooglePlacesAutocompleteAdapter(myC, R.layout.list_item));
        autoCompView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(autoCompView2.getWindowToken(), 0);
                destination_spinner.setSelection(0);
                destination_poi_selected = false;
                destination_address_selected = true;
                setBackgrounds();
                autoCompView2.clearFocus();
                parent_layout.requestFocus();
                //Toast.makeText(myC, selection, Toast.LENGTH_SHORT).show();

            }
        });

        String[] from_columns;
        if (lan.equals("es")) {from_columns = new String[] {TaxiContract.PoiEntry.COLUMN_NAME_ES}; }
        else {from_columns = new String[] {TaxiContract.PoiEntry.COLUMN_NAME_EN}; }

        simpleCursorAdapter = new SimpleCursorAdapter(myC, R.layout.spinner_item, null, from_columns, new int[] { R.id.spinnerrij }, 0);

        origin_spinner.setAdapter(simpleCursorAdapter);
        destination_spinner.setAdapter(simpleCursorAdapter);

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



                if (origin_selected_position == destination_selected_position) {
                    Toast.makeText(myC, getResources().getString(R.string.diff_points_toast) , Toast.LENGTH_LONG).show();
                }

                else {
                    String[] selectionArgs;

                    if (Integer.parseInt(destination_selected_position) > Integer.parseInt(origin_selected_position))
                        {selectionArgs = new String[]{origin_selected_position, destination_selected_position};}
                    else {selectionArgs = new String[]{destination_selected_position, origin_selected_position};}

                    Uri uri = TaxiContract.RateEntry.buildBasicRateUri();

                    Cursor cursor = myC.getContentResolver().query(
                            uri,   // The content URI -> indicates which provider/db/table to use
                            TaxiContract.RateEntry.COLUMNS,                        // The columns to return for each row
                            null,                    // Selection criteria
                            selectionArgs,                     // Selection criteria
                            null);                        // The sort order for the returned rows

                    String rate_to_pass = null;
                    String distance_to_pass = null;
                    String duration_to_pass = null;
                    String from_to_pass;
                    String to_to_pass;

                    if (lan.equals("es")){
                        Cursor cursor1 = (Cursor) origin_spinner.getSelectedItem();
                        from_to_pass = cursor1.getString(COLNR_POI_NAME_ES);
                        Cursor cursor2 = (Cursor) destination_spinner.getSelectedItem();
                        to_to_pass = cursor2.getString(COLNR_POI_NAME_ES);
                    }
                    else {
                        Cursor cursor1 = (Cursor) origin_spinner.getSelectedItem();
                        from_to_pass = cursor1.getString(COLNR_POI_NAME_EN);
                        Cursor cursor2 = (Cursor) destination_spinner.getSelectedItem();
                        to_to_pass = cursor2.getString(COLNR_POI_NAME_EN);
                    }


                    if (cursor.moveToFirst()) {
                        do {
                            rate_to_pass = String.valueOf(cursor.getDouble(COLNR_RATE_RATE));
                            distance_to_pass = String.valueOf(cursor.getDouble(COLNR_RATE_DIST));
                            duration_to_pass = String.valueOf(cursor.getDouble(COLNR_RATE_DUR));

                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    // Send the event to the host activity if in 2 panes
                    mCallback.onCalculated(rate_to_pass, distance_to_pass, duration_to_pass, from_to_pass, to_to_pass);
                }

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

                Toast.makeText(myC, getResources().getString(R.string.select_toast) , Toast.LENGTH_LONG).show();
            }

        }

        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnCalculatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    private void get_google_places(String from, String to) {

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
                parse_JSON_response(output);

                // Send the event to the host activity if in 2 panes
                mCallback.onCalculated("", distance, duration, from_used, to_used);

            }
        });
        IO_Getter.execute(uri);

    }

    private void parse_JSON_response(String JSON_response) {

        try {

            JSONObject obj = new JSONObject(JSON_response);
            JSONObject temp = obj.getJSONArray("routes")
                    .getJSONObject(0).getJSONArray("legs")
                    .getJSONObject(0);

            from_used = temp.get("start_address").toString();
            to_used = temp.get("end_address").toString();
            String duration_parsed = temp.getJSONObject("duration").get("value").toString();
            Log.w("dur received", duration_parsed);
            String distance_parsed = temp.getJSONObject("distance").get("value").toString();
            double distance_double = Double.parseDouble(distance_parsed) / 1000 ; //in km
            double duration_double = Double.parseDouble(duration_parsed) * DURATION_FACTOR / 60 ; //in min
            distance = String.format(Locale.ENGLISH, "%.2f", distance_double);
            duration = String.format(Locale.ENGLISH, "%.2f", duration_double);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);


        }
    }

    public void processFinish(String output){
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
            Intent intent = new Intent(myC, MyPreferenceActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setBackgrounds () {

        if (origin_address_selected) {
            autoCompView1.setBackground(getResources().getDrawable(R.drawable.view_border));
        } else {autoCompView1.setBackgroundColor(getResources().getColor(R.color.white));}
        if (destination_address_selected) {
            autoCompView2.setBackground(getResources().getDrawable(R.drawable.view_border));
        } else {autoCompView2.setBackgroundColor(getResources().getColor(R.color.white));}
        if (origin_poi_selected) {
            origin_spinner.setBackground(getResources().getDrawable(R.drawable.view_border));
        } else {origin_spinner.setBackgroundColor(getResources().getColor(R.color.white));}
        if (destination_poi_selected) {
            destination_spinner.setBackground(getResources().getDrawable(R.drawable.view_border));
        } else {destination_spinner.setBackgroundColor(getResources().getColor(R.color.white));}
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {

        Uri uri = TaxiContract.PoiEntry.buildBasicPoiUri();

        if (lan.equals("es")) {
                Log.w("", "sppppppp");
                return new CursorLoader(myC, uri, new String[]{TaxiContract.PoiEntry._ID , TaxiContract.PoiEntry.COLUMN_NAME_ES} , null, null, null);
        }
        else {
            Log.w("", "ennnn");
            return new CursorLoader(myC, uri, new String[]{TaxiContract.PoiEntry._ID, TaxiContract.PoiEntry.COLUMN_NAME_EN} , null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        simpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        simpleCursorAdapter.swapCursor(null);
    }

    // get the exact address given an id of a point of interest
    private String getAddressFromPoi(int index) {

        String result=null;

        Uri uri = TaxiContract.PoiEntry.buildPoiUri(index);

        Cursor cursor = myC.getContentResolver().query(
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("origin_poi_selected", origin_poi_selected);
        outState.putBoolean("destination_poi_selected", destination_poi_selected);
        outState.putBoolean("origin_address_selected", origin_address_selected);
        outState.putBoolean("destination_address_selected", destination_address_selected);
    }


    // MainActivity must implement this interface (to communicate data between main fragment and main activity)
    public interface OnCalculatedListener {
        public void onCalculated(String rate, String distance, String duration, String from, String to);
    }

    private class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            Log.w("", "GooglePlacesAutocompleteAdapter created");

        }

        @Override
        public int getCount() {
            Log.w("", "GooglePlacesAutocompleteAdapter getcount");

            return resultList.size();

        }

        @Override
        public String getItem(int index) {
            Log.w("", "GooglePlacesAutocompleteAdapter getitem");

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

