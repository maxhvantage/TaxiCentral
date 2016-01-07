package com.taxicentral.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;

public class ShowMapsActivity extends FragmentActivity  implements LocationListener {
    //aa
    GoogleMap map;
    LocationManager locationManager;
    double lat, lon;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);*/

        gps = new GPSTracker(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 1, this);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        gps.getLocation();
        lat = gps.getLatitude();
        lon = gps.getLongitude();

        if(lat == 0.0 && lon == 0.0){
            showSettingsAlert();
        }
        map.setMyLocationEnabled(true);

        LatLng loc = new LatLng(lat, lon);
        map.moveCamera(CameraUpdateFactory.newLatLng(loc));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
        // map.addMarker(new MarkerOptions().position(loc).title("Your Location"));

        LatLng zoneLocation = new LatLng(AppPreferences.getRadiusLatitude(ShowMapsActivity.this)
                , AppPreferences.getRadiusLongitude(ShowMapsActivity.this));

        drawMarkerWithCircle(zoneLocation);

    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = AppPreferences.getRadius(ShowMapsActivity.this);
        int strokeColor = 0xff0000ff; //red outline
        int shadeColor = 0x440000ff; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
        Circle mCircle = map.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position);
        //Marker mMarker = map.addMarker(markerOptions);
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.d("change Location", lat + " : " + lon);
        LatLng loc = new LatLng(lat, lon);
        map.moveCamera(CameraUpdateFactory.newLatLng(loc));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    public void showSettingsAlert() {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowMapsActivity.this);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to enable it in settings?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.setShowDialog(ShowMapsActivity.this, false);
    }
}
