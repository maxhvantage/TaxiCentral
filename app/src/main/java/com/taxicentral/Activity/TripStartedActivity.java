package com.taxicentral.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Database.DBAdapter;
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

public class TripStartedActivity extends AppCompatActivity implements LocationListener {

    FloatingActionButton btn_trip_end;
    GoogleMap map;
    LocationManager locationManager;
    double lat, lon;
    Trip trip;
    String distances = "";
    TextView distanceTxt,already_paid_tv;
    CardView already_paid_text;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_started);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        distanceTxt = (TextView)findViewById(R.id.text);
        already_paid_tv = (TextView)findViewById(R.id.already_paid_tv);
        already_paid_text = (CardView)findViewById(R.id.cardView);

        btn_trip_end = (FloatingActionButton) findViewById(R.id.btn_trip_end);
        btn_trip_end.setOnClickListener(tripEnd);

        gps = new GPSTracker(this);

        trip = getIntent().getParcelableExtra("tripDetails");

        if(String.valueOf(trip.getDestinationLatitude()).equalsIgnoreCase("") || String.valueOf(trip.getDestinationLogitude()).equalsIgnoreCase("")){
            trip.setDestinationLatitude(gps.getLatitude());
            trip.setDestinationLogitude(gps.getLongitude());
        }

        distanceTxt.setVisibility(View.GONE);
        //distanceTxt.setText(trip.getDistance());


        if(AppConstants.CORPORATE.equalsIgnoreCase(trip.getTripType())){
            if(AppPreferences.getAvlCredit(TripStartedActivity.this)>Float.valueOf(trip.getFare())){
                already_paid_text.setVisibility(View.VISIBLE);
            }else{
                already_paid_text.setVisibility(View.GONE);
            }

        }else{
            already_paid_text.setVisibility(View.GONE);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                0, 1, this);

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        gps.getLocation();
        lat = gps.getLatitude();
        lon = gps.getLongitude();

        if(lat == 0.0 && lon == 0.0){
            showSettingsAlert();
        }
        map.setMyLocationEnabled(true);

        LatLng loc = new LatLng(lat, lon);
        LatLng sourceLocation = new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude());
        LatLng destinationLocation = new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude());

        map.moveCamera(CameraUpdateFactory.newLatLng(loc));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        map.addMarker(new MarkerOptions()
                .position(sourceLocation)
                .title("Source")
                .snippet(trip.getSourceAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_three)));
        map.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .title("Destination")
                .snippet(trip.getDestinationAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_four)));

       /* map.addPolyline((new PolylineOptions())
                .add(new LatLng(trip.getSourceLatitude(), trip.getSourcelogitude()), new LatLng(trip.getDestinationLatitude(), trip.getDestinationLogitude())
                ).width(5).color(Color.BLUE)
                .geodesic(true));*/
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 15));

        String url = Function.getDirectionsUrl(sourceLocation, destinationLocation);
        RoutesDownloadTask downloadTask = new RoutesDownloadTask(TripStartedActivity.this);
        downloadTask.execute(url);


    }




    View.OnClickListener tripEnd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_trip_end.setClickable(false);
            AppPreferences.setPaymentmode(TripStartedActivity.this, "");
            gps.getLocation();

            AppPreferences.setDestilatitude(TripStartedActivity.this, String.valueOf(gps.getLatitude()));
            AppPreferences.setDestilogitude(TripStartedActivity.this, String.valueOf(gps.getLongitude()));
            AppPreferences.setDestiaddress(TripStartedActivity.this,
                    Function.getAddressFromLatlng(TripStartedActivity.this, gps.getLatitude(), gps.getLongitude()));

            Log.d("AddresDlatlng", AppPreferences.getDestiaddress(TripStartedActivity.this) +
                    "\nlat:" + AppPreferences.getDestilatitude(TripStartedActivity.this) +
                    "\nlng:" + AppPreferences.getDestilogitude(TripStartedActivity.this));



            new TripFinishTask().execute();


        }
    };

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        lat = location.getLatitude();
        lon = location.getLongitude();
Log.d("change Location", lat +" : "+ lon);
        LatLng loc = new LatLng(lat, lon);
       // map.moveCamera(CameraUpdateFactory.newLatLng(loc));
       // map.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }
    public void showSettingsAlert() {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TripStartedActivity.this);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to enable it in settings?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.setActivity(TripStartedActivity.this, getClass().getName());
    }

    @Override
    public void onBackPressed() {

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
                Log.d("Background Task data ", url[0]);
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

                        com.google.android.gms.maps.model.LatLng mapPoint =
                                new com.google.android.gms.maps.model.LatLng(lat, lng);
                        builder.include(mapPoint);
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(4);
                    lineOptions.color(Color.BLUE);
                }
               // distanceTxt.setText(distance + " - " + duration);
                distances = distance.replace("km","").replace("m", "").replace(" ","").replace(",","");
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

    private class TripFinishTask extends AsyncTask<Void, Void, Boolean> {
      //  float fare;
        DialogManager dialogManager;
        public TripFinishTask(){
        //    fare = Float.parseFloat(distances) * Float.parseFloat(trip.getFare());
            dialogManager = new DialogManager();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(TripStartedActivity.this,"");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("driverId", AppPreferences.getDriverId(TripStartedActivity.this));
                jsonObject.put("tripId", trip.getId());
                jsonObject.put("latitude", gps.getLatitude());
                jsonObject.put("longitude", gps.getLongitude());
                jsonObject.put("sourcelatitude", AppPreferences.getSourcelatitude(TripStartedActivity.this));
                jsonObject.put("sourcelongitude", AppPreferences.getSourcelongitude(TripStartedActivity.this));
                jsonObject.put("sourceaddress", AppPreferences.getSourceaddress(TripStartedActivity.this));
                jsonObject.put("destinationlatitude", AppPreferences.getDestilatitude(TripStartedActivity.this));
                jsonObject.put("destinationlongitude", AppPreferences.getSourcelongitude(TripStartedActivity.this));
                jsonObject.put("destinationaddress", AppPreferences.getDestiaddress(TripStartedActivity.this));
              //  jsonObject.put("totalFare", fare);
                String json = serviceHandler.makeServiceCall(AppConstants.FINISHTRIP, ServiceHandler.POST, jsonObject);
                if(json != null){
                    JSONObject object = new JSONObject(json);
                    if(object.getString("status").equalsIgnoreCase("200")){
                        return  true;
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

            if(aBoolean) {

                AppPreferences.setTripId(TripStartedActivity.this, "");
                AppPreferences.setEndTime(TripStartedActivity.this, Function.getCurrentDateTime());
                DBAdapter db = new DBAdapter(TripStartedActivity.this);
                db.deleteTrip(trip.getId());
                if(trip.getTripType().equalsIgnoreCase(AppConstants.CORPORATE)){

                    //Intent intent = new Intent(TripStartedActivity.this, PaymentActivity.class);
                    Intent intent = new Intent(TripStartedActivity.this, PaymentRecivedActivity.class);
                    intent.putExtra("tripDetails", trip);
                    intent.putExtra("distance", distances);
                    startActivity(intent);
                    finish();


                }else{
                    //Intent intent = new Intent(TripStartedActivity.this, CashPaymentActivity.class);
                    Intent intent = new Intent(TripStartedActivity.this, RateExperienceActivity.class);
                    intent.putExtra("tripDetails", trip);
                    intent.putExtra("distance", distances);
                    startActivity(intent);
                    finish();
                }

            }else {
                btn_trip_end.setClickable(false);
            }
            dialogManager.stopProcessDialog();
        }
    }
}
