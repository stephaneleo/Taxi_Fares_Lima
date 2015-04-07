package com.example.android.taxi_fares_lima.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TaxiContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website
    public static final String CONTENT_AUTHORITY = "com.example.android.taxi_fares_lima.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's) - my tables
    public static final String PATH_POI = "poi";
    public static final String PATH_RATE = "rate";


    /* Inner class that defines the table contents of the location table */
    public static final class PoiEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_POI).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POI;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POI;

        // Table name
        public static final String TABLE_NAME = "poi";
        public static final String COLUMN_NAME_ES = "name_es";
        public static final String COLUMN_NAME_EN = "name_en";
        public static final String COLUMN_ADDRESS = "address";

        public static final String[] COLUMNS = {_ID, COLUMN_NAME_ES, COLUMN_NAME_EN, COLUMN_ADDRESS};

        public static Uri buildBasicPoiUri() {
            return CONTENT_URI;
        }

        public static Uri buildPoiUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIDSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1); } //segment correct ???
    }

    /* Inner class that defines the table contents of the rate table */
    public static final class RateEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RATE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RATE;

        public static final String TABLE_NAME = "rate";

        public static final String COLUMN_FROM_ID = "from_id"; //foreign key
        public static final String COLUMN_TO_ID = "to_id"; //foreign key
        public static final String COLUMN_RATE = "rate";
        public static final String COLUMN_DIST = "dist";
        public static final String COLUMN_DUR = "dur";

        public static final String[] COLUMNS = {_ID, COLUMN_FROM_ID, COLUMN_TO_ID, COLUMN_RATE, COLUMN_DIST, COLUMN_DUR};


        public static Uri buildBasicRateUri() {
            return CONTENT_URI;
        }

        public static Uri buildRateUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        ///////////////////////////////////////////

        /*

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }*/
    }




}
