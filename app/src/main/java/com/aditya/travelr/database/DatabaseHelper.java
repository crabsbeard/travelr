package com.aditya.travelr.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by devad_000 on 29-06-2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "travelr";
    private static final String TABLE_NAME = "VISITED";
    private static final String DESCRIPTION_NAME = "description";
    private static final String LONGITUDE_NAME = "longitude";
    private static final String LATITUDE_NAME = "latitude";
    private static final String NICKNAME_NAME = "nickname";
    private static final String REVERSE_GEOCODE_NAME = "reverse_geocode";
    private static final String TIMESTAMP_NAME = "timestamp";
    private static final String EMPTY = " ";

    //query as string
    private static final String tableCreateQuery =
            "CREATE TABLE"
            +EMPTY+TABLE_NAME+EMPTY
            +"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            +EMPTY+NICKNAME_NAME+EMPTY+"VARCHAR(100),"
            +EMPTY+REVERSE_GEOCODE_NAME+EMPTY+"VARCHAR(255),"
            +EMPTY+LATITUDE_NAME+EMPTY+"REAL,"
            +EMPTY+LONGITUDE_NAME+EMPTY+"REAL,"
            +EMPTY+DESCRIPTION_NAME+EMPTY+"TEXT,"
            +EMPTY+TABLE_NAME+EMPTY+"INTEGER);";

    private static final int DATABASE_VERSION = 1;
    Context context;
    public DatabaseHelper(Context context){
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
