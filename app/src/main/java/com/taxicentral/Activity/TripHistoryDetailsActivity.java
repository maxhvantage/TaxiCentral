package com.taxicentral.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Model.Trip;
import com.taxicentral.Model.TripHistoryModel;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DirectionsJSONParser;
import com.taxicentral.Utils.Function;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TripHistoryDetailsActivity extends AppCompatActivity {
    TextView trip_id, trip_date, trip_time, trip_type;
    private GoogleMap map;
    RatingBar rate;
    Trip tripHistory;
    GPSTracker gps;
    double lat, lon;
    //aa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trip_id = (TextView) findViewById(R.id.trip_id);
        trip_date = (TextView) findViewById(R.id.trip_date);
        trip_time = (TextView) findViewById(R.id.trip_time);
        trip_type = (TextView) findViewById(R.id.trip_type);
        rate = (RatingBar) findViewById(R.id.rate);

        gps = new GPSTracker(this);

         tripHistory = getIntent().getParcelableExtra("tripHistory");

        getSupportActionBar().setTitle(" Details");

        trip_id.setText(tripHistory.getId() + "");
      //  trip_date.setText("" + tripHistory.getDate().substring(0, 11));
        trip_type.setText(tripHistory.getTripType());
      //  trip_time.setText("" + tripHistory.getDate().substring(11,16));



        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null ;
        try {
            date = formatter.parse(tripHistory.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf=new SimpleDateFormat("dd MMM yyyy");
        String setDate =sdf.format(date.getTime());
        SimpleDateFormat sdf1=new SimpleDateFormat("hh:mm aa");
        String setTime =sdf1.format(date.getTime());

        trip_date.setText(setDate);
        trip_time.setText(setTime);
        Log.d("ratingvalue",tripHistory.getCustomerRating()+"");
        rate.setRating(tripHistory.getCustomerRating());




        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        gps.getLocation();
        lat = gps.getLatitude();
        lon = gps.getLongitude();

        if(lat == 0.0 && lon == 0.0){
            // showSettingsAlert();
        }


        LatLng sourceLocation = new LatLng(tripHistory.getSourceLatitude(), tripHistory.getSourcelogitude());
        LatLng destinationLocation = new LatLng(tripHistory.getDestinationLatitude(), tripHistory.getDestinationLogitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(sourceLocation));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        map.addMarker(new MarkerOptions()
                .position(sourceLocation)
                .title(tripHistory.getCustomerName())
                .snippet(tripHistory.getSourceAddress()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        map.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .title(tripHistory.getCustomerName())
                .snippet(tripHistory.getDestinationAddress()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        String url = Function.getDirectionsUrl(sourceLocation, destinationLocation);
        RoutesDownloadTask downloadTask = new RoutesDownloadTask(TripHistoryDetailsActivity.this);
        downloadTask.execute(url);


    }

    public class RoutesDownloadTask  extends AsyncTask<String, Void, String> {

        Context context;
        String distanceTime;

        public RoutesDownloadTask(Context context) {
            this.context = context;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
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
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                //Log.d("Exception while downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;


                try {

                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
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

                if (result.size() < 1) {
                    //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                    return;
                }
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    lineOptions = new PolylineOptions();
                    points = new ArrayList<LatLng>();
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
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
                map.addPolyline(lineOptions);
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
            }
        }


    }
        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }


}
