package com.aditya.travelr.messages;

import android.content.Context;
import android.content.res.Resources;

import com.aditya.travelr.R;
import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Created by devad_000 on 01-07-2015.
 */
public class GeoFenceErrorMessages {
    private GeoFenceErrorMessages() {}

    public static String getErrorString(Context context, int errorCode) {
        Resources resources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return resources.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return resources.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return resources.getString(R.string.geofence_too_many_pending_intents);
            default:
                return resources.getString(R.string.unknown_geofence_error);
        }
    }
}
