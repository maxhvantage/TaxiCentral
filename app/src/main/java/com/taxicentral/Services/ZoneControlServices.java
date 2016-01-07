package com.taxicentral.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.taxicentral.Activity.AlertDialogActivity;
import com.taxicentral.Activity.NotificationActivity;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;

import java.util.Timer;
import java.util.TimerTask;

public class ZoneControlServices extends Service {
    String TAG = "ZoneControlServices";
    GPSTracker gps;
    private Timer timer;
    private TimerTask task;

    public ZoneControlServices() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new GPSTracker(this);
        gps.getLocation();

        int delay = 10000; // delay for 10 sec.
        int period = 10000; // repeat every sec.

        timer = new Timer();

        timer.scheduleAtFixedRate(task = new TimerTask() {
            public void run() {
                gps.getLocation();
                boolean status = updateLocation(AppPreferences.getRadiusLatitude(ZoneControlServices.this)
                        , AppPreferences.getRadiusLongitude(ZoneControlServices.this));
Log.d("zone status", status+"");
                if (!status) {
                    //notifyDriver();
                   if(!AppPreferences.isShowDialog(ZoneControlServices.this)){
                       Intent intent = new Intent(ZoneControlServices.this, AlertDialogActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       //startActivity(intent);
                    }
                }
            }
        }, delay, period);

    }

    private void notifyDriver() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_bell)
                        .setContentTitle(getString(R.string.txt_zone_control))
                        .setContentText(getString(R.string.txt_out_of_zone));

        Intent notificationIntent = new Intent(this, NotificationActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
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
        Log.d("ServiceTask", "Stop");
        timer.cancel();
        task.cancel();
        stopSelf();
    }

}
