package com.aditya.travelr.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.aditya.travelr.pojo.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

/**
 * Created by devad_000 on 30-06-2015.
 */
public class GetAddressIntentService extends IntentService {
    private static final String TAG = "TravelrError";
    protected ResultReceiver resultReceiver;

    public GetAddressIntentService(String name) {
        super(name);
    }
    public GetAddressIntentService(){
        super("GetAddressIntentService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        String errorMessage = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;
        try{
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }catch (IOException ie){
            errorMessage = "I/O Exception";
            Log.e(TAG, errorMessage, ie);
        }catch (IllegalArgumentException illegalArgumentException){
            errorMessage = "Wrong or Illegeal Arguments, such as Lat and Lon";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No Result Found";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        }
        else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }

    private void deliverResultToReceiver(int resultCode, String addressOut) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, addressOut);
        resultReceiver.send(resultCode, bundle );
    }
}
