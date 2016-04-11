package com.taxicentral.Activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.taxicentral.Adapter.TripAdapter;
import com.taxicentral.BuildConfig;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Gcm.Config;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DialogManager;
import com.taxicentral.Utils.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainScreen extends Fragment {
    //aa
    public static MainScreen instance = null;
    public static SwipeRefreshLayout pullToRefresh;
    static ImageView gps_icon;
    static ImageView wifi;
    CardView Notrip_tv;
    TextView driver_id_tv, version_code_tv;
    ListView trip_listview;
    TripAdapter tripAdapter;
    ArrayList<Trip> tripList;
    DBAdapter db;
    DialogManager dialogManager;
    private boolean isRefreshing = true;
    GPSTracker gps;
    Handler handler = new Handler();
    public MainScreen() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        instance = this;

        try {
            dialogManager = new DialogManager();
            gps = new GPSTracker(getActivity());
            pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(
                    R.id.feed_swipe_container);
            gps_icon = (ImageView) rootView.findViewById(R.id.gps_icon);
            wifi = (ImageView) rootView.findViewById(R.id.wifi);
            driver_id_tv = (TextView) rootView.findViewById(R.id.driver_id_tv);
            version_code_tv = (TextView) rootView.findViewById(R.id.version_code_tv);
            trip_listview = (ListView) rootView.findViewById(R.id.trip_listview);
            Notrip_tv = (CardView) rootView.findViewById(R.id.Notrip_cardView);
            Notrip_tv.setVisibility(View.GONE);
            db = new DBAdapter(getActivity());

            View padding = new View(getActivity());
            trip_listview.addHeaderView(padding);

            dialogManager.showProcessDialog(getActivity(), "");
            if (Function.isConnectingToInternet(getActivity())) {



            }


   /* if (Function.CheckGpsEnableOrNot(getActivity())) {
        gps_icon.setImageResource(R.drawable.ic_action_location_2);
        gps_icon.getDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        gps_icon.invalidate();
    } else {
        gps_icon.getDrawable().clearColorFilter();
        gps_icon.invalidate();
    }

    if (Function.isOnline(getActivity())) {
        wifi.setImageResource(R.drawable.ic_action_signal);
        wifi.getDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        wifi.invalidate();
    } else {
        wifi.getDrawable().clearColorFilter();
        wifi.invalidate();
    }*/

            driver_id_tv.setText("#" + AppPreferences.getDriverId(getActivity()));
            version_code_tv.setText("V" + BuildConfig.VERSION_NAME);

            tripList = new ArrayList<Trip>();

            tripAdapter = new TripAdapter(getActivity(), tripList);
            trip_listview.setAdapter(tripAdapter);

            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if (isRefreshing) {
                        dialogManager.showProcessDialog(getActivity(), "");

                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 5000);

                        isRefreshing = false;
                    } else {
                        isRefreshing = true;
                    }
                }
            });



        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        handler.postDelayed(runnable, 5000);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {


            new getTripTask().execute();

        }
    };
 
    public void updateListview(ArrayList<Trip> trips) {

        ArrayList<Trip> tripList = db.getTrip();

        if(!tripList.isEmpty()){
            trips.add(0, tripList.get(0));
        }

        tripAdapter.updateResult(trips);
        int index = trip_listview.getFirstVisiblePosition();

        Log.d("trip_listview", index+"");


        View v = trip_listview.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        trip_listview.setSelectionFromTop(index, top);

        if(index==0){
            try {
                NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(Config.NEW_TRIP_ID);
            }catch (NullPointerException e){

            }
        }

        pullToRefresh.setRefreshing(false);
        isRefreshing = true;
        try {
            dialogManager.stopProcessDialog();
        }catch (IllegalArgumentException e){

        }

        if (trips.isEmpty()) {
            Notrip_tv.setVisibility(View.VISIBLE);
        } else {
            Notrip_tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        public NetworkChangeReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            /*if(context != null) {
                if (wifi != null) {
                    if (Function.isOnline(context)) {
                        wifi.setImageResource(R.drawable.ic_action_signal);
                        wifi.getDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                        wifi.invalidate();
                    } else {
                        wifi.setImageResource(R.drawable.ic_action_signal);
                        wifi.getDrawable().clearColorFilter();
                        wifi.invalidate();
                    }
                }
            }*/
        }
    }

    public static class GpsLocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


