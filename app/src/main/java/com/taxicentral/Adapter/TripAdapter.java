package com.taxicentral.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.taxicentral.Activity.TripDetailsActivity;
import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MMFA-MANISH on 11/6/2015.
 */
public class TripAdapter extends BaseAdapter {

    ArrayList<Trip> tripList = new ArrayList<Trip>();
    Context context;
    TextView km_hours_tv;

    private LayoutInflater inflater;

    public TripAdapter(Context context, ArrayList<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    public void updateResult(ArrayList<Trip> tripList){
        this.tripList = tripList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return tripList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Trip trip = tripList.get(position);
        if (inflater == null)
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.trip_list, null);

        km_hours_tv = (TextView) convertView.findViewById(R.id.km_hours_tv);
        TextView fareText = (TextView) convertView.findViewById(R.id.fare);
        TextView trip_id = (TextView) convertView.findViewById(R.id.trip_id);
        TextView agreement_tv = (TextView) convertView.findViewById(R.id.agreement_tv);
        TextView agreement = (TextView) convertView.findViewById(R.id.agreement);
        TextView fare_tv = (TextView) convertView.findViewById(R.id.fare_tv);
        TextView destination = (TextView) convertView.findViewById(R.id.destination);
        Button btn_details = (Button) convertView.findViewById(R.id.btn_details);
        ImageView img_app = (ImageView) convertView.findViewById(R.id.img_app);
        ImageView img_corporate = (ImageView) convertView.findViewById(R.id.img_corporate);
        ImageView img_webportal = (ImageView) convertView.findViewById(R.id.img_webportal);
        ImageView img_taxicompany = (ImageView) convertView.findViewById(R.id.img_taxicompany);

        fareText.setText(context.getString(R.string.fare));

        trip_id.setText(context.getString(R.string.tripid)+" " + trip.getId());

        if (String.valueOf(trip.getId()).equalsIgnoreCase(AppPreferences.getTripId(context))) {
          btn_details.setText(R.string.on_trip);
        }else{
            btn_details.setText(R.string.details);
        }

        btn_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppPreferences.getTripId(context).equalsIgnoreCase("")) {
                    Intent intent = new Intent(context, TripDetailsActivity.class);
                    intent.putExtra("tripDetails", tripList.get(position));
                    context.startActivity(intent);
                } else if (String.valueOf(trip.getId()).equalsIgnoreCase(AppPreferences.getTripId(context))) {
                    Class<?> lastActivity;
                    try {
                        lastActivity = Class.forName(AppPreferences.getActivity(context));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        lastActivity = TripDetailsActivity.class;
                    }
                    Intent intent = new Intent(context, lastActivity);
                    intent.putExtra("tripDetails", tripList.get(position));
                    context.startActivity(intent);
                } else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    dialogBuilder.setTitle(context.getString(R.string.alert));
                    dialogBuilder.setMessage(context.getString(R.string.trip_already_assigned));
                    dialogBuilder.setCancelable(false);

                    dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
            }
        });
        km_hours_tv.setText(trip.getDistance() + "   " + trip.getTravelTime());

        agreement.setText(trip.getAgreement());
        destination.setText(trip.getDestinationAddress());
        fare_tv.setText(context.getString(R.string.currency) + trip.getFare() + context.getString(R.string.per_km));


        if (trip.getTripType().equalsIgnoreCase(AppConstants.APP)) {
            img_app.getDrawable().setColorFilter(0xddecc50f, PorterDuff.Mode.SRC_IN);
            img_app.invalidate();
        } else {
            img_app.getDrawable().clearColorFilter();
            img_app.invalidate();
        }
        if (trip.getTripType().equalsIgnoreCase(AppConstants.CORPORATE)) {
            img_corporate.getDrawable().setColorFilter(0xddecc50f, PorterDuff.Mode.SRC_IN);
            img_corporate.invalidate();
            agreement_tv.setVisibility(View.GONE);
            agreement.setVisibility(View.GONE);
        } else {
            img_corporate.getDrawable().clearColorFilter();
            img_corporate.invalidate();
            agreement_tv.setVisibility(View.VISIBLE);
            agreement.setVisibility(View.VISIBLE);
        }
        if (trip.getTripType().equalsIgnoreCase(AppConstants.TAXI_COMPANY)) {
            img_taxicompany.getDrawable().setColorFilter(0xddecc50f, PorterDuff.Mode.SRC_IN);
            img_taxicompany.invalidate();
        } else {
            img_taxicompany.getDrawable().clearColorFilter();
            img_taxicompany.invalidate();
        }
        if (trip.getTripType().equalsIgnoreCase(AppConstants.WEB_PORTAL)) {
            img_webportal.getDrawable().setColorFilter(0xddecc50f, PorterDuff.Mode.SRC_IN);
            img_webportal.invalidate();
        } else {
            img_webportal.getDrawable().clearColorFilter();
            img_webportal.invalidate();
        }


        return convertView;
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


                    }

                }
                Log.d("distance", distance + " - " + duration);
                //km_hours_tv.setText(distance + " - " + duration);
                setDistanceTime(distance + " - " + duration);
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
