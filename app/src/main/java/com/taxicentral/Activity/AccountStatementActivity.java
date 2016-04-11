package com.taxicentral.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.taxicentral.Model.Trip;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Tabs.CorporateFragment;
import com.taxicentral.Tabs.MyAccountFragment;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AccountStatementActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    SimpleDateFormat dateFormatter_fromDate;
    DatePickerDialog DatePickerDialog_fromDate;
    SimpleDateFormat dateFormatter_toDate;
    DatePickerDialog DatePickerDialog_toDate;
    int pageNumber=0;
    List<String> itemList;
    ArrayList<String> monthList;
    HashMap<String, ArrayList<Trip>> tripHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_statement);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tripHistoryList = new HashMap<String, ArrayList<Trip>>();
        monthList = new ArrayList<String>();
        itemList = new ArrayList<String>();

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageNumber = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyAccountFragment(), "My Account");
        adapter.addFragment(new CorporateFragment(), "Corporate");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<Fragment>();
        private final List<String> mFragmentTitleList = new ArrayList<String>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
                onBackPressed();
                return true;
            case R.id.action_filter:
                //Intent intent = new Intent(AccountStatementActivity.this, TripFilterActivity.class);
               // startActivity(intent);

                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AccountStatementActivity.this);
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

                final Button btn_filter = (Button) dialogView.findViewById(R.id.btn_filter);
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
                        }else if(Function.isOnline(AccountStatementActivity.this)){
                           // updateList(fromdate,todate);
                            // new TripHistoryTask(fromdate, todate).execute();

                                if(pageNumber == 0){

                                   // new MyAccountFragment().task(AccountStatementActivity.this, fromdate, todate);

                                }else if(pageNumber == 1){
                                    new CorporateAccountTask(fromdate, todate).execute();
                                }


                            alertDialog.dismiss();
                        }else{
                            btn_filter.setClickable(true);
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_check_internet_connection), Snackbar.LENGTH_LONG).show();
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

    public class CorporateAccountTask extends AsyncTask<Void, Void, ArrayList<Trip>> {

        ArrayList<Trip> tripList;
        ArrayList<String> monthArrayList;
        String fromDate;
        String toDate;
        public CorporateAccountTask(String fromDate, String toDate){

            monthArrayList = new ArrayList<String>();
            this.fromDate = fromDate;
            this.toDate = toDate;
        }

        @Override
        protected ArrayList<Trip> doInBackground(Void... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject object = new JSONObject();
                object.put("driverId", AppPreferences.getDriverId(AccountStatementActivity.this));
                object.put("fromDate", fromDate);
                object.put("toDate", toDate);
                String json = serviceHandler.makeServiceCall(AppConstants.ACC_STATE_CORPORATE, ServiceHandler.POST, object);
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
                                JSONObject jsonObjMonthList = jsonObj.getJSONObject(monthArrayList.get(j));
                                JSONArray jsonArrayinside = jsonObjMonthList.getJSONArray("result");
                                tripList = new ArrayList<>();
                                for(int k=0; k<jsonArrayinside.length(); k++){
                                    JSONObject object1 = jsonArrayinside.getJSONObject(k);
                                    Trip trip = new Trip();
                                    trip.setFare("Trip "+(k+1)+" - "+getString(R.string.currency)+object1.getString("trip"));
                                    tripList.add(trip);
                                }
                                Trip trip = new Trip();
                                trip.setFare("Total " + monthArrayList.get(j) + " - " + getString(R.string.currency) + jsonObjMonthList.getInt("amount"));
                                tripList.add(trip);
                                Trip trip1 = new Trip();
                                trip1.setFare("");
                                tripList.add(trip1);
                                //   String totalAmount = jsonObjMonthList.getString("amount");

                                Log.d("test", "Total " + monthArrayList.get(j) + " " + getString(R.string.currency) + jsonObjMonthList.getInt("amount"));

                                monthList.add(monthArrayList.get(j));
                                tripHistoryList.put(monthList.get(j),tripList);


                            }

                        }
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
            CorporateFragment.corporateAccountAdapter.updateResult(monthList, tripHistoryList);
            /*if(btn_filter != null){
                btn_filter.setClickable(true);
            }*/
        }
    }

    private class getMyAccountTask extends AsyncTask<Void,Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("driverId", AppPreferences.getDriverId(AccountStatementActivity.this));
                String json = serviceHandler.makeServiceCall(AppConstants.ACC_STATE_MYACCOUNT, ServiceHandler.POST,jsonObject);
                if(json != null){
                    JSONObject jsonObj = new JSONObject(json);
                    if(jsonObj.getString("status").equalsIgnoreCase("200")){
                        List<String> monthList = new ArrayList<String>();
                        JSONArray jsonArray = jsonObj.getJSONArray("month");
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            monthList.add(jsonObject1.getString("name"));
                        }
                        JSONArray jsonArray1 = jsonObj.getJSONArray("result");
                        for(int j=0; j<jsonArray1.length(); j++){
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);

                            int amount = 200;

                            //     month = jsonObject1.getInt(monthList.get(j));


                            String type = jsonObject1.getString("type");
                            itemList.add(monthList.get(j)+" - $"+amount+" - "+ type);

                        }

                        return "200";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MyAccountFragment.myPaymentAdapter.notifyDataSetChanged();
        }
    }

}