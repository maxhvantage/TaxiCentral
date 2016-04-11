package com.taxicentral.Classes;

/**
 * Created by MAX on 11/9/2015.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.taxicentral.Activity.TaxiWaitingActivity;

public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;
    boolean GPSEnabled = false;
    boolean NetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude, longitude;
    double Ulatitude, Ulongitude;
    private Location mLastLocation;
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final long MIN_DISTANCE_FOR_UPDATES = 5; //10;
    private static final long MIN_TIME_FOR_UPDATES = 1000; //1000*60;

    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    @TargetApi(23)
    public void getLocation() {
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        /*if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("locationcheck", "PERMISSION_GRANTED");
            return;
        }else{
            Log.d("locationcheck", "PERMISSION_NOT_GRANTED");
        }*/


        try {
            Log.d("locationcheck", "locationcheck");

            GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            NetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.d("locationcheck", GPSEnabled + "locationcheck" + NetworkEnabled);
            if (!GPSEnabled && !NetworkEnabled) {
                latitude = 0.0;
                longitude = 0.0;
                Ulatitude = 0.0;
                Ulongitude = 0.0;
            } else {
                this.canGetLocation = true;
                if (NetworkEnabled) {

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Ulatitude = location.getLatitude();
                            Ulongitude = location.getLongitude();
                        }
                    }

                }
                if (GPSEnabled) {
                    if (location == null) {

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_FOR_UPDATES, MIN_DISTANCE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Ulatitude = location.getLatitude();
                                Ulongitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("GPSTRACKER>>>>>>>>>>>>",e.getMessage());
        }
        return;
    }


    @Override
    public void onLocationChanged(final Location location) {
        //this.location = location;

        this.location = location;
        Log.d("LOCATION<<<<<", location.getLatitude()+"");
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //functions
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }


    public void stopUsingGPS() {
        if (locationManager != null) {

            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to enable it in settings?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private static long calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }




}