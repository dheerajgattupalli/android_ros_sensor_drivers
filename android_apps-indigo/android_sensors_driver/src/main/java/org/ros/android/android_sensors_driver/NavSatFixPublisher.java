package org.ros.android.android_sensors_driver;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class NavSatFixPublisher implements
            ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

        private static final String TAG = "location-updates-sample";
        public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
        public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
                UPDATE_INTERVAL_IN_MILLISECONDS / 2;
        private GoogleApiClient mGoogleApiClient;
        private LocationRequest mLocationRequest;

        private Context mContext;
        private getLocation mGetCurrentLocation;

        public NavSatFixPublisher(Context context) {
            mContext = context;
            int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
            	if(resp == ConnectionResult.SUCCESS){
                    buildGoogleApiClient();
                }
                else{
                    Toast.makeText(mContext,"Google Play Services Not Installed", Toast.LENGTH_SHORT).show();

                }

        }

        private synchronized void buildGoogleApiClient() {
            Log.i(TAG, "Building GoogleApiClient");
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            createLocationRequest();
        }

        public interface getLocation {
            void onLocationChanged(Location location);
        }

        public void startGettingLocation(getLocation location) {
            mGetCurrentLocation = location;
            connect();
        }

        public void stopGettingLocation() {
            stopLocationUpdates();
            disconnect();
        }

        private void createLocationRequest() {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        private void startLocationUpdates() {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            }
        }

        private void stopLocationUpdates() {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


        private void connect() {
            mGoogleApiClient.connect();
        }

        private void disconnect() {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            Log.i(TAG, "Connected to GoogleApiClient");
            startLocationUpdates();

        }

        @Override
        public void onLocationChanged(Location location) {
            mGetCurrentLocation.onLocationChanged(location);
            //Log.e("Latitude",String.valueOf(location.getLatitude()));
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.i(TAG, "Connection suspended");
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        }
}