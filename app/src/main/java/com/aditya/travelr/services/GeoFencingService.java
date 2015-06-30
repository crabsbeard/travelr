package com.aditya.travelr.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aditya.travelr.HomeActivity;
import com.aditya.travelr.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devad_000 on 01-07-2015.
 */
public class GeoFencingService extends IntentService {

    protected static final String TAG = "geofence-transitions-service";

    public GeoFencingService(String name) {
        super(name);
    }

    public GeoFencingService(){
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()){
            //handle error
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER||geofenceTransition==Geofence.GEOFENCE_TRANSITION_EXIT){
            List<Geofence> triggeredGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetail(
                    this,
                    geofenceTransition,
                    triggeredGeofences
            );

            sendOutNotification(geofenceTransitionDetails);
            //log it maybe?
        }
        else{
            //handle it maybe?
        }
    }

    private void sendOutNotification(String geofenceTransitionDetails) {
        Intent notificationIntent = new Intent(getApplicationContext(), HomeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColor(Color.BLUE)
                .setContentTitle(geofenceTransitionDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }

    private String getGeofenceTransitionDetail(Context context, int geofenceTransition, List<Geofence> triggeredGeofences) {
        String geofenceTransitionString = getTransitionString(geofenceTransition);
        ArrayList triggeredGeofenceIdsList = new ArrayList();
        for(Geofence geofence : triggeredGeofences){
            triggeredGeofenceIdsList.add(geofence.getRequestId());
        }
        String triggeredGeofenceIdsString = TextUtils.join(",", triggeredGeofenceIdsList);
        return geofenceTransitionString+": "+triggeredGeofenceIdsString;
    }

    private String getTransitionString(int geofenceTransition) {
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }
}
