package com.aditya.travelr.pojo;

/**
 * Created by devad_000 on 30-06-2015.
 */
public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "com.aditya.travelr";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    public static final String LOCATION = PACKAGE_NAME+".LOCATION";
    public static final String LATITUDE = PACKAGE_NAME+".LAT";
    public static final String LONGITUDE = PACKAGE_NAME+".LON";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 24;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public float GEOFENCE_RADIUS_IN_METERS = 100;

    public void setGEOFENCE_RADIUS_IN_METERS(float GEOFENCE_RADIUS_IN_METERS) {
        this.GEOFENCE_RADIUS_IN_METERS = GEOFENCE_RADIUS_IN_METERS;
    }

    public float getGEOFENCE_RADIUS_IN_METERS() {
        return GEOFENCE_RADIUS_IN_METERS;
    }
}