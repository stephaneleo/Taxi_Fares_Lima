package com.example.android.taxi_fares_lima.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class TaxiProvider extends ContentProvider {



    // The URI Matcher used by this content provider.
    private static final UriMatcher MyUriMatcher = buildUriMatcher();
    private DbHelper MyOpenHelper;
    private SQLiteDatabase db;
    private static final SQLiteQueryBuilder MyQueryBuilder = new SQLiteQueryBuilder();


    //only gets created upon installation of the app
    @Override
    public boolean onCreate() {
        MyOpenHelper = new DbHelper(getContext());
        db = MyOpenHelper.getReadableDatabase(); //initialize the database if it doesn't exist
        Log.w("ff", "openhelper created");
        return true;
    }

    // Codes for the matcher
    static final int POI = 100;
    static final int POI_WITH_ID = 101;
    static final int RATE = 200;

    //poi._id = ?
    private static final String sPoiSelection =
            TaxiContract.PoiEntry.TABLE_NAME+
                    "." + TaxiContract.PoiEntry._ID + " = ? ";

    //rate.from_id = ? AND rate.to_id = ?
    private static final String sRateSelection =
            TaxiContract.RateEntry.TABLE_NAME+
                    "." + TaxiContract.RateEntry.COLUMN_FROM_ID + " = ? AND " +
                    TaxiContract.RateEntry.COLUMN_TO_ID + " = ? ";

    // query for poi by id
    private Cursor getPoiByID(Uri uri, String[] projection) {

        String ID = TaxiContract.PoiEntry.getIDSettingFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sPoiSelection;
        selectionArgs = new String[]{ID};

        MyQueryBuilder.setTables(TaxiContract.PoiEntry.TABLE_NAME);

        return MyQueryBuilder.query(MyOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }

    private Cursor getRateByIDs(Uri uri, String[] projection, String[] selectionArgs) {

        MyQueryBuilder.setTables(TaxiContract.RateEntry.TABLE_NAME);

        String selection = sRateSelection;
        return MyQueryBuilder.query(MyOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }


    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = MyUriMatcher.match(uri);

        Log.w("bb", "uri matched " + Integer.toString(match));

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case POI:
                return TaxiContract.PoiEntry.CONTENT_TYPE;
            case POI_WITH_ID:
                return TaxiContract.PoiEntry.CONTENT_ITEM_TYPE;
            case RATE:
                return TaxiContract.RateEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Log.w("ff", "query received");

        Cursor retCursor;
        switch (MyUriMatcher.match(uri)) {
            // "poi"
            case POI:
            {

                MyQueryBuilder.setTables(TaxiContract.PoiEntry.TABLE_NAME);

                retCursor =  MyQueryBuilder.query(MyOpenHelper.getReadableDatabase(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

                /*retCursor = MyOpenHelper.getReadableDatabase().query(
                        TaxiContract.PoiEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null, null
                );*/
                break;
            }
            // "poi/#"
            case POI_WITH_ID: {
                retCursor = getPoiByID(uri, projection);
                break;
            }
            // "rate"
            case RATE: {
                Log.w("dd", uri + " " + projection + " " + selectionArgs);
                retCursor = getRateByIDs(uri, projection, selectionArgs);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = MyOpenHelper.getWritableDatabase();
        final int match = MyUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POI: {
                long _id = db.insert(TaxiContract.PoiEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TaxiContract.PoiEntry.buildPoiUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case RATE: {
                long _id = db.insert(TaxiContract.RateEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TaxiContract.RateEntry.buildRateUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = MyOpenHelper.getWritableDatabase();
        final int match = MyUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case POI:
                rowsDeleted = db.delete(
                        TaxiContract.PoiEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case RATE:
                rowsDeleted = db.delete(
                        TaxiContract.RateEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = MyOpenHelper.getWritableDatabase();
        final int match = MyUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case POI:
                rowsUpdated = db.update(TaxiContract.PoiEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case RATE:
                rowsUpdated = db.update(TaxiContract.RateEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = MyOpenHelper.getWritableDatabase();
        final int match = MyUriMatcher.match(uri);
        switch (match) {
            case POI:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TaxiContract.PoiEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case RATE:
                db.beginTransaction();
                int returnCount2 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TaxiContract.RateEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount2++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount2;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TaxiContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, TaxiContract.PATH_POI, POI);
        matcher.addURI(authority, TaxiContract.PATH_POI + "/#", POI_WITH_ID);

        matcher.addURI(authority, TaxiContract.PATH_RATE, RATE);


        return matcher;
    }
}
