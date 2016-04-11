package com.taxicentral.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.taxicentral.Adapter.TripHistoryAdapter;
import com.taxicentral.Database.DBAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class TripHistoryActivity extends AppCompatActivity {
    //aa
    ExpandableListView trip_listview;
    TextView header_tv;
    Button btn_filter;
    HashMap<String, ArrayList<Trip>> tripHistoryList;
    TripHistoryAdapter tripHistoryAdapter;
    TripHistoryActivity instance = null;
    ArrayList<String> monthList;
    SimpleDateFormat dateFormatter_fromDate;
    DatePickerDialog DatePickerDialog_fromDate;
    SimpleDateFormat dateFormatter_toDate;
    DatePickerDialog DatePickerDialog_toDate;
    DialogManager dialogManager;
    DBAdapter db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        instance = this;
        dialogManager = new DialogManager();
        db = new DBAdapter(TripHistoryActivity.this);

        header_tv = (TextView) findViewById(R.id.header_tv);
        trip_listview = (ExpandableListView) findViewById(R.id.trip_lv);

        tripHistoryList = new HashMap<String, ArrayList<Trip>>();
        monthList = new ArrayList<String>();



        tripHistoryAdapter = new TripHistoryAdapter(TripHistoryActivity.this, monthList, tripHistoryList);
        trip_listview.setAdapter(tripHistoryAdapter);

        updateList("", "");

        if(Function.isOnline(TripHistoryActivity.this)){
            new TripHistoryTask().execute();
        }else{
            Snackbar.make(findViewById(android.R.id.content),getString(R.string.error_check_internet_connection), Snackbar.LENGTH_LONG).show();
        }

        trip_listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(TripHistoryActivity.this, TripHistoryDetailsActivity.class);
                intent.putExtra("tripHistory", (Trip)tripHistoryAdapter.getChild(groupPosition, childPosition));
                startActivity(intent);
                return false;
            }
        });



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        trip_listview.setIndicatorBounds(width - GetDipsFromPixel(55), width - GetDipsFromPixel(15));

    }

    public int GetDipsFromPixel(float pixels)
    {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void updateList(String fromDate,String toDate){
        monthList.clear();
        tripHistoryList.clear();

        if(!fromDate.equalsIgnoreCase("") && !toDate.equalsIgnoreCase("")){
            ArrayList<Trip> trips = db.getTripHistoryFilter(fromDate, toDate);
            for(int i=0; i<trips.size(); i++){
                if(!monthList.contains(trips.get(i).getMonth())){
                    monthList.add(trips.get(i).getMonth());
                    tripHistoryList.put(trips.get(i).getMonth(),  db.getTripHistoryByMonthWithDate(trips.get(i).getMonth(),fromDate, toDate));
                }
            }
        }else {
            ArrayList<Trip> trips = db.getTripHistory();
            for (int i = 0; i < trips.size(); i++) {
                if (!monthList.contains(trips.get(i).getMonth())) {
                    monthList.add(trips.get(i).getMonth());
                    tripHistoryList.put(trips.get(i).getMonth(), db.getTripHistoryByMonth(trips.get(i).getMonth()));
                }
            }
        }
        tripHistoryAdapter.updateResult(monthList, tripHistoryList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_filter:



                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TripHistoryActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.content_trip_filter, null);
                dialogBuilder.setView(dialogView);
                View header = inflater.inflate(R.layout.dialog_heading, null);
                TextView textView = (TextView) header.findViewById(R.id.text);
                ImageView icon = (ImageView) header.findViewById(R.id.icon);
                icon.setImageResource(R.drawable.ic_action_filter);
                textView.setText(R.string.action_filter);
                dialogBuilder.setCustomTitle(header);
                /*dialogBuilder.setTitle(R.string.action_filter);
                dialogBuilder.setIcon(R.drawable.ic_action_filter);*/
                dialogBuilder.setCancelable(true);

                final AlertDialog alertDialog = dialogBuilder.create();

                btn_filter = (Button) dialogView.findViewById(R.id.btn_filter);
               Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
                final EditText txtFromDate = (EditText) dialogView.findViewById(R.id.from_date);
                final EditText txtToDate = (EditText) dialogView.findViewById(R.id.to_date);

                btn_filter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_filter.setClickable(false);
                        String fromdate = txtFromDate.getText().toString().trim();
                        String todate = txtToDate.getText().toString().trim();
                        if(TextUtils.isEmpty(fromdate)){
                            txtFromDate.setError(getString(R.string.error_field_required));
                            btn_filter.setClickable(true);
                        }else if(TextUtils.isEmpty(todate)){
                            txtToDate.setError(getString(R.string.error_field_required));
                            btn_filter.setClickable(true);
                        }else if(Function.isOnline(TripHistoryActivity.this)){
                            updateList(fromdate,todate);
                           // new TripHistoryTask(fromdate, todate).execute();
                            alertDialog.dismiss();
                        }else{
                            btn_filter.setClickable(true);
                            Snackbar.make(findViewById(android.R.id.content),getString(R.string.error_check_internet_connection), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                txtFromDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        txtFromDate.setError(null);
                        DatePickerDialog_fromDate.show();
                    }
                });
                Calendar newCalendar = Calendar.getInstance();
                newCalendar.add(Calendar.MONTH,0);
                dateFormatter_fromDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                DatePickerDialog_fromDate = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                String fromDate = dateFormatter_fromDate.format(newDate
                                        .getTime());

                                txtFromDate.setText(fromDate);

                            }

                        }, newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));



                txtToDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        txtToDate.setError(null);
                        DatePickerDialog_toDate.show();
                    }
                });
                //Calendar newCalendar1 = Calendar.getInstance();
                newCalendar.add(Calendar.MONTH, 0);
                dateFormatter_toDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                DatePickerDialog_toDate = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                String toDate = dateFormatter_toDate.format(newDate
                                        .getTime());

                                txtToDate.setText(toDate);

                            }

                        }, newCalendar.get(Calendar.YEAR),
                        newCalendar.get(Calendar.MONTH),
                        newCalendar.get(Calendar.DAY_OF_MONTH));



                alertDialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class TripHistoryTask extends AsyncTask<Void, Void, ArrayList<Trip>> {

        ArrayList<Trip> tripList;
        ArrayList<String> monthArrayList;
        String fromDate, toDate;
        public TripHistoryTask(){
            monthArrayList = new ArrayList<String>();
            dialogManager.showProcessDialog(TripHistoryActivity.this, "updating...");
            db.deleteTripHistory();
        }
        public TripHistoryTask(String fromDate, String toDate){
            monthArrayList = new ArrayList<String>();
            dialogManager.showProcessDialog(TripHistoryActivity.this, "updating...");
            db.deleteTripHistory();
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        @Override
        protected ArrayList<Trip> doInBackground(Void... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject object = new JSONObject();
                object.put("driverId", AppPreferences.getDriverId(instance));
                object.put("fromdate", fromDate);
                object.put("todate", toDate);
                String json = serviceHandler.makeServiceCall(AppConstants.TRIPHISTORY, ServiceHandler.POST, object);
                if(json != null){
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getString("status").equalsIgnoreCase("200")){
                        JSONArray monthArray = jsonObject.getJSONArray("month");
                        for(int a=0; a<monthArray.length(); a++){
                            JSONObject monthObj = monthArray.getJSONObject(a);
                            monthArrayList.add(monthObj.getString("name"));
                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("result");

                        for(int i=0; i<jsonArray.length(); i++){

                            JSONObject jsonObj = jsonArray.getJSONObject(i);
                            for(int j=0; j<jsonObj.length(); j++){
                                JSONArray jsonArray2 = jsonObj.getJSONArray(monthArrayList.get(j));

                                tripList = new ArrayList<>();
                                for(int k=0; k<jsonArray2.length(); k++){
                                    JSONObject object1 = jsonArray2.getJSONObject(k);
                                    Trip trip = new Trip();
                                    trip.setId(Long.parseLong(object1.getString("id")));
                                    trip.setDate(object1.getString("tripdatetime"));
                                    trip.setTripType(object1.getString("trip_type"));
                                    trip.setCustomerName(object1.getString("userName"));
                                    trip.setCustomerImage(object1.getString("userImage"));
                                    if(object1.getString("customer_rating").equalsIgnoreCase("")){
                                        trip.setCustomerRating(1.0f);
                                    }else {
                                        trip.setCustomerRating(Float.parseFloat(object1.getString("customer_rating")));
                                    }
                                    Log.d("ratingvalue",object1.getString("customer_rating"));


                                    if(object1.getString("source_address").equalsIgnoreCase("")){
                                        trip.setSourceAddress(object1.getString("source_landmark"));
                                    }else{
                                        trip.setSourceAddress(object1.getString("source_address"));
                                    }

                                    if(object1.getString("destination_address").equalsIgnoreCase("")){
                                        trip.setDestinationAddress(object1.getString("destination_landmark"));
                                    }else{
                                        trip.setDestinationAddress(object1.getString("destination_address"));
                                    }

                                    if(object1.getString("source_latitude").equalsIgnoreCase("")){
                                        trip.setSourceLatitude(0.0);
                                    }else{
                                        trip.setSourceLatitude(new Double(object1.getString("source_latitude")));
                                    }
                                    if(object1.getString("source_longitude").equalsIgnoreCase("")){
                                        trip.setSourcelogitude(0.0);
                                    }else{
                                        trip.setSourcelogitude(new Double(object1.getString("source_longitude")));
                                    }
                                    if(object1.getString("destination_latitude").equalsIgnoreCase("")){
                                        trip.setDestinationLatitude(0.0);
                                    }else{
                                        trip.setDestinationLatitude(new Double(object1.getString("destination_latitude")));
                                    }
                                    if(object1.getString("destination_longitude").equalsIgnoreCase("")){
                                        trip.setDestinationLogitude(0.0);
                                    }else{
                                        trip.setDestinationLogitude(new Double(object1.getString("destination_longitude")));
                                    }




                                    //tripList.add(trip);
                                    trip.setMonth(monthArrayList.get(j));


                                        db.insertTripHistory(trip);
                                }
                                //monthList.add(monthArrayList.get(j));
                                //tripHistoryList.put(monthList.get(j), tripList);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       // tripHistoryAdapter.updateResult(monthList, tripHistoryList);
                                    }
                                });

                            }

                        }
                    }
                    if(jsonObject.getString("status").equalsIgnoreCase("100")){
                        final String message = jsonObject.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                header_tv.setVisibility(View.VISIBLE);
                                header_tv.setText(message);
                            }
                        });

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tripList;
        }

        @Override
        protected void onPostExecute(ArrayList<Trip> trips) {
            super.onPostExecute(trips);

            updateList("","");
            //tripHistoryAdapter.updateResult(monthList, tripHistoryList);
            if(btn_filter != null){
                btn_filter.setClickable(true);
            }
            dialogManager.stopProcessDialog();
        }
    }
}
