package com.aditya.travelr;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.aditya.travelr.pojo.Constants;
import com.aditya.travelr.services.GeoFencingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by devad_000 on 30-06-2015.
 */
public class GeofenceActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "creating-and-monitoring-geofences";
    protected GoogleApiClient googleApiClient;
    ArrayList<Geofence> geofenceArrayList;
    private boolean geoFenceAdded;
    private PendingIntent geoFencePendingIntent;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);
        geofenceArrayList = new ArrayList<>();
        geoFencePendingIntent = null;
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        geoFenceAdded = sharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //connection logger
    }

    @Override
    public void onConnectionSuspended(int i) {
        //connection suspension logger
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //connection failed logger
    }

    @Override
    public void onResult(Status status) {
        if(status.isSuccess()){
            geoFenceAdded = !geoFenceAdded;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.SHARED_PREFERENCES_NAME, geoFenceAdded);
            editor.commit();
        }
        else{
            //need to handle that shit!
        }
    }

    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceArrayList);
        return builder.build();
    }

    public void addGeofenceHandler(){
        if(!googleApiClient.isConnected()){
            //handle non-connection
        }
        try{
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeoFencePendingIntent()
            ).setResultCallback(this);
        }catch(SecurityException se){
            //catch exception message
        }
    }
    private PendingIntent getGeoFencePendingIntent(){
        if(geoFencePendingIntent!=null){
            return geoFencePendingIntent;
        }
        Intent intent = new Intent(this, GeoFencingService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void getGeofenceList(){

    }

}
