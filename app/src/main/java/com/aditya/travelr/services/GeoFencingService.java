package com.aditya.travelr.services;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by devad_000 on 01-07-2015.
 */
public class GeoFencingService extends IntentService {

    public GeoFencingService(String name) {
        super(name);
    }

    public GeoFencingService(){
        super("GeoFencingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
