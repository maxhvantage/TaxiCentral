package com.taxicentral.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.taxicentral.Activity.AlertDialogActivity;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Gcm.Config;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ZoneControlServices extends Service implements LocationListener {
    String TAG = "ZoneControlServices";
    GPSTracker gps;
    private Timer timer;
    private TimerTask task;
    ArrayList<LatLng> vertices;
    LocationManager locationManager;
    boolean showAlertDialog = true;
    public static boolean stopZoneControlServices = false;
    ArrayList<LatLng> polyLoc;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, this);

        vertices = new ArrayList<LatLng>();
        /* vertices.add(new LatLng(22.73007586, 75.87924242));
        vertices.add(new LatLng(22.72390087, 75.87314844));
        vertices.add(new LatLng(22.72144663, 75.88194609));
        vertices.add(new LatLng(22.72738423, 75.88748217));
        vertices.add(new LatLng(22.72544464, 75.88047624));
        vertices.add(new LatLng(22.73007586, 75.87924242));*/
        /*String verticesString = intent.getStringExtra("vertices").replace("[", "").replace("]","").trim();//AppPreferences.getVertices(ZoneControlServices.this).replace("[", "").replace("]","").trim();

        String vertiess[] = verticesString.split(",");
        Log.d("vertiess", verticesString);
        for(int i=0; i<vertiess.length; i++){
            Log.d("vertiess", vertiess[i]);
            String latlng[] = vertiess[i].split("/");
            Log.d("vertiesslat", latlng[0]+ ":" +latlng[1]);
            vertices.add(new LatLng(new Double(latlng[0]), new Double(latlng[1])));
        }
        String latlng[] = vertiess[0].split("/");
        vertices.add(new LatLng(new Double(latlng[0]), new Double(latlng[1])));*/

        /*String verticesString = AppPreferences.getVertices(ZoneControlServices.this).replace("[", "").replace("]","").trim();

        String vertiess[] = verticesString.split(",");
        Log.d("vertiess", verticesString);
        for(int i=0; i<vertiess.length; i++){
            Log.d("vertiess", vertiess[i]);
            String latlng[] = vertiess[i].split("/");
//            Log.d("vertiesslat", latlng[0]+ ":" +latlng[1]);
            vertices.add(new LatLng(new Double(latlng[0]), new Double(latlng[1])));
        }
        String latlng[] = vertiess[0].split("/");
        vertices.add(new LatLng(new Double(latlng[0]), new Double(latlng[1])));*/


        return super.onStartCommand(intent, flags, startId);
    }

    public ZoneControlServices() {


    }

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new GPSTracker(this);
        gps.getLocation();



    }

    private void notifyDriver() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Zone Control")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentText(getResources().getString(R.string.txt_out_of_zone));

        builder.setOnlyAlertOnce(true);

        Intent notificationIntent = new Intent(this, AlertDialogActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);

        if(showAlertDialog){
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(notificationIntent);
            showAlertDialog = false;
        }


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(Config.OUTOFZONE_ID, builder.build());
    }

    // Remove notification
    private void removeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(0);
    }


    private boolean updateLocation(double aoiLat, double aoiLong) {

        Log.d(TAG, "is Running");
        Location areaOfIinterest = new Location("A");
        Location currentPosition = new Location("B");

        areaOfIinterest.setLatitude(aoiLat);
        areaOfIinterest.setLongitude(aoiLong);

        currentPosition.setLatitude(gps.getLatitude());
        currentPosition.setLongitude(gps.getLongitude());

        float dist = areaOfIinterest.distanceTo(currentPosition);
        Log.d(TAG, "dist : " + dist);
        return (dist < AppPreferences.getRadius(ZoneControlServices.this));

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Stop Zone");
  //      timer.cancel();
//        task.cancel();
        stopSelf();
    }

    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);

    }

    @Override
    public void onLocationChanged(Location location) {

        if(stopZoneControlServices){
            stopSelf();
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(Config.OUTOFZONE_ID);
        }else {
            String verticesString = AppPreferences.getVertices(ZoneControlServices.this).replace("[", "").replace("]", "").trim();

            vertices = new ArrayList<LatLng>();

            String vertiess[] = verticesString.split(",");


            polyLoc = new ArrayList<LatLng>();
            for (int i = 0; i < vertiess.length; i++) {
                String latlng1[] = vertiess[i].split("/");

                try {
                    polyLoc.add(new LatLng(new Double(latlng1[0]), new Double(latlng1[1])));
                }catch (NumberFormatException e){}

            }
            String latlng1[] = vertiess[0].split("/");

            try {
                polyLoc.add(new LatLng(new Double(latlng1[0]), new Double(latlng1[1])));
            }catch (NumberFormatException e){
                polyLoc.add(new LatLng(0.0, 0.0));
            }

            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            boolean value = Contains(loc);
            if (value) {
                Log.d("zone", value + ": inside:");
                AppPreferences.setShowDialog(this, false);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(10);
                AppPreferences.setCheckzone(this, "In");
                showAlertDialog = true;
            } else {
                Log.d("zone", value + ": outside:");
                notifyDriver();
                AppPreferences.setShowDialog(this, true);
                AppPreferences.setCheckzone(this, "Out");

            }

        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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

    public boolean Contains(LatLng location)
    {
        if (location==null)
            return false;

        LatLng lastPoint = polyLoc.get(polyLoc.size()-1);
        boolean isInside = false;
        double x = location.longitude;

        for(LatLng point: polyLoc)
        {
            double x1 = lastPoint.longitude;
            double x2 = point.longitude;
            double dx = x2 - x1;

            if (Math.abs(dx) > 180.0)
            {
                // we have, most likely, just jumped the dateline (could do further validation to this effect if needed).  normalise the numbers.
                if (x > 0)
                {
                    while (x1 < 0)
                        x1 += 360;
                    while (x2 < 0)
                        x2 += 360;
                }
                else
                {
                    while (x1 > 0)
                        x1 -= 360;
                    while (x2 > 0)
                        x2 -= 360;
                }
                dx = x2 - x1;
            }

            if ((x1 <= x && x2 > x) || (x1 >= x && x2 < x))
            {
                double grad = (point.latitude - lastPoint.latitude) / dx;
                double intersectAtLat = lastPoint.latitude + ((x - x1) * grad);

                if (intersectAtLat > location.latitude)
                    isInside = !isInside;
            }
            lastPoint = point;
        }

        return isInside;
    }

}
