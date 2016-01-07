package com.taxicentral.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.taxicentral.Adapter.TripAdapter;
import com.taxicentral.BuildConfig;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.GetTripServices;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.Function;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzir.runOnUiThread;

public class MainScreen extends Fragment {

    public static MainScreen instance = null;
    static ImageView gps_icon;
    static ImageView wifi;
    TextView driver_id_tv, version_code_tv;
    ListView trip_listview;
    TripAdapter tripAdapter;
    ArrayList<Trip> tripList;
    DBAdapter db;
    MyResultReceiver resultReceiver;
    android.app.AlertDialog alertDialog;

    public MainScreen() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        instance = this;
        gps_icon = (ImageView) rootView.findViewById(R.id.gps_icon);
        wifi = (ImageView) rootView.findViewById(R.id.wifi);
        driver_id_tv = (TextView) rootView.findViewById(R.id.driver_id_tv);
        version_code_tv = (TextView) rootView.findViewById(R.id.version_code_tv);
        trip_listview = (ListView) rootView.findViewById(R.id.trip_listview);

        db = new DBAdapter(getActivity());

        resultReceiver = new MyResultReceiver(null);
        Intent intentGetTripServices = new Intent(getActivity(), GetTripServices.class);
        intentGetTripServices.putExtra("receiver", resultReceiver);
        getActivity().startService(intentGetTripServices);


        if (Function.CheckGpsEnableOrNot(getActivity())) {
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
        }

        driver_id_tv.setText("#" + AppPreferences.getDriverId(getActivity()));
        version_code_tv.setText("V" + BuildConfig.VERSION_NAME);

        tripList = new ArrayList<Trip>();

        alertDialog = new android.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(getString(R.string.how_to_pay_payment));
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setCancelable(false);
        if (!tripList.isEmpty()) {
            CheckPermission();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateListview();
    }

    private void CheckPermission() {
        try {
            if (AppPreferences.getTripId(getActivity()).equalsIgnoreCase("")) {
                if (AppPreferences.getPermission(getActivity()) == 0) {
                    alertDialog.show();
                } else {
                    alertDialog.dismiss();
                }
            }
        } catch (Exception e) {
        }
    }

    public void updateListview() {
        tripList.clear();
        tripList = db.getTrip();
        tripAdapter = new TripAdapter(getActivity(), tripList);
        trip_listview.setAdapter(tripAdapter);
    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        public NetworkChangeReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (wifi != null) {
                if (Function.isOnline(instance)) {
                    wifi.setImageResource(R.drawable.ic_action_signal);
                    wifi.getDrawable().setColorFilter(instance.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    wifi.invalidate();
                } else {
                    wifi.setImageResource(R.drawable.ic_action_signal);
                    wifi.getDrawable().clearColorFilter();
                    wifi.invalidate();
                }
            }
        }
    }

    public static class GpsLocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
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
        }
    }

    class UpdateUI implements Runnable {
        int resultCode;

        public UpdateUI(int resultCode, Trip trip) {
            this.resultCode = resultCode;
        }

        public UpdateUI(int resultCode) {
            this.resultCode = resultCode;
        }

        public void run() {
            if (resultCode == 200) {
                //  Toast.makeText(getActivity(), R.string.new_trip, Toast.LENGTH_LONG).show();
                updateListview();
                CheckPermission();
            } else if (resultCode == 100) {
                CheckPermission();
            }
        }
    }

    class MyResultReceiver extends ResultReceiver {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 200) {
                runOnUiThread(new UpdateUI(resultCode, resultData.<Trip>getParcelable("trip")));

            }
            if (resultCode == 100) {
                runOnUiThread(new UpdateUI(resultCode));

            }
        }
    }

}
