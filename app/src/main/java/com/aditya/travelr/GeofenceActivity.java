package com.aditya.travelr;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.travelr.database.TravelRDatabaseAdapter;
import com.aditya.travelr.pojo.Constants;
import com.aditya.travelr.pojo.HistoryCard;
import com.aditya.travelr.services.GeoFencingService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class GeofenceActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "creating-and-monitoring-geofences";
    protected GoogleApiClient googleApiClient;
    ArrayList<Geofence> geofenceArrayList;
    private boolean geoFenceAdded;
    private PendingIntent geoFencePendingIntent;
    private SharedPreferences sharedPreferences;
    TextView tv_appBarTitle;
    Button removeGeoButton;
    Button addButton;
    TravelRDatabaseAdapter databaseAdapter;
    ArrayList<HistoryCard> arrayListDialog;
    String selected_nickname;
    double selected_lat;
    double selected_lon;
    int radius;
    TextView textView;
    int position;
    private String SHARED_PREF = "com_aditya_travelr_sharedpref_geofence";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo);
        getGeofenceList();
        databaseAdapter = new TravelRDatabaseAdapter(this);
        tv_appBarTitle = (TextView) findViewById(R.id.tv_addressTitle);
        tv_appBarTitle.setText("Tap to Add or Remove!");
        removeGeoButton = (Button) findViewById(R.id.b_removeGeo);
        removeGeoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptRemove();
            }
        });
        addButton = (Button) findViewById(R.id.b_addGeoLocation);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptAdd();
            }
        });
        geofenceArrayList = new ArrayList<>();
        geoFencePendingIntent = null;
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        geoFenceAdded = sharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);
        buildGoogleApiClient();
    }

    private void promptAdd() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                GeofenceActivity.this);
        builderSingle.setTitle("Select a Location");
        arrayListDialog = databaseAdapter.fetchAllData();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                GeofenceActivity.this,
                android.R.layout.simple_list_item_1);
        for (HistoryCard historyCard : arrayListDialog) {
            arrayAdapter.add(historyCard.getNickname());
        }
        builderSingle.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        position = which;
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                GeofenceActivity.this);
                        builderInner.setCancelable(false);
                        builderInner.setMessage(selected_nickname);
                        builderInner.setTitle("Confirm");
                        builderInner.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        which++;
                                        selected_nickname = arrayListDialog.get(position).getNickname();
                                        selected_lat = arrayListDialog.get(position).getLat();
                                        selected_lon = arrayListDialog.get(position).getLon();
                                        addGeofenceList(selected_nickname, selected_lat, selected_lon);
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });
        builderSingle.show();
    }

    private void promptRemove() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Confirm Delete");
        alertDialog.setMessage("Are you sure you want delete this?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                removeGeofenceHandler();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
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
        if (status.isSuccess()) {
            geoFenceAdded = !geoFenceAdded;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.SHARED_PREFERENCES_NAME, geoFenceAdded);
            editor.apply();
        } else {
            //need to handle that shit!
        }
    }

    public void addGeofenceHandler() {
        if (!googleApiClient.isConnected()) {
            //handle non-connection
        }
        try {
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    getGeofencingRequest(),
                    getGeoFencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException se) {
            //catch exception message
        }
    }

    public void removeGeofenceHandler() {
        if (!googleApiClient.isConnected()) {
        }
        try {
            LocationServices.GeofencingApi.removeGeofences(
                    googleApiClient,
                    getGeoFencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException securityException) {
        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceArrayList);
        return builder.build();
    }

    private PendingIntent getGeoFencePendingIntent() {
        if (geoFencePendingIntent != null) {
            return geoFencePendingIntent;
        }
        Intent intent = new Intent(this, GeoFencingService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void getGeofenceList() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPreferences.getAll();
        double lat;
        double lon;
        int i= 0;
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if(i>3){
                break;
            }
            String latlng[];
            latlng = entry.getValue().toString().split(",");
            lat = Double.valueOf(latlng[0]);
            lon = Double.valueOf(latlng[1]);
            i++;
            geofenceArrayList.add(new Geofence.Builder().setRequestId(entry.getKey())
            .setCircularRegion(lat, lon, Constants.GEOFENCE_RADIUS_IN_METERS)
            .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT).build());
        }
    }

    public void addGeofenceList(String nickname, double lat, double lon) {
        String key = "key_" + nickname + "_" + String.valueOf(lat) + "_";
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            //already exist
            return;
        }
        if (sharedPreferences.getAll().size() == 3) {
            //its full clear  it
            Toast toast = Toast.makeText(this, "List if full, consider removing", Toast.LENGTH_SHORT);
            toast.show();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        double[] latlng = {lat, lon};
        String latlngSring = Arrays.toString(latlng);
        editor.putString(nickname, latlngSring);
    }

}
