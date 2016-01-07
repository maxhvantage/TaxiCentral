package com.taxicentral.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DialogManager;
import com.taxicentral.Utils.DirectionsJSONParser;
import com.taxicentral.Utils.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TripDetailsActivity extends AppCompatActivity {
    //aa
    Button btn_reject, btn_accept, btn_discard;
    public static TripDetailsActivity instance = null;
    Trip trip;
    TextView km_hours_tv;
    DialogManager dialogManager;
    GPSTracker gps;
    CardView btn_reject_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = this;
        dialogManager = new DialogManager();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        km_hours_tv = (TextView) findViewById(R.id.km_hours_tv);
        TextView agreement_tv = (TextView) findViewById(R.id.agreement_tv);
        TextView agreement = (TextView) findViewById(R.id.agreement);
        TextView fare_tv = (TextView) findViewById(R.id.fare_tv);
        TextView source_address = (TextView) findViewById(R.id.source_address_tv);
        TextView destination_address = (TextView) findViewById(R.id.destination_address_tv);
        TextView pickup_time = (TextView) findViewById(R.id.pickup_time_tv);
        ImageView img_app = (ImageView) findViewById(R.id.img_app);
        ImageView img_corporate = (ImageView) findViewById(R.id.img_corporate);
        ImageView img_webportal = (ImageView) findViewById(R.id.img_webportal);
        ImageView img_taxicompany = (ImageView) findViewById(R.id.img_taxicompany);
        btn_reject_card = (CardView) findViewById(R.id.btn_reject_card);

        btn_reject = (Button) findViewById(R.id.btn_reject);
        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_discard = (Button) findViewById(R.id.btn_discard);

        btn_reject.setOnClickListener(reject);
        btn_accept.setOnClickListener(accepct);
        btn_discard.setOnClickListener(discard);

        gps = new GPSTracker(this);

        trip = getIntent().getParcelableExtra("tripDetails");

Log.d("latlngg so", trip.getSourceLatitude()+" : "+ trip.getSourcelogitude());
        Log.d("latlngg de", trip.getDestinationLatitude()+" : "+ trip.getDestinationLogitude());
        LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
        LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());
        String url = Function.getDirectionsUrl(sourceLocation, destinationLocation);
        RoutesDownloadTask downloadTask = new RoutesDownloadTask(TripDetailsActivity.this);
        downloadTask.execute(url);

        km_hours_tv.setText(AppPreferences.getDistanceTime(TripDetailsActivity.this));
        //AppPreferences.setDistanceTime(TripDetailsActivity.this, "");

        //km_hours_tv.setText(trip.getDistance()+" - "+ trip.getTravelTime());
        agreement.setText(trip.getAgreement());
        fare_tv.setText(getString(R.string.currency) + trip.getFare() + getString(R.string.per_km));
        source_address.setText(trip.getSourceAddress());
        destination_address.setText(trip.getDestinationAddress());
        if(AppConstants.TAXI_COMPANY.equalsIgnoreCase(trip.getTripType())){
            btn_reject_card.setVisibility(View.VISIBLE);
        }else{
            btn_reject_card.setVisibility(View.GONE);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = null ;
        try {
            date = formatter.parse(trip.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            String setDate = sdf.format(date.getTime());
            pickup_time.setText(setDate);
        }catch (Exception e){}
/*
        if(trip.getDistance().equalsIgnoreCase("") || trip.getTravelTime().equalsIgnoreCase("")){
            km_hours_tv.setText("10km - 20min");
        }
        if(trip.getFare().equalsIgnoreCase("")){
            fare_tv.setText(getString(R.string.currency) + " 12");
        }*/

        if (trip.getTripType().equalsIgnoreCase(AppConstants.CORPORATE)) {
            agreement_tv.setVisibility(View.GONE);
            agreement.setVisibility(View.GONE);
        } else {
            agreement_tv.setVisibility(View.VISIBLE);
            agreement.setVisibility(View.VISIBLE);
        }

        if (trip.getTripType().equalsIgnoreCase(AppConstants.APP)) {
            img_app.getDrawable().setColorFilter(0xddecc50f, PorterDuff.Mode.SRC_IN);
        } else if (trip.getTripType().equalsIgnoreCase(AppConstants.CORPORATE)) {
            img_corporate.getDrawable().setColorFilter(0xddecc50f, PorterDuff.Mode.SRC_IN);
        } else if (trip.getTripType().equalsIgnoreCase(AppConstants.TAXI_COMPANY)) {
            img_taxicompany.getDrawable().setColorFilter(0xddecc50f, PorterDuff.Mode.SRC_IN);
        } else if (trip.getTripType().equalsIgnoreCase(AppConstants.WEB_PORTAL)) {
            img_webportal.getDrawable().setColorFilter(0xddecc50f, PorterDuff.Mode.SRC_IN);
        }

        Log.d("corporateType", trip.getCorporateType()+"");
    }

    View.OnClickListener reject = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            final String[] message = {getString(R.string.i_am_busy_this_moment)};
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TripDetailsActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.trip_detail_reject, null);
            dialogBuilder.setView(dialogView);
            View header = inflater.inflate(R.layout.dialog_heading, null);
            ImageView icon = (ImageView) header.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.ic_launcher);
            TextView textView = (TextView) header.findViewById(R.id.text);
            textView.setText(R.string.cancel_trip);
            dialogBuilder.setCustomTitle(header);
            //dialogBuilder.setTitle(R.string.cancel_trip);
            //dialogBuilder.setIcon(R.drawable.ic_action_cancel);
            dialogBuilder.setCancelable(false);

            final AlertDialog alertDialog = dialogBuilder.create();

            final RadioGroup mRadioGroup = (RadioGroup) dialogView.findViewById(R.id.radiogroup);
            Button confirm = (Button) dialogView.findViewById(R.id.btn_confirm);
            Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);

            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    message[0] = radioButton.getText().toString();
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Function.isOnline(TripDetailsActivity.this)) {
                        new TripRejectTask(trip.getId(), message[0]).execute();
                    } else {
                        //dialogManager.showAlertDialog(TripDetailsActivity.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
                        Snackbar.make(findViewById(android.R.id.content),getString(R.string.error_check_internet_connection),Snackbar.LENGTH_LONG).show();
                    }
                    alertDialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();

        }
    };

    View.OnClickListener accepct = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_accept.setClickable(false);
            new AcceptTripTask().execute();

        }
    };

    View.OnClickListener discard = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          finish();

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private class TripRejectTask  extends AsyncTask<Void, Void, Boolean> {
        String text;
        Long tripId;
        private TripRejectTask(Long tripId, String text){
            this.tripId = tripId;
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(TripDetailsActivity.this,"");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            JSONObject object = new JSONObject();
            try {
                object.put("driverId", String.valueOf(AppPreferences.getDriverId(TripDetailsActivity.this)));
                object.put("tripId", tripId);
                object.put("message", text);
                object.put("latitude", gps.getLatitude());
                object.put("longitude", gps.getLongitude());
                object.put("dateTime", Function.getCurrentDateTime());
                String json = serviceHandler.makeServiceCall(AppConstants.TRIPREJECT, ServiceHandler.POST, object);
                Log.d("rejecttrip", json);
                if(json != null){
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getString("status").equalsIgnoreCase("200")){
                        return true;

                    }else if(jsonObject.getString("status").equalsIgnoreCase("400")) {
                        return false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                AppPreferences.setTripId(TripDetailsActivity.this,"");
                finish();
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(android.R.id.content), R.string.error_not_send_message, Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            dialogManager.stopProcessDialog();
        }
    }

    private class AcceptTripTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(TripDetailsActivity.this,"");
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {

                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("driverId", AppPreferences.getDriverId(TripDetailsActivity.this));
                jsonObject.put("tripId", trip.getId());
                jsonObject.put("latitude", gps.getLatitude());
                jsonObject.put("longitude", gps.getLongitude());
                jsonObject.put("dateTime", Function.getCurrentDateTime());
                String json = serviceHandler.makeServiceCall(AppConstants.ACCEPTTRIP, ServiceHandler.POST, jsonObject);
                if(json != null){
                    JSONObject object = new JSONObject(json);
                    if(object.getString("status").equalsIgnoreCase("100")){
                        return 100;
                    }
                    if(object.getString("status").equalsIgnoreCase("200")){
                        JSONObject jsonObj = object.getJSONObject("result");

                        String avl_credit = jsonObj.getString("avl_credit");
                        if(!avl_credit.equalsIgnoreCase("")) {
                            AppPreferences.setAvlCredit(TripDetailsActivity.this, Float.parseFloat(avl_credit));
                        }
                        return 200;
                    }
                    if(object.getString("status").equalsIgnoreCase("300")){
                        return 300;
                    }
                    if(object.getString("status").equalsIgnoreCase("400")){
                        return 400;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            if(responseCode == 100){
                btn_accept.setClickable(true);
                Snackbar.make(findViewById(android.R.id.content), R.string.trip_already_alloted,Snackbar.LENGTH_LONG).show();
                dialogManager.stopProcessDialog();
            }else if(responseCode == 200){
                AppPreferences.setTripId(TripDetailsActivity.this, String.valueOf(trip.getId()));
                AppPreferences.setStartTime(TripDetailsActivity.this, Function.getCurrentDateTime());
                Intent intent = new Intent(TripDetailsActivity.this, TripAcceptActivity.class);
                intent.putExtra("tripDetails", trip);
                startActivity(intent);
                finish();
                dialogManager.stopProcessDialog();
            }else if(responseCode == 300){
                btn_accept.setClickable(true);
                Snackbar.make(findViewById(android.R.id.content),"",Snackbar.LENGTH_LONG).show();
                dialogManager.stopProcessDialog();
            }else if(responseCode == 400){
                btn_accept.setClickable(true);
                Snackbar.make(findViewById(android.R.id.content),getString(R.string.server_not_response),Snackbar.LENGTH_LONG).show();
                dialogManager.stopProcessDialog();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppPreferences.setActivity(TripDetailsActivity.this, getClass().getName());
    }

    public class RoutesDownloadTask  extends AsyncTask<String, Void, String> {

        Context context;

        public RoutesDownloadTask(Context context){
            this.context = context;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }


        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);


        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            }catch(Exception e){
                //Log.d("Exception while downloading url", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try{
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {

                String distance = "";
                String duration = "";

                if(result.size()<1){
                    //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Traversing through all the routes
                for(int i=0;i<result.size();i++){

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
                    // Fetching all the points in i-th route
                    JSONArray jsonArray = new JSONArray();
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        if(j==0){    // Get distance from the list
                            distance = (String)point.get("distance");
                            continue;
                        }else if(j==1){ // Get duration from the list
                            duration = (String)point.get("duration");
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        try {

                            JSONObject latLng = new JSONObject();

                            latLng.put("latitude", lat);
                            latLng.put("longitude", lng);
                            jsonArray.put(latLng);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    km_hours_tv.setText(distance + " - " + duration);
                }

            }
        }

    }
}
