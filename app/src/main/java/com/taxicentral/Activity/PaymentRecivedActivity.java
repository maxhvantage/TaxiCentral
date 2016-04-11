package com.taxicentral.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.Function;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class PaymentRecivedActivity extends AppCompatActivity {
    //aa
    Button  btn_continue, btn_cancel;
    ImageView confirm_icon;
    TextView waiting_payment_tv, name, number, address, show_distance, show_fare_perkm,show_payment;
    Trip trip;
    //String distance;
    float fare;
    ProgressBar progressBar;
    LinearLayout show_payment_layout;
    android.os.Handler mHandler = new android.os.Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_recived);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        show_payment_layout = (LinearLayout) findViewById(R.id.show_payment_layout);
        confirm_icon = (ImageView) findViewById(R.id.confirm_icon);
        btn_continue = (Button) findViewById(R.id.btn_continue);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        waiting_payment_tv = (TextView) findViewById(R.id.waiting_payment_tv);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);
        progressBar = (ProgressBar) findViewById(R.id.wait_progress);
        show_distance = (TextView) findViewById(R.id.show_distance);
        show_fare_perkm = (TextView) findViewById(R.id.show_fare_perkm);
        show_payment = (TextView) findViewById(R.id.show_payment);

        btn_continue.setOnClickListener(continues);

        trip = getIntent().getParcelableExtra("tripDetails");
       // distance = getIntent().getStringExtra("distance");
        name.setText(trip.getCustomerName());
        number.setText(trip.getNumber());
        address.setText(trip.getSourceAddress());

      /*  distance = getIntent().getStringExtra("distance");
        Log.d("distancess", distance);
        if(Float.parseFloat(distance) > 1.0){
            fare = Float.parseFloat(distance) * Float.parseFloat(trip.getFare());
        }else{
            fare = Float.parseFloat("1.0") * Float.parseFloat(trip.getFare());
        }*/



        show_payment.setText(trip.getFare());
        //show_distance.setText(distance + " " + getString(R.string.km));
        //show_fare_perkm.setText(trip.getFare() + getString(R.string.per_km));

        btn_continue.setVisibility(View.VISIBLE);


        mHandler.postDelayed(mRunnable,3000);


       /* Timer timer = new Timer();
        TimerTask task;
        timer = new Timer();

        timer.scheduleAtFixedRate(task = new TimerTask() {
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        confirm_icon.setVisibility(View.VISIBLE);
                        waiting_payment_tv.setVisibility(View.VISIBLE);

                        float avl_bal = AppPreferences.getAvlCredit(PaymentRecivedActivity.this);
                        fare = Float.valueOf(trip.getFare());
                        if (avl_bal > fare) {

                        } else {
                            btn_cancel.setVisibility(View.VISIBLE);
                            btn_continue.setVisibility(View.GONE);
                           // show_payment_layout.setVisibility(View.VISIBLE);
                            confirm_icon.setVisibility(View.GONE);
                            waiting_payment_tv.setVisibility(View.GONE);
                        }


                    }
                });

            }
        }, 3000, 3000);*/


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentRecivedActivity.this, PaymentCancelledActivity.class);
                intent.putExtra("tripDetails", trip);
                //intent.putExtra("distance", distance);
                intent.putExtra("fare", fare);
                startActivity(intent);
                finish();
            }
        });


    }

    View.OnClickListener continues = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_continue.setClickable(false);

            new PaymentTask("paid").execute();



        }
    };

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if(AppPreferences.getPaymentmode(PaymentRecivedActivity.this).equalsIgnoreCase(AppConstants.CREDIT) ||
                AppPreferences.getPaymentmode(PaymentRecivedActivity.this).equalsIgnoreCase(AppConstants.CASH)){
                AppPreferences.setPaymentmode(PaymentRecivedActivity.this,"");
                mHandler.removeCallbacks(mRunnable);
                Log.d("setPaymentmode","inside");
                progressBar.setVisibility(View.GONE);
                confirm_icon.setVisibility(View.VISIBLE);
                waiting_payment_tv.setVisibility(View.VISIBLE);

                float avl_bal = AppPreferences.getAvlCredit(PaymentRecivedActivity.this);
                fare = Float.valueOf(trip.getFare());
                if (avl_bal > fare) {
                    Log.d("setPaymentmode","ifff");
                } else {
                    btn_cancel.setVisibility(View.VISIBLE);
                    btn_continue.setVisibility(View.GONE);
                    // show_payment_layout.setVisibility(View.VISIBLE);
                    confirm_icon.setVisibility(View.GONE);
                    waiting_payment_tv.setVisibility(View.GONE);
                    Log.d("setPaymentmode", "elseee");
                }
            }else {
                Log.d("setPaymentmode","outside tryagain");
                mHandler.postDelayed(mRunnable, 3000);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.setActivity(PaymentRecivedActivity.this, getClass().getName());
    }

    @Override
    public void onBackPressed() {

    }

    private class PaymentTask extends AsyncTask<Void, Void, String> {
        String paymentMode, watingTime;
        String startTime = AppPreferences.getStartTime(PaymentRecivedActivity.this);
        String arrivedTime = AppPreferences.getArrivedTime(PaymentRecivedActivity.this);

        public PaymentTask(String paymentMode){
            this.paymentMode = paymentMode;

            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            try {
                Date date1 = simpleDateFormat.parse(startTime);
                Date date2 = simpleDateFormat.parse(arrivedTime);
                watingTime = Function.printDifference(date1, date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String status = "";
            ServiceHandler serviceHandler = new ServiceHandler();
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("driverId", AppPreferences.getDriverId(PaymentRecivedActivity.this));
                jsonObject.put("tripId", trip.getId());
                jsonObject.put("payment_mode",paymentMode);
                jsonObject.put("start_time", startTime);
                jsonObject.put("waiting_time", watingTime);
                jsonObject.put("end_time", AppPreferences.getEndTime(PaymentRecivedActivity.this));
                //jsonObject.put("distance", distance);
                //jsonObject.put("amount", fare);
                jsonObject.put("avl_credit", AppPreferences.getAvlCredit(PaymentRecivedActivity.this));
                Log.d("finalData", jsonObject.toString());
                String json = serviceHandler.makeServiceCall(AppConstants.PAYPAYMENT,ServiceHandler.POST, jsonObject);
                if(json!=null){
                    JSONObject object = new JSONObject(json);
                    status = object.getString("status");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return status;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("200")){

                Intent intent = new Intent(PaymentRecivedActivity.this, RateExperienceActivity.class);
                intent.putExtra("tripDetails", trip);
                startActivity(intent);
                finish();
            }else if(s.equalsIgnoreCase("400")){
                finish();
            }else{
                btn_continue.setClickable(true);
            }
        }
    }
}
