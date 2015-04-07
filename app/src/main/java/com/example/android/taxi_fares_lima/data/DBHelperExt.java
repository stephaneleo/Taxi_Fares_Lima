//
//package com.example.android.taxi_fares_lima.data;
//
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteException;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//public class DBHelperExt extends SQLiteOpenHelper{
//
//    //The Android's default system path of your application database.
//    String DB_PATH = null;
//    private static String DB_NAME = "taxi_ext.db";
//    private SQLiteDatabase myDataBase;
//    private final Context myContext;
//
//
//    /**
//     * Constructor
//     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
//     * @param context
//     */
//    public DBHelperExt(Context context) {
//        super(context, DB_NAME, null, 1);
//        this.myContext = context;
//
//        if(android.os.Build.VERSION.SDK_INT >= 17){
//            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
//        }
//        else
//        {
//            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
//        }
//    }
//
//    /**
//     * Creates a empty database on the system and rewrites it with your own database.
//     */
//     public void createDataBase() throws IOException {
//
//         boolean dbExist = checkDataBase();
//         Log.w("dd", "in oncrezate222");
//
//
//         if(dbExist){
//             Log.w("dd", "db already exists");
//         }else{
//             Log.w("dd", "db NOT already exists");
//
//         //By calling this method an empty database will be created into the default system path
//         //of your application so we are gonna be able to overwrite that database with our database.
//         //this.getReadableDatabase();
//
//
//         try {
//         copyDataBase();
//             Log.w("dd", "db copied");
//
//         } catch (IOException e) {
//
//         throw new Error("Error copying database");
//
//         }
//             Log.w("dd", "finalllll");
//         }
//
//     }
//
//    /**
//     * Check if the database already exist to avoid re-copying the file each time you open the application.
//     * @return true if it exists, false if it doesn't
//     */
//    private boolean checkDataBase(){
//
//        File database = myContext.getDatabasePath("taxi_ext.db");
//
//        if (!database.exists()) {
//            // Database does not exist so copy it from assets here
//            Log.w("Database", "Not Found");
//            return false;
//        } else {
//            Log.w("Database", "Found");
//            Log.w("daatataaba", database.toString());
//            return false;
//        }
//
//        /*
//        SQLiteDatabase checkDB = null;
//
//        try{
//            String myPath = DB_PATH + DB_NAME;
//            Log.w("myPath", myPath);
//            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
//            Log.w("myPath", checkDB.toString());
//
//        }catch(SQLException e){
//            Log.w("myPath", "doesn't exist");
//            //database does't exist yet.
//
//        }
//
//        if(checkDB != null){
//
//            checkDB.close();
//
//        }
//
//        return checkDB != null ? true : false;*/    }
//
//    /**
//     * Copies your database from your local assets-folder to the just created empty database in the
//     * system folder, from where it can be accessed and handled.
//     * This is done by transfering bytestream.
//     * */
//    private void copyDataBase() throws IOException{
//
//        {
//            InputStream mInput = myContext.getAssets().open(DB_NAME);
//            String outFileName = DB_PATH + DB_NAME;
//            Log.w("dbbb", outFileName);
//
//            OutputStream mOutput = new FileOutputStream(outFileName);
//            byte[] mBuffer = new byte[1024];
//            int mLength;
//            while ((mLength = mInput.read(mBuffer))>0)
//            {
//                mOutput.write(mBuffer, 0, mLength);
//            }
//            mOutput.flush();
//            mOutput.close();
//            mInput.close();
//
//
//
//            Log.w("dbbb", "database copied");
//            Log.w("dbbb", outFileName);
//        }
//
//    }
//
//    public void openDataBase() throws SQLiteException{
//
//        //Open the database
//        String myPath = DB_PATH + DB_NAME;
//        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
//
//    }
//
//    @Override
//    public synchronized void close() {
//
//        if(myDataBase != null)
//            myDataBase.close();
//
//        super.close();
//
//    }
//
//
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        Log.w("dd", "in oncrezate");
//        try {createDataBase();}
//        catch (java.io.IOException e) {}
//
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
//    }
//
//
//}
