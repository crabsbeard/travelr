package com.aditya.travelr;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.travelr.database.TravelRDatabaseAdapter;
import com.aditya.travelr.pojo.Constants;

/**
 * Created by devad_000 on 30-06-2015.
 */
public class LocationAddActivity extends AppCompatActivity {

    double latitude;
    double longitude;
    String locationAddress;
    Toolbar toolbar;
    TextView tv_title;
    TextView tv_appbar;
    Button b_addData;
    EditText et_name;
    EditText et_desc;
    TravelRDatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        toolbar = (Toolbar) findViewById(R.id.toolbarLocation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        databaseAdapter = new TravelRDatabaseAdapter(this);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0);
        longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0);
        locationAddress = intent.getStringExtra(Constants.LOCATION);
        setupViews(latitude, longitude, locationAddress);
        setButtonClick(this);
    }

    private void setButtonClick(final Context context) {
        b_addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = et_desc.getText().toString();
                String nickname = et_name.getText().toString();
                if(description==null||description.trim().length()==0||nickname==null||nickname.trim().length()==0){
                    Toast toast = Toast.makeText(context, "No field can be empty!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(latitude==0.0||longitude==0.0||locationAddress==null){
                    Toast toast = Toast.makeText(context, "Some Problem with the result!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    long id = databaseAdapter.insertData(nickname, locationAddress,latitude,longitude, description,System.currentTimeMillis()/1000);
                    if(id<1){
                        Toast toast = Toast.makeText(context, "Error! Can't Insert Data", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else{
                        Toast toast = Toast.makeText(context, "Added Values!", Toast.LENGTH_SHORT);
                        toast.show();
                        clearFieldsAndReturn();
                    }
                }
            }
        });
    }

    private void clearFieldsAndReturn() {
        et_desc.setText("");
        et_name.setText("");
        onBackPressed();
        //Intent intent = new Intent(this, HomeActivity.class);
        //startActivity(intent);
    }

    private void setupViews(double latitude, double longitude, String locationAddress) {
        tv_title = (TextView) findViewById(R.id.tv_headline);
        tv_appbar = (TextView) findViewById(R.id.tv_addressTitle);
        b_addData = (Button) findViewById(R.id.b_addData);
        et_name = (EditText) findViewById(R.id.et_nickname);
        et_desc = (EditText) findViewById(R.id.et_description);
        String title = crunchDoubleToString(latitude, longitude);
        tv_title.setText(title);
        tv_appbar.setText(locationAddress);
    }

    private String crunchDoubleToString(double latitude, double longitude) {
        String lat = Double.toString(latitude).substring(0,6);
        String lon = Double.toString(longitude).substring(0,6);
        lat = "Lat: "+lat;
        lon = "Lon: "+lon;
        return lat+", "+lon;
    }
}