/*
            if(instance != null) {
                if (gps_icon != null) {
                    if (Function.CheckGpsEnableOrNot(instance)) {
                        gps_icon.setImageResource(R.drawable.ic_action_location_2);
                        gps_icon.getDrawable().setColorFilter(instance.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                        gps_icon.invalidate();
                    } else {
                        gps_icon.setImageResource(R.drawable.ic_action_location_2);
                        gps_icon.getDrawable().clearColorFilter();
                        gps_icon.invalidate();
                    }
                    if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                    }
                }
            }*/


        }
    }




    private class getTripTask extends AsyncTask<Void, Void, Void> {


        ArrayList<Trip> tripList ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tripList = new ArrayList<Trip>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            gps.getLocation();


            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("driverId", String.valueOf(AppPreferences.getDriverId(getActivity())));
                jsonObj.put("latitude", String.valueOf(gps.getLatitude()));
                jsonObj.put("longitude", String.valueOf(gps.getLongitude()));


                String json = serviceHandler.makeServiceCall(AppConstants.GETTRIP, ServiceHandler.POST, jsonObj);

                if(json != null){

                    JSONObject jsonObject = new JSONObject(json);

                    if(jsonObject.getString("status").equalsIgnoreCase("200")){
                        AppPreferences.setPermission(getActivity(), Integer.parseInt(jsonObject.getString("permission")));
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            Log.d("GetTripServices", object.getString("id")+"TripId");
                            try {
                                Trip trip = new Trip();
                                trip.setId(Long.parseLong(object.getString("id")));
                                trip.setDistance(object.getString("trip_distance") + " km");


                                trip.setAgreement(object.getString("agreement"));
                                trip.setTravelTime(object.getString("travelTime"));


                                if(object.getString("source_latitude").equalsIgnoreCase("")){
                                    trip.setSourceLatitude(0.0);
                                }else{
                                    trip.setSourceLatitude(new Double(object.getString("source_latitude")));
                                }
                                if(object.getString("source_longitude").equalsIgnoreCase("")){
                                    trip.setSourcelogitude(0.0);
                                }else{
                                    trip.setSourcelogitude(new Double(object.getString("source_longitude")));
                                }
                                if(object.getString("destination_latitude").equalsIgnoreCase("")){
                                    trip.setDestinationLatitude(0.0);
                                }else{
                                    trip.setDestinationLatitude(new Double(object.getString("destination_latitude")));
                                }
                                if(object.getString("destination_longitude").equalsIgnoreCase("")){
                                    trip.setDestinationLogitude(0.0);
                                }else{
                                    trip.setDestinationLogitude(new Double(object.getString("destination_longitude")));
                                }

                                trip.setNumber(object.getString("contact_no"));
                                trip.setCustomerName(object.getString("name"));
                                trip.setDate(object.getString("tripdatetime"));
                                trip.setTripType(object.getString("trip_type"));

                                if (object.getString("trip_type").equalsIgnoreCase(AppConstants.CORPORATE)) {
                                    //trip.setCorporateType(Integer.parseInt(object.getString("corporateType")));
                                    trip.setCorporateType(0);
                                    trip.setSourceAddress(object.getString("source_landmark"));
                                    trip.setDestinationAddress(object.getString("destination_landmark"));

                                    if(object.getString("trip_ammount").equalsIgnoreCase("") || object.getString("trip_ammount").equalsIgnoreCase("null")){
                                        trip.setFare("50");
                                    }else{
                                        trip.setFare(object.getString("trip_ammount"));
                                    }

                                }else{
                                    trip.setSourceAddress(object.getString("source_address"));
                                    trip.setDestinationAddress(object.getString("destination_address"));
                                    if(object.getString("price_perkm").equalsIgnoreCase("") || object.getString("price_perkm").equalsIgnoreCase("null")){
                                        trip.setFare("10");
                                    }else{
                                        trip.setFare(object.getString("price_perkm"));
                                    }
                                }

                                tripList.add(trip);

                            }catch (Exception e){
                                Log.d("Exception", e.getMessage());
                            }

                        }


                    }else{

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
            handler.postDelayed(runnable, 10000);
            Log.d("GetTripServices", "onPostExecute");

            updateListview(tripList);
        }
    }
}
