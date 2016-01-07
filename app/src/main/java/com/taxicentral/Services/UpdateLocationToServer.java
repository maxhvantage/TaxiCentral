package com.taxicentral.Services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

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

public class UpdateLocationToServer extends Service {
    String TAG = "UpdateLocationToServer";
    GPSTracker gps;
    private Timer timer;
    private TimerTask task;
    DBAdapter db;

    public UpdateLocationToServer() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new GPSTracker(this);


        db = new DBAdapter(UpdateLocationToServer.this);

        int delay = 60000; // delay for 5 sec.
        int period = 60000; // repeat every sec.

        timer = new Timer();

        timer.scheduleAtFixedRate(task = new TimerTask() {
            public void run() {

                if(Function.isOnline(UpdateLocationToServer.this)) {
                    ArrayList<User> locationList = db.getLocation();
                    if(locationList.isEmpty()) {
                        new updateLocationTask().execute();
                    }else{
                        for(int i=0; i<locationList.size(); i++){
                            new updateLocationTask(locationList.get(i)).execute();
                        }
                    }
                }else{
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //date = df.format(calendar.getTime());
                    User user = new User();
                    user.setCurrentLatitude(gps.getLatitude());
                    user.setCurrentLongitude(gps.getLongitude());
                    user.setAddedOn(df.format(calendar.getTime()));
                    db.insertLocation(user);
                }
            }
        }, delay, period);

    }



    private class updateLocationTask extends AsyncTask<Void, Void, Void> {


        int id=0;
        String latitude, logitude, date;

        public updateLocationTask(){
            latitude = String.valueOf(gps.getLatitude());
            logitude = String.valueOf(gps.getLongitude());
            date = Function.getCurrentDateTime();
        }

        public updateLocationTask(User userLocation) {
            latitude = String.valueOf(userLocation.getCurrentLatitude());
            logitude = String.valueOf(userLocation.getCurrentLongitude());
            date = userLocation.getAddedOn();
            id = Integer.parseInt(String.valueOf(userLocation.getId()));
        }

        @Override
        protected Void doInBackground(Void... params) {


            gps.getLocation();

            Log.d(TAG, "is Running");
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("driverId", AppPreferences.getDriverId(UpdateLocationToServer.this));
                jsonObj.put("tripId", AppPreferences.getTripId(UpdateLocationToServer.this));
                jsonObj.put("latitude", latitude);
                jsonObj.put("longitude", logitude);
                jsonObj.put("date", date);

               String json = serviceHandler.makeServiceCall(AppConstants.UPDATELOCATION, ServiceHandler.POST, jsonObj);
                Log.d("locationUpdate", json);
                if(json != null){
                    JSONObject object = new JSONObject(json);
                    if(object.getString("status").equalsIgnoreCase("200")){
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
        timer.cancel();
        task.cancel();
        stopSelf();
    }

}
