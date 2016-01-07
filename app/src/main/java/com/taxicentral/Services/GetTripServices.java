package com.taxicentral.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.taxicentral.Activity.NotificationActivity;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GetTripServices extends IntentService {
    String TAG = "GetTripServices";
    GPSTracker gps;
    private Timer timer;
    private TimerTask task;
    DBAdapter db;
    ArrayList<Trip> TripList;
    ArrayList<Integer> tripIdList;
    ArrayList<Long> idList;
    ResultReceiver resultReceiver;
    public static volatile boolean shouldContinue = true;

    public GetTripServices() {
        super(GetTripServices.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = new DBAdapter(GetTripServices.this);
        gps = new GPSTracker(this);
        gps.getLocation();
        TripList = new ArrayList<Trip>();
        idList = new ArrayList<Long>();




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        resultReceiver = intent.getParcelableExtra("receiver");
        return super.onStartCommand(intent, flags, startId);

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        tripIdList = new ArrayList<Integer>();
        tripIdList = db.getTripId();

        for (int j = 0; j < tripIdList.size(); j++) {

            if (AppPreferences.getTripId(GetTripServices.this).equalsIgnoreCase(String.valueOf(tripIdList.get(j)))) {

            } else {

                boolean statu = db.deleteTrip(tripIdList.get(j));
                Log.d("dbb", tripIdList.get(j) + " : " + statu);
            }
        }
        new getTripTask().execute();

    }

    @Override
    protected void onHandleIntent(Intent intent) {



            int delay = 60000;
            int period = 60000;

            timer = new Timer();

        timer.scheduleAtFixedRate(task = new TimerTask() {
            public void run() {
                
                if (shouldContinue == true) {
                    tripIdList = new ArrayList<Integer>();
                    tripIdList = db.getTripId();

                    for (int j = 0; j < tripIdList.size(); j++) {

                        if (AppPreferences.getTripId(GetTripServices.this).equalsIgnoreCase(String.valueOf(tripIdList.get(j)))) {

                        } else {

                            boolean statu = db.deleteTrip(tripIdList.get(j));
                            Log.d("dbb", tripIdList.get(j) + " : " + statu);
                        }
                    }


                    /*TripList = db.getTrip();
                    idList.clear();
                    for(int i=0; i<TripList.size(); i++){
                        idList.add(TripList.get(i).getId());
                    }*/

                    new getTripTask().execute();
                }
                if (shouldContinue == false) {
                    timer.cancel();
                    task.cancel();
                    stopSelf();
                    return;
                }
            }
        }, delay, period);


    }

    private class getTripTask extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... params) {
            gps.getLocation();

            Log.d(TAG, "is Running");
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("driverId", String.valueOf(AppPreferences.getDriverId(GetTripServices.this)));
                jsonObj.put("latitude", String.valueOf(gps.getLatitude()));
                jsonObj.put("longitude", String.valueOf(gps.getLongitude()));


                String json = serviceHandler.makeServiceCall(AppConstants.GETTRIP, ServiceHandler.POST, jsonObj);

                if(json != null){

                    JSONObject jsonObject = new JSONObject(json);

                    if(jsonObject.getString("status").equalsIgnoreCase("200")){
                        AppPreferences.setPermission(GetTripServices.this, Integer.parseInt(jsonObject.getString("permission")));
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            try {
                                Trip trip = new Trip();
                                trip.setId(Long.parseLong(object.getString("id")));
                                trip.setDistance(object.getString("trip_distance") + " km");
                                if(object.getString("price_perkm").equalsIgnoreCase("") || object.getString("price_perkm").equalsIgnoreCase("null")){
                                    trip.setFare("5");
                                }else{
                                    trip.setFare(object.getString("price_perkm"));
                                }

                                trip.setAgreement(object.getString("agreement"));
                                trip.setTravelTime(object.getString("travelTime"));


                                trip.setSourceLatitude(new Double(object.getString("source_latitude")));
                                trip.setSourcelogitude(new Double(object.getString("source_longitude")));
                                trip.setDestinationLatitude(new Double(object.getString("destination_latitude")));
                                trip.setDestinationLogitude(new Double(object.getString("destination_longitude")));
                                trip.setNumber(object.getString("contact_no"));
                                trip.setCustomerName(object.getString("name"));
                                trip.setDate(object.getString("tripdatetime"));
                                trip.setTripType(object.getString("trip_type"));

                                if (object.getString("trip_type").equalsIgnoreCase(AppConstants.CORPORATE)) {
                                    //trip.setCorporateType(Integer.parseInt(object.getString("corporateType")));
                                    trip.setCorporateType(0);
                                    trip.setSourceAddress(object.getString("source_landmark"));
                                    trip.setDestinationAddress(object.getString("destination_landmark"));
                                }else{
                                    trip.setSourceAddress(object.getString("source_address"));
                                    trip.setDestinationAddress(object.getString("destination_address"));
                                }
                                if(!AppPreferences.getTripId(GetTripServices.this).equalsIgnoreCase(object.getString("id"))){
                                    db.insertTrip(trip);
                                }

                                Bundle bundle = new Bundle();
                                bundle.putParcelable("trip", trip);
                                resultReceiver.send(200, bundle);

                                if(!tripIdList.contains(Integer.parseInt(String.valueOf(trip.getId())))){
                                    Log.d("dbb",trip.getId()+" : new");
                                    sendNotification("You have get a new trip");
                                }
                            }catch (Exception e){
                                Log.d("Exception", e.getMessage());
                            }

                            /*Log.d(TAG, Long.parseLong(object.getString("id")) + "");
                            if (!idList.contains(Long.valueOf(object.getString("id")))) {
                                db.insertTrip(trip);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("trip", trip);
                                resultReceiver.send(200, bundle);
                            }else{
                                Bundle bundle = new Bundle();
                                resultReceiver.send(100, bundle);
                            }*/
                        }
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ServiceTask", "Stop");
        //timer.cancel();
       // task.cancel();
        //stopSelf();
    }

    private void sendNotification(String msg) {
        NotificationManager mNotificationManager = (NotificationManager) this
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
        mNotificationManager.notify(0, mBuilder.build());
    }
}
