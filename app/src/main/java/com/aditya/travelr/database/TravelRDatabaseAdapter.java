package com.aditya.travelr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.aditya.travelr.pojo.HistoryCard;

import java.util.ArrayList;

/**
 * Created by devad_000 on 29-06-2015.
 */
public class TravelRDatabaseAdapter  {
    TravelRDatabaseHelper helperObject;
    Context context;
    ArrayList<HistoryCard> historyCardArrayList;

    public TravelRDatabaseAdapter(Context context){
        helperObject = new TravelRDatabaseHelper(context);
        this.context = context;
    }

    public long insertData(String nickname, String reverseGeocode, double lat, double lon, String description, long timestamp ){
        SQLiteDatabase sqLiteDatabase = helperObject.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(helperObject.NICKNAME_NAME, nickname);
        contentValues.put(helperObject.REVERSE_GEOCODE_NAME, reverseGeocode);
        contentValues.put(helperObject.LATITUDE_NAME, lat);
        contentValues.put(helperObject.LONGITUDE_NAME, lon);
        contentValues.put(helperObject.DESCRIPTION_NAME, description);
        contentValues.put(helperObject.TIMESTAMP_NAME, timestamp);
        long id = sqLiteDatabase.insert(helperObject.TABLE_NAME, null, contentValues);
        return id;
    }

    public ArrayList<HistoryCard> fetchAllData(){
        historyCardArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = helperObject.getWritableDatabase();
        String[] columns = {helperObject.UID,
                helperObject.NICKNAME_NAME,
                helperObject.REVERSE_GEOCODE_NAME,
                helperObject.LATITUDE_NAME,
                helperObject.LONGITUDE_NAME,
                helperObject.DESCRIPTION_NAME,
                helperObject.TIMESTAMP_NAME};

        Cursor cursor = sqLiteDatabase.query(helperObject.TABLE_NAME, columns, null, null, null, null,
                helperObject.TIMESTAMP_NAME+" DESC");
        int index1 = cursor.getColumnIndex(helperObject.UID);
        int index2 = cursor.getColumnIndex(helperObject.NICKNAME_NAME);
        int index3 = cursor.getColumnIndex(helperObject.REVERSE_GEOCODE_NAME);
        int index4 = cursor.getColumnIndex(helperObject.LATITUDE_NAME);
        int index5 = cursor.getColumnIndex(helperObject.LONGITUDE_NAME);
        int index6 = cursor.getColumnIndex(helperObject.DESCRIPTION_NAME);
        int index7 = cursor.getColumnIndex(helperObject.TIMESTAMP_NAME);
        int i=0;
        while (cursor.moveToNext()){
            int uid = cursor.getInt(index1);
            String nickname = cursor.getString(index2);
            String location = cursor.getString(index3);
            double latitude = cursor.getDouble(index4);
            double longitude = cursor.getDouble(index5);
            String description = cursor.getString(index6);
            long timeStamp = cursor.getLong(index7);
            HistoryCard historyCard = new HistoryCard(uid, nickname, location, latitude, longitude, description, timeStamp);
            historyCardArrayList.add(historyCard);
        }
        return historyCardArrayList;
    }

    class TravelRDatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "travelr";
        private static final String TABLE_NAME = "VISITED";
        private static final String DESCRIPTION_NAME = "description";
        private static final String LONGITUDE_NAME = "longitude";
        private static final String LATITUDE_NAME = "latitude";
        private static final String NICKNAME_NAME = "nickname";
        private static final String REVERSE_GEOCODE_NAME = "reverse_geocode";
        private static final String TIMESTAMP_NAME = "timestamp";
        private static final String EMPTY = " ";
        private static final String UID ="_id";

        //query as string
        private static final String tableCreateQuery =
                "CREATE TABLE"
                        + EMPTY + TABLE_NAME + EMPTY
                        + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + EMPTY + NICKNAME_NAME + EMPTY + "VARCHAR(100),"
                        + EMPTY + REVERSE_GEOCODE_NAME + EMPTY + "VARCHAR(255),"
                        + EMPTY + LATITUDE_NAME + EMPTY + "REAL,"
                        + EMPTY + LONGITUDE_NAME + EMPTY + "REAL,"
                        + EMPTY + DESCRIPTION_NAME + EMPTY + "TEXT,"
                        + EMPTY + TIMESTAMP_NAME + EMPTY + "INTEGER);";

        private static final int DATABASE_VERSION = 1;
        Context context;

        public TravelRDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //table is created here
            try {
                db.execSQL(tableCreateQuery);
            } catch (SQLException e) {
                //handle with snack bar
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
