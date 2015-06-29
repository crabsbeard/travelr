package com.aditya.travelr;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aditya.travelr.database.DatabaseHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeActivity extends AppCompatActivity implements
        OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {
    DatabaseHelper dbhelper;
    GoogleApiClient googleApiClient;
    Location lastKnowLocation;
    double latitude;
    double longitude;
    SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initApiClient();
        googleApiClient.connect();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        dbhelper = new DatabaseHelper(this);

        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
    }

    protected synchronized void initApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentPosition = new LatLng(latitude, longitude);

        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 12));

        googleMap.addMarker(new MarkerOptions()
                .title("You are here") /*later on get location name and return*/
                .position(currentPosition));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastKnowLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastKnowLocation != null) {
            latitude = lastKnowLocation.getLatitude();
            longitude = lastKnowLocation.getLongitude();
            mapFragment.getMapAsync(this);
        } else {
            //will handle later
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //error message will handle later
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //error message, will handle later
    }

    //storing data methods

    private void initStoreActivity(){

    }
}
