package com.taxicentral.Gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.taxicentral.Activity.AlertDialogActivity;
import com.taxicentral.Activity.MainScreen;
import com.taxicentral.Activity.NavigationDrawer;
import com.taxicentral.Activity.NotificationActivity;
import com.taxicentral.Activity.RecivedMessage;
import com.taxicentral.Activity.ShowMapsActivity;
import com.taxicentral.Activity.TaxiWaitingActivity;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Model.NotificationModel;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "GCMNotificationIntentService";
    NotificationCompat.Builder builder;
    DBAdapter db;
    private NotificationManager mNotificationManager;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        Log.d("GCM:::::", extras.toString()+":: extra");
        db = new DBAdapter(GCMNotificationIntentService.this);
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        Log.d("GCM:::::", messageType+":: messageType ");

        if (!extras.isEmpty()) {



            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                //sendNotification("Deleted messages on server: "+ extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {

                String registerMessage = "" + extras.get(Config.LOGIN);
                String tripCancel = "" + extras.get(Config.TRIP_CANCEL);
                String reciveMessage = "" + extras.get(Config.TRIP_RECIVE_MSG);
                String boarded = "" + extras.get(Config.TRIP_BOARDED);
                String newTrip = "" + extras.get(Config.NEW_TRIP);
                String adminMessage = "" + extras.get(Config.ADMIN_MSG);
                String outOfZone = "" + extras.get(Config.OUTOFZONE);
                String zoneUpdate = "" + extras.get(Config.ZONEUPDATE);
                String paymentMode = "" + extras.get(Config.PAYMENTMODE);

                Log.d("NOTIFICATION ", "registerMessage:" + registerMessage + "\ntripCancel: "
                        + tripCancel + " \nreciveMessage: " + reciveMessage + " \nboarded: "
                        + boarded + " \nnewTrip: " + newTrip + " \nadminMessage: " + adminMessage
                        + " \noutOfZone:"+outOfZone+" \nzoneUpdate:"+zoneUpdate+" \npaymentMode:"+paymentMode);

                try {
                    if (!registerMessage.equalsIgnoreCase("null")) {
                        //sendNotification(registerMessage);
                    } else if (!tripCancel.equalsIgnoreCase("null")) {
                        if(!AppPreferences.getTripId(this).equalsIgnoreCase("")){
                            tripCancelNotify(tripCancel);
                        }

                    } else if (!reciveMessage.equalsIgnoreCase("null")) {

                        tripReciveMessage(reciveMessage);
                    } else if (!boarded.equalsIgnoreCase("null")) {
                        tripBoardedNotify(boarded);
                    } else if (!newTrip.equalsIgnoreCase("null")) {
                        newTripNotify(newTrip);
                    } else if (!adminMessage.equalsIgnoreCase("null")) {

                        adminMessageNotify(adminMessage);

                    } else if (!outOfZone.equalsIgnoreCase("null")) {

                        outOfZoneNotify(outOfZone);

                    }else if (!zoneUpdate.equalsIgnoreCase("null")) {

                        zoneUpdateNotify(zoneUpdate);

                    }else if (!paymentMode.equalsIgnoreCase("null")) {

                        paymentModeNotify(paymentMode);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void paymentModeNotify(String paymentMode) {
       // credit
         //       cash


        AppPreferences.setPaymentmode(this,paymentMode);
    }

    private void zoneUpdateNotify(String zoneUpdate) throws JSONException {
        ArrayList<String> vertices = new ArrayList<String>();
        JSONObject jsonObj = new JSONObject(zoneUpdate);
        String cordinated = jsonObj.getString("cordinated");
        Log.d("cordinated", cordinated);
        String cordinated1 = cordinated.replace("(","").replace(")","").replace(" ","");
        String cortinat[] = cordinated1.split(",");
        for(int i=0;i<cortinat.length; i++){
            String lat = cortinat[i];
            Log.d("cordinated", lat.toString());
            i++;
            String lng = cortinat[i];
            Log.d("cordinated", lng.toString());
            vertices.add(lat+"/"+lng);
        }
        Log.d("cordinated", vertices.toString());

        AppPreferences.setVertices(this, vertices);

    }

    private void outOfZoneNotify(String outOfZone) throws JSONException {
        JSONObject jsonObject = new JSONObject(outOfZone);


        Log.d("outOfZone", outOfZone + "\n" + jsonObject.getString("status"));

        if(jsonObject.getString("status").equalsIgnoreCase("400")){
        }else{
        }
    }

    private void adminMessageNotify(String adminMessage) throws JSONException {

        JSONObject jsonObject = new JSONObject(adminMessage);

        NotificationModel notification = new NotificationModel();
        notification.setId(Integer.parseInt(jsonObject.getString("id")));
        notification.setHeader("Admin");
        notification.setDescription("Message");
        db.insertNotification(notification);

        sendNotification("You have a new message");

    }

    private void newTripNotify(String newTrip) {

        if (MainScreen.instance != null) {

        }

        sendNotificationNewTrip(newTrip);


    }

    private void tripBoardedNotify(String boarded) throws JSONException {

        final Handler mHandler = new Handler(Looper.getMainLooper());


            JSONObject jsonObject = new JSONObject(boarded);
            String alreadyBarded = jsonObject.getString("message");

            if (AppPreferences.getActivityopen(this)) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                TaxiWaitingActivity.fabBoarded.performClick();
                            }
                        });

                    }
                }).start();

            } else {
                AppPreferences.setActivityresumeopen(this, alreadyBarded);

            }



    }

    private void tripReciveMessage(String reciveMessage) throws JSONException {

            JSONObject jsonObject = new JSONObject(reciveMessage);

            sendNotificationReciveMessage(jsonObject.getString("message"));

            Intent intent = new Intent(this, RecivedMessage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", jsonObject.getString("message"));
            Log.d("isActivityOpen", AppPreferences.getActivityopen(this) + "");
            if (AppPreferences.getActivityopen(this)) {
                startActivity(intent);
            } else {

                AppPreferences.setActivityresumeopen(this, jsonObject.getString("message"));
            }


    }

    public void tripCancelNotify(String tripCancel) throws JSONException {
        AppPreferences.setTripId(this, "");

            JSONObject jsonObject = new JSONObject(tripCancel);

            NotificationModel notification = new NotificationModel();
            notification.setId(Integer.parseInt(jsonObject.getString("trip_id")));
            notification.setHeader("Trip Canceled");
            notification.setDescription("Trip Id : " + jsonObject.getString("trip_id") + "\n" + jsonObject.getString("canceltaxirequest"));
            db.insertNotification(notification);

            db.deleteTrip(Long.parseLong(jsonObject.getString("trip_id")));

            sendNotification("Your trip has been cancel");

            Intent intent = new Intent(this, NavigationDrawer.class);
            intent.putExtra("notificationData", tripCancel);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(0);



    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NotificationActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Taxi Central")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(msg);


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotificationForBoarded(String msg) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, TaxiWaitingActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Taxi Central")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(msg);


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNotificationReciveMessage(String msg) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, RecivedMessage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("message", msg);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Taxi Central")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(msg);



        mBuilder.setContentIntent(contentIntent);


        mNotificationManager.notify(Config.TRIP_RECIVE_MSG_ID, mBuilder.build());

    }

    private void sendNotificationNewTrip(String msg) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NavigationDrawer.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Taxi Central")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentText(msg);

        if (MainScreen.instance == null) {
            mBuilder.setContentIntent(contentIntent);
        }
        mNotificationManager.notify(Config.NEW_TRIP_ID, mBuilder.build());
    }

    private void sendOutOfZoneNotify() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_bell)
                        .setContentTitle("Zone Control")
                        .setContentText("You are out of zone please come back to your zone.");

        Intent notificationIntent = new Intent(this, AlertDialogActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }



}
