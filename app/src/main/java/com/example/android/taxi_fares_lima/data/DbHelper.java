package com.example.android.taxi_fares_lima.data;


import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.taxi_fares_lima.data.TaxiContract.PoiEntry;
import com.example.android.taxi_fares_lima.data.TaxiContract.RateEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class DbHelper extends SQLiteOpenHelper {


    private Context context;
    private static final String DATABASE_NAME = "taxi.db";
    private static final int DATABASE_VERSION = 1;

    // SQL to create a table to hold poi's.
    final String SQL_CREATE_POI_TABLE = "CREATE TABLE " + PoiEntry.TABLE_NAME + " (" +
            PoiEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            PoiEntry.COLUMN_NAME_ES + " TEXT UNIQUE NOT NULL, " +
            PoiEntry.COLUMN_NAME_EN + " TEXT UNIQUE NOT NULL, " +
            PoiEntry.COLUMN_ADDRESS + " TEXT UNIQUE NOT NULL " +
            " );";

    final String SQL_CREATE_RATE_TABLE = "CREATE TABLE " + RateEntry.TABLE_NAME + " (" +

            RateEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

            RateEntry.COLUMN_FROM_ID + " INTEGER NOT NULL, " +
            RateEntry.COLUMN_TO_ID + " INTEGER NOT NULL, " +
            RateEntry.COLUMN_RATE + " REAL NOT NULL, " +
            RateEntry.COLUMN_DIST + " REAL NOT NULL," +
            RateEntry.COLUMN_DUR + " REAL NOT NULL, " +

            " FOREIGN KEY (" + RateEntry.COLUMN_FROM_ID + ") REFERENCES " +
            PoiEntry.TABLE_NAME + "(" + PoiEntry._ID + "), " +

            " FOREIGN KEY (" + RateEntry.COLUMN_TO_ID + ") REFERENCES " +
            PoiEntry.TABLE_NAME + "(" + PoiEntry._ID + "));";



    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    // gets called upon first getreadabledatabase
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POI_TABLE);
        db.execSQL(SQL_CREATE_RATE_TABLE);

        populateTablePOI("poi_table.csv", db);
        populateTableRate("rate_table.csv", db);

        Log.w("db", "created");

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + PoiEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RateEntry.TABLE_NAME);
        onCreate(db);
    }

    private void populateTablePOI (String file, SQLiteDatabase db) {
        AssetManager manager = context.getAssets();
        InputStream inStream = null;
        try {
            inStream = manager.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] cols = line.split("\\|");
                if (cols.length != PoiEntry.COLUMNS.length) {
                    Log.e("CSVParser", "Skipping Bad CSV Row");
                    continue;
                }

                ContentValues cv = new ContentValues();

                cv.put(PoiEntry.COLUMNS[0], Integer.parseInt(cols[0].trim()));
                cv.put(PoiEntry.COLUMNS[1], cols[1].trim());
                cv.put(PoiEntry.COLUMNS[2], cols[2].trim());
                cv.put(PoiEntry.COLUMNS[3], cols[3].trim());
                db.insert(PoiEntry.TABLE_NAME, null, cv);
            }

            long numRows = DatabaseUtils.queryNumEntries(db, PoiEntry.TABLE_NAME);
            Log.e("rrdcferrfer", Long.toString(numRows));

        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    private void populateTableRate (String file, SQLiteDatabase db) {
        AssetManager manager = context.getAssets();
        InputStream inStream = null;
        try {
            inStream = manager.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader buffer = new BufferedReader(new InputStreamReader(inStream));
        String line = "";
        db.beginTransaction();
        try {
            while ((line = buffer.readLine()) != null) {
                String[] cols = line.split("\\|");

                Log.e("CSVParser", cols[0]);
                Log.e("CSVParser", cols[1]);
                Log.e("CSVParser", cols[2]);
                Log.e("CSVParser", cols[3]);
                Log.e("CSVParser", cols[4]);
                Log.e("CSVParser", cols[5]);


                if (cols.length != RateEntry.COLUMNS.length) {
                    Log.e("CSVParser", "Skipping Bad CSV Row");


                    continue;
                }

                ContentValues cv = new ContentValues();

                cv.put(RateEntry.COLUMNS[0], Integer.parseInt(cols[0].trim()));
                cv.put(RateEntry.COLUMNS[1], Integer.parseInt(cols[1].trim()));
                cv.put(RateEntry.COLUMNS[2], Integer.parseInt(cols[2].trim()));
                cv.put(RateEntry.COLUMNS[3], Double.parseDouble(cols[3].trim()));
                cv.put(RateEntry.COLUMNS[4], Double.parseDouble(cols[4].trim()));
                cv.put(RateEntry.COLUMNS[5], Double.parseDouble(cols[5].trim()));

                db.insert(RateEntry.TABLE_NAME, null, cv);
            }

            long numRows = DatabaseUtils.queryNumEntries(db, RateEntry.TABLE_NAME);
            Log.e("rrdcferrfer", Long.toString(numRows));
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
