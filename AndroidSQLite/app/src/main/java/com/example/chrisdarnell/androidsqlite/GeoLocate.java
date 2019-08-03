package com.example.chrisdarnell.androidsqlite;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by chrisdarnell on 7/29/17.
 */

public class GeoLocate extends AppCompatActivity{

    if (mGoogleApiClient == null) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    mLocationRequest = LocationRequest.create();
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    mLocationRequest.setInterval(5000);
    mLocationRequest.setFastestInterval(5000);

    mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mLonText.setText(String.valueOf(location.getLongitude()));
                mLatText.setText(String.valueOf(location.getLatitude()));
            } else {
                mLonText.setText("No Location Available");
            }
        }
    };

    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,mLocationListener);

}


