package com.aditya.travelr;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aditya.travelr.pojo.Constants;
import com.aditya.travelr.services.GetAddressIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements
        OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {


    //global variables
    GoogleApiClient googleApiClient;
    Location lastKnowLocation;
    double latitude;
    double longitude;
    SupportMapFragment mapFragment;
    boolean addressRequested;
    GoogleMap googleMap;
    private AddressResultReceiver addressResultReceiver;
    String addressOutput;
    Toolbar toolbar;
    NavigationView navDrawer;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    int selectedId;
    static final String SELECTED_ITEM_ID = "selected_item_id";
    static final String FIRST_TIME = "first_time";
    boolean userSawDrawer = false;
    TextView tv_addressTitle;
    Map<LatLng, Marker> map = new HashMap<>();
    FloatingActionButton fab_addLocation;

    /*
    * Activity override methods
    * onCreate is the most basic one
    * onResume does nothing in this case as we handle that in onStart
    * onStart is called after onResume and it connects to the google api client
    * onStop basically frees the google api client and does other magic
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeAsUpIndicator(R.drawable.abc_tab_indicator_material);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setNav(this);
        tv_addressTitle = (TextView) findViewById(R.id.tv_addressTitle);
        fab_addLocation = (FloatingActionButton) findViewById(R.id.fab_add);
        setClickLister();
        initApiClient();
        googleApiClient.connect();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    }

    /*
    * Navigation Drawer implementation
    * Menu used to navigate between activities
    */

    private void setNav(final Context context) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navDrawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent = null;
                int SelectedId = menuItem.getItemId();
                if (SelectedId == R.id.history) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    intent = new Intent(context, HistoryActivity.class);
                    startActivity(intent);
                }
                if (SelectedId == R.id.geofence) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    intent = new Intent(context, GeofenceActivity.class);
                    startActivity(intent);
                }
                return true;
            }

        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_location:
                getLatestLocation();
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Google Api Client Methods
    /**
    * All these methods are used to get the google api client
    * which is then used to get the location date
    * which then is fed to google maps and reverse geocoder service
    * This is all done in async I suppose.
    * */

    protected synchronized void initApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        lastKnowLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastKnowLocation != null) {
            latitude = lastKnowLocation.getLatitude();
            longitude = lastKnowLocation.getLongitude();
            addressRequested = true;
            fetchLocationStarter(lastKnowLocation);
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

    /*
    * One method is initially called to render the google map
    * second method is called when the location button is clicked
    * the second method pans the camera and nothing more, it is just a fancy button
    * Default title is provided
    * */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng currentPosition = new LatLng(latitude, longitude);
        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(false);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
        Marker marker = map.get(currentPosition);
        if(marker==null){
            googleMap.addMarker(new MarkerOptions()
                    .title("You are here") /*later on get location name and return*/
                    .position(currentPosition));
        }
    }

    private void getLatestLocation() {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        googleMap.animateCamera(cameraUpdate);
    }



    /*
    * These are the methods that are used to get location info
    * There is also a class definition for receiving the results
    * They send basic info, such as lat and lon, through which info is returned
    * */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            updateAppBar(addressOutput);
        }

    }

    private void fetchLocationStarter(Location lastKnowLocation) {
        if (!Geocoder.isPresent()) {
            //handle unavailability of geocoder
        }
        if (addressRequested) {
            startIntentService(lastKnowLocation);
        }
    }

    private void startIntentService(Location lastKnowLocation) {
        Intent intent = new Intent(this, GetAddressIntentService.class);
        addressResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastKnowLocation);
        startService(intent);
    }


    //UI change methods

    private void updateAppBar(String addressOutput) {
        String[] addressArray = addressOutput.split("\n");
        this.addressOutput = addressOutput;
        addressOutput = addressArray[0];
        tv_addressTitle.setText(addressOutput);
    }

    //Activity starter methods

    public void setClickLister(){
        fab_addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initStoreActivity();
            }
        });
    }

    private void initStoreActivity() {
        Intent intent = new Intent(this, LocationAddActivity.class);
        intent.putExtra(Constants.LOCATION, addressOutput);
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);
        startActivity(intent);
    }
}
