package com.taxicentral.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.Point;
import com.taxicentral.Utils.Polygon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowMapsActivity extends FragmentActivity  implements LocationListener {
    //aa
    GoogleMap map;
    LocationManager locationManager;
    double lat, lon;
    ArrayList<LatLng> vertices;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);*/



        gps = new GPSTracker(this);
        this.setFinishOnTouchOutside(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, this);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        gps.getLocation();
        lat = gps.getLatitude();
        lon = gps.getLongitude();
        Log.d("change Location1", lat + " : " + lon);
        if(lat == 0.0 && lon == 0.0){
            showSettingsAlert();
        }
        map.setMyLocationEnabled(true);

        LatLng loc = new LatLng(lat, lon);
        map.moveCamera(CameraUpdateFactory.newLatLng(loc));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        // map.addMarker(new MarkerOptions().position(loc).title("Your Location"));


        /* Circle */
        /*LatLng zoneLocation = new LatLng(AppPreferences.getRadiusLatitude(ShowMapsActivity.this)
                , AppPreferences.getRadiusLongitude(ShowMapsActivity.this));

        drawMarkerWithCircle(zoneLocation);*/
        /*Circle*/

        /*tr*/


        vertices = new ArrayList<LatLng>();


        String verticesString = AppPreferences.getVertices(ShowMapsActivity.this).replace("[", "").replace("]","").trim();

        String vertiess[] = verticesString.split(",");
        Log.d("vertiess", verticesString);
        for(int i=0; i<vertiess.length; i++){
            Log.d("vertiess", vertiess[i]);
            String latlng[] = vertiess[i].split("/");
//            Log.d("vertiesslat", latlng[0]+ ":" +latlng[1]);
            try {
                vertices.add(new LatLng(new Double(latlng[0]), new Double(latlng[1])));
            }catch (NumberFormatException e){
                vertices.add(new LatLng(0.0,0.0));
            }
        }
        String latlng[] = vertiess[0].split("/");
        try {
            vertices.add(new LatLng(new Double(latlng[0]), new Double(latlng[1])));
        }catch (NumberFormatException e){
            vertices.add(new LatLng(0.0,0.0));
        }
    PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(vertices);
        lineOptions.width(5);
        lineOptions.color(Color.BLUE);
        map.addPolyline(lineOptions);

    }



    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.d("change Location", lat + " : " + lon);
       // Toast.makeText(ShowMapsActivity.this,lat+ " : "+ lon, Toast.LENGTH_LONG).show();
        LatLng loc = new LatLng(lat, lon);

       // map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
        //map.animateCamera(CameraUpdateFactory.zoomTo(15));
       // map.addMarker(new MarkerOptions().position(loc).title("Your Location"));

        if(!AppPreferences.isShowDialog(ShowMapsActivity.this)){
            finish();
        }


        boolean sta = isPointInPolygon(loc, vertices);
        Log.d("inside", sta + "");
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

    private boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = AppPreferences.getRadius(ShowMapsActivity.this);
        int strokeColor = 0xff0000ff; //red outline
        int shadeColor = 0x440000ff; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).strokeColor(strokeColor).strokeWidth(2);
        Circle mCircle = map.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position);
        //Marker mMarker = map.addMarker(markerOptions);
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
       // AppPreferences.setShowDialog(ShowMapsActivity.this, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
