package com.aditya.travelr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeActivity extends AppCompatActivity implements
        OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {


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
        setupNavDrawer(savedInstanceState);
        initApiClient();
        googleApiClient.connect();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    * Navigation Drawer implementation
    * Menu used to navigate between activities
    * */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }

    private void setupNavDrawer(Bundle savedInstanceState) {
        navDrawer = (NavigationView) findViewById(R.id.navDrawer);
        navDrawer.setNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if(!drawerWasSeen()){
            showDrawer();
            markDrawerSeen();
        }
        else{
            hideDrawer();
        }
        selectedId = savedInstanceState == null? R.id.history : savedInstanceState.getInt(SELECTED_ITEM_ID);
        navigate(selectedId);
    }
    private boolean drawerWasSeen(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return userSawDrawer;
    }
    private void showDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    private void markDrawerSeen(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, userSawDrawer).apply();
    }
    private void hideDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    private void navigate(int mSelectedId) {
        Intent intent = null;
        if (mSelectedId == R.id.history) {
            drawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.geofence) {
            drawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, GeofenceActivity.class);
            startActivity(intent);
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
        googleMap.addMarker(new MarkerOptions()
                .title("You are here") /*later on get location name and return*/
                .position(currentPosition));
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
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastKnowLocation);
        startService(intent);
    }


    //UI change methods

    private void updateAppBar(String addressOutput) {

    }

    //Activity starter methods

    private void initStoreActivity() {
        Intent intent = new Intent(this, LocationAddActivity.class);
        intent.putExtra(Constants.LOCATION, addressOutput);
        intent.putExtra(Constants.LATITUDE, latitude);
        intent.putExtra(Constants.LONGITUDE, longitude);

    }
}
