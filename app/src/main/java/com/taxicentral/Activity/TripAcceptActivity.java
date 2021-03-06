package com.taxicentral.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DialogManager;
import com.taxicentral.Utils.DirectionsJSONParser;
import com.taxicentral.Utils.Function;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TripAcceptActivity extends AppCompatActivity   implements LocationListener {
    //aa
    DialogManager dialogManager;
    Trip trip;
    ImageView call_img;
    TextView name, number, address;
    public static TripAcceptActivity instance = null;
    FloatingActionsMenu floatingActionsMenu;
    private GoogleMap map;
    GPSTracker gps;
    double lat, lon;
    View view;
    LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_accept);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = this;

        ////GetTripServices.shouldContinue = false;

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 0, this);

        dialogManager = new DialogManager();
        gps = new GPSTracker(this);

        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);
        call_img = (ImageView) findViewById(R.id.img_call);

        call_img.setOnClickListener(call);

        trip = getIntent().getParcelableExtra("tripDetails");
        name.setText(trip.getCustomerName());
        number.setText(trip.getNumber());
        address.setText(trip.getSourceAddress());

        /* FloatingActionsMenu */

        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        FloatingActionButton fabArrived = (FloatingActionButton) findViewById(R.id.fab_arrived);
        FloatingActionButton fabMessage = (FloatingActionButton) findViewById(R.id.fab_message);
        FloatingActionButton fabCancel = (FloatingActionButton) findViewById(R.id.fab_cancel);

        fabArrived.setOnClickListener(arrived);
        fabMessage.setOnClickListener(message);
        fabCancel.setOnClickListener(cancel);

    map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    gps.getLocation();
    lat = gps.getLatitude();
    lon = gps.getLongitude();

    if (lat == 0.0 && lon == 0.0) {
        // showSettingsAlert();
    }
        if(map != null) {
            map.setMyLocationEnabled(true);
        }

        if(String.valueOf(trip.getDestinationLatitude()).equalsIgnoreCase("") || String.valueOf(trip.getDestinationLogitude()).equalsIgnoreCase("")){
            trip.setDestinationLatitude(gps.getLatitude());
            trip.setDestinationLogitude(gps.getLongitude());
        }
    LatLng currentLocation = new LatLng(lat, lon);
    final LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
    final LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());
   // map.moveCamera(CameraUpdateFactory.newLatLng(sourceLocation));
    //map.animateCamera(CameraUpdateFactory.zoomTo(11));

    map.addMarker(new MarkerOptions()
            .position(sourceLocation)
            .title(trip.getCustomerName())
            .snippet(trip.getSourceAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_three)));
    map.addMarker(new MarkerOptions()
            .position(destinationLocation)
            .title(trip.getCustomerName())
            .snippet(trip.getDestinationAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_four)));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 15));

       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               String url = Function.getDirectionsUrl(sourceLocation, destinationLocation);
               RoutesDownloadTask downloadTask = new RoutesDownloadTask(TripAcceptActivity.this);
               downloadTask.execute(url);
           }
       });





      /*  LatLng latLng = new LatLng(22.71986323087638, 75.8661961555481  );
        ArrayList<LatLng> vertices = new ArrayList<LatLng>();
        vertices.add(new LatLng(22.718952766042808, 75.86031675338745));
        vertices.add(new LatLng(22.716300507872447, 75.8570122718811));
        vertices.add(new LatLng(22.716735956770492, 75.86898565292358));
        vertices.add(new LatLng(22.718952766042808, 75.86031675338745));

        boolean sta = isPointInPolygon(latLng, vertices);
        Log.d("stataaaa", sta + "");
PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(vertices);
        lineOptions.width(5);
        lineOptions.color(Color.BLUE);

    map.addPolyline(lineOptions);*/

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







    View.OnClickListener message = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            floatingActionsMenu.collapseImmediately();

            final String[] message = {getString(R.string.five_min_late_traffic)};
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TripAcceptActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.send_message_for_accept, null);
            dialogBuilder.setView(dialogView);
            View header = inflater.inflate(R.layout.dialog_heading, null);
            ImageView icon = (ImageView) header.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.ic_action_send);
            TextView textView = (TextView) header.findViewById(R.id.text);
            textView.setText(R.string.send_message);
            dialogBuilder.setCustomTitle(header);
            //dialogBuilder.setIcon(R.drawable.ic_action_send);
            //dialogBuilder.setTitle(R.string.send_message);
            dialogBuilder.setCancelable(false);

            final AlertDialog alertDialog = dialogBuilder.create();

            final RadioGroup mRadioGroup = (RadioGroup) dialogView.findViewById(R.id.radiogroup);
            Button accept = (Button) dialogView.findViewById(R.id.btn_accept);
            Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);

            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    message[0] = radioButton.getText().toString();
                }
            });

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Function.isOnline(TripAcceptActivity.this)) {
                        new SendMSGTask(message[0]).execute();
                    } else {
                       // dialogManager.showAlertDialog(TripAcceptActivity.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
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
    View.OnClickListener cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingActionsMenu.collapseImmediately();

            final String[] message = {getString(R.string.address_does_not_exist)};
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TripAcceptActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.trip_accept_cancel, null);
            dialogBuilder.setView(dialogView);
            View header = inflater.inflate(R.layout.dialog_heading, null);
            TextView textView = (TextView) header.findViewById(R.id.text);
            ImageView icon = (ImageView) header.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.canceltrip);
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
                    if (Function.isOnline(TripAcceptActivity.this)) {
                        new TripRejectTask(trip.getId(), message[0]).execute();
                    } else {
                        //dialogManager.showAlertDialog(TripAcceptActivity.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
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
    View.OnClickListener arrived = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            floatingActionsMenu.collapseImmediately();
            floatingActionsMenu.setClickable(false);

            new TripArrivedTask().execute();
        }
    };
    View.OnClickListener call = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + trip.getNumber()));
            try {
                startActivity(in);
            } catch (android.content.ActivityNotFoundException ex) {
                Snackbar.make(findViewById(android.R.id.content), R.string.call_not_allow, Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
       // map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
       // map.moveCamera(CameraUpdateFactory.newLatLng(loc));
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

    private class SendMSGTask extends AsyncTask<Void, Void, Boolean> {
        String text;

        private SendMSGTask(String text) {
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(TripAcceptActivity.this, "");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            JSONObject object = new JSONObject();
            try {
                object.put("driverId", String.valueOf(AppPreferences.getDriverId(TripAcceptActivity.this)));
                object.put("tripId", trip.getId());
                object.put("message", text);
                String json = serviceHandler.makeServiceCall(AppConstants.SENDMESSAGE, ServiceHandler.POST, object);
                if (json != null) {
                    JSONObject jsonObject = new JSONObject(json);
                    if (!jsonObject.getString("status").equalsIgnoreCase("200")) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            if (status) {

                Snackbar.make(findViewById(android.R.id.content), R.string.error_not_send_message, Snackbar.LENGTH_LONG).show();

            }
            dialogManager.stopProcessDialog();
        }
    }



    private class TripArrivedTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(TripAcceptActivity.this, "");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("driverId", AppPreferences.getDriverId(TripAcceptActivity.this));
                jsonObject.put("tripId", trip.getId());
                jsonObject.put("latitude", gps.getLatitude());
                jsonObject.put("longitude", gps.getLongitude());
                jsonObject.put("dateTime", Function.getCurrentDateTime());
                String json = serviceHandler.makeServiceCall(AppConstants.ARRIVEDTRIP, ServiceHandler.POST, jsonObject);
                if(json != null){
                    JSONObject object = new JSONObject(json);
                    if(object.getString("status").equalsIgnoreCase("200")){
                        return  true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
                gps.getLocation();
                AppPreferences.setSourcelatitude(TripAcceptActivity.this, String.valueOf(gps.getLatitude()));
                AppPreferences.setSourcelongitude(TripAcceptActivity.this, String.valueOf(gps.getLongitude()));
                AppPreferences.setSourceaddress(TripAcceptActivity.this,
                        Function.getAddressFromLatlng(TripAcceptActivity.this, gps.getLatitude(), gps.getLongitude()));

                Log.d("Addreslatlng", AppPreferences.getSourceaddress(TripAcceptActivity.this) +
                        "\nlat:" + AppPreferences.getSourcelatitude(TripAcceptActivity.this) +
                        "\nlng:" + AppPreferences.getSourcelongitude(TripAcceptActivity.this));
                AppPreferences.setArrivedTime(TripAcceptActivity.this, Function.getCurrentDateTime());
                Intent intent = new Intent(TripAcceptActivity.this, TaxiWaitingActivity.class);
                intent.putExtra("tripDetails", trip);
                startActivity(intent);
                finish();
                dialogManager.stopProcessDialog();
            }else{
                floatingActionsMenu.setClickable(true);
                Snackbar.make(findViewById(android.R.id.content),getString(R.string.server_not_response),Snackbar.LENGTH_LONG).show();
                dialogManager.stopProcessDialog();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppPreferences.setActivityopen(instance, false);


    }

    @Override
    protected void onStart() {
        super.onStart();
        AppPreferences.setActivity(TripAcceptActivity.this, getClass().getName());

    }


    @Override
    protected void onResume() {
        super.onResume();
        AppPreferences.setActivityopen(instance, true);
        if(!AppPreferences.getActivityresumeopen(instance).equalsIgnoreCase("")) {
            Intent intent = new Intent(this, RecivedMessage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", AppPreferences.getActivityresumeopen(instance));
            startActivity(intent);
            AppPreferences.setActivityresumeopen(instance,"");
        }
    }
    @Override
    public void onBackPressed() {

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
            dialogManager.showProcessDialog(TripAcceptActivity.this,"");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            JSONObject object = new JSONObject();
            try {
                object.put("driverId", String.valueOf(AppPreferences.getDriverId(TripAcceptActivity.this)));
                object.put("tripId", tripId);
                object.put("message", text);
                object.put("latitude", gps.getLatitude());
                object.put("longitude", gps.getLongitude());
                object.put("dateTime", Function.getCurrentDateTime());
                String json = serviceHandler.makeServiceCall(AppConstants.TRIPREJECT, ServiceHandler.POST, object);
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
                AppPreferences.setTripId(TripAcceptActivity.this,"");
                Intent intent = new Intent(TripAcceptActivity.this, NavigationDrawer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(android.R.id.content), R.string.error_not_send_message, Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            try {
                dialogManager.stopProcessDialog();
            }catch (IllegalArgumentException e){}
        }
    }

    public class RoutesDownloadTask  extends AsyncTask<String, Void, String> {

        Context context;
        String distanceTime;

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
                PolylineOptions lineOptions = null;
                ArrayList<LatLng> points = null;
                String distance = "";
                String duration = "";

                if(result.size()<1){
                    //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                // Traversing through all the routes
                for(int i=0;i<result.size();i++){
                    lineOptions = new PolylineOptions();
                    points = new ArrayList<LatLng>();
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
                    // Fetching all the points in i-th route
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

                        points.add(position);
                        Log.d("distancess", distance);
                        String dis = distance.replace("km","").replace("m","").replace(" ","").replace(",","");
                        double dist = new Double(dis);

                        com.google.android.gms.maps.model.LatLng mapPoint =
                                new com.google.android.gms.maps.model.LatLng(lat, lng);
                        builder.include(mapPoint);
                        if(dist > 40){
                          //  map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 7f));
                        }else {
                           // map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 12f));
                        }
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLUE);
                }
                map.addPolyline(lineOptions);
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
            }
        }

        public String getDistanceTime() {
            return distanceTime;
        }

        public void setDistanceTime(String distanceTime) {
            this.distanceTime = distanceTime;
        }
    }


}
