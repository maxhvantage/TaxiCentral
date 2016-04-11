package com.taxicentral.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import android.provider.Settings.Secure;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.taxicentral.Activity.LoginActivity;
import com.taxicentral.Activity.NavigationDrawer;
import com.taxicentral.Activity.TaxiWaitingActivity;
import com.taxicentral.Classes.FusedLocationService;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Model.Trip;
import com.taxicentral.Model.User;
import com.taxicentral.R;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateLocationToServer extends Service  {
    String TAG = "UpdateLocationToServer";
    DBAdapter db;
    static final long TIME_FOR_1_SECOND = 5000;
    FusedLocationService fusedLocationService;
    Handler handler = new Handler();

    public UpdateLocationToServer() {

    }

    @Override
    public void onCreate() {
        super.onCreate();


        db = new DBAdapter(UpdateLocationToServer.this);

        fusedLocationService = new FusedLocationService(UpdateLocationToServer.this);

        handler.postDelayed(runnable, TIME_FOR_1_SECOND);

        AppPreferences.setBookingSpeed(getApplicationContext(), "1.0");


    }


    final Handler mHandler = new Handler(Looper.getMainLooper());

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Location location = fusedLocationService.getLocation();



            String speed = AppPreferences.getBookingSpeed(UpdateLocationToServer.this);

           /* final String finalSpeed = speed;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),  finalSpeed +" : Speed ", Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }).start();*/

            if(speed.equalsIgnoreCase("0")){
                Log.d(TAG, "000");
                handler.postDelayed(runnable, TIME_FOR_1_SECOND);
            }else if (location != null && Function.isOnline(UpdateLocationToServer.this) && location.getLatitude() != 0.0
                    && AppPreferences.getDriverId(UpdateLocationToServer.this) != 0) {
               //AppPreferences.setBookingSpeed(getApplicationContext(),"0");
                Log.d(TAG, "online");

                ArrayList<User> locationList = db.getLocation();
                if (locationList.isEmpty()) {

                        new updateLocationTask(location.getLatitude(), location.getLongitude()).execute();

                } else {
                    for (int i = 0; i < locationList.size(); i++) {
                       // new updateLocationTask(locationList.get(i)).execute();
                        db.deleteLocation(Integer.parseInt(String.valueOf(locationList.get(i).getId())));
                    }
                }
            } /*else  if (location != null ){
                Log.d(TAG, "ofline");

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //date = df.format(calendar.getTime());
                User user = new User();
                user.setCurrentLatitude(location.getLatitude());
                user.setCurrentLongitude(location.getLongitude());
                user.setAddedOn(df.format(calendar.getTime()));
                db.insertLocation(user);
                handler.postDelayed(runnable, TIME_FOR_1_SECOND);
            }*/else{
                handler.postDelayed(runnable, TIME_FOR_1_SECOND);
            }
        }
    };


    private class updateLocationTask extends AsyncTask<Void, Void, Void> {


        int id=0;
        String latitude, logitude, date;

        public updateLocationTask(double latitude, double logitude){
            this.latitude = String.valueOf(latitude);
            this.logitude = String.valueOf(logitude);
            date = Function.getCurrentDateTime();
        }

        public updateLocationTask(User userLocation) {
            this.latitude = String.valueOf(userLocation.getCurrentLatitude());
            this.logitude = String.valueOf(userLocation.getCurrentLongitude());
            date = userLocation.getAddedOn();
            id = Integer.parseInt(String.valueOf(userLocation.getId()));
        }

        @Override
        protected Void doInBackground(Void... params) {
            String android_id="";

                    android_id = Secure.getString(getContentResolver(),
                    Secure.ANDROID_ID);

            String android_name = getDeviceName();

            Log.d(TAG, "is Running");
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("driverId", AppPreferences.getDriverId(UpdateLocationToServer.this));
                jsonObj.put("tripId", AppPreferences.getTripId(UpdateLocationToServer.this));
                jsonObj.put("latitude", latitude);
                jsonObj.put("longitude", logitude);
                jsonObj.put("date", date);
                jsonObj.put("android_id",android_id);
                jsonObj.put("inout", AppPreferences.getCheckzone(UpdateLocationToServer.this));

               String json = serviceHandler.makeServiceCall(AppConstants.UPDATELOCATION, ServiceHandler.POST, jsonObj);
                Log.d("locationUpdate", json);
                if(json != null){
                    JSONObject object = new JSONObject(json);
                    if(object.getString("status").equalsIgnoreCase("200")){

                        JSONObject jsonObject = object.getJSONObject("result");

                        if(!jsonObject.getString("android_id").equalsIgnoreCase(android_id)){
                            stopService(new Intent(UpdateLocationToServer.this, ZoneControlServices.class));
                            stopService(new Intent(UpdateLocationToServer.this, UpdateLocationToServer.class));
                            ////stopService(new Intent(instance, GetTripServices.class));
                            ZoneControlServices.stopZoneControlServices = true;
                            //AppPreferences.setShowDialog(instance, false);
                            // AppPreferences.setVertices(instance, new ArrayList<String>());
                            ////GetTripServices.shouldContinue = false;
                            AppPreferences.setDriverId(UpdateLocationToServer.this, 0);
                            Intent intent = new Intent(UpdateLocationToServer.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction("autoLogout");
                            startActivity(intent);
                        }




                        if(id !=0) {
                            db.deleteLocation(id);
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
            handler.postDelayed(runnable, TIME_FOR_1_SECOND);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Stop");
//        timer.cancel();
  //      task.cancel();
        stopSelf();

    }
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }


}
