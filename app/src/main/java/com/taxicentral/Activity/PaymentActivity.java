package com.taxicentral.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.taxicentral.Classes.GPSTracker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PaymentActivity extends AppCompatActivity {
    //aa
    Button btn_cancel,btn_payment_ok,btn_cash,btn_credit,btn_cash_ok,btn_paid,btn_unpaid;
    ProgressBar progressBar;
    ImageView confirm_icon;
    TextView show_payment,credit_amount_et,cash_amount_et, avl_credit,show_distance, show_fare_perkm;
    TextView waiting_payment_tv, name, number, address;
    Trip trip;
    DialogManager dialogManager;
    LinearLayout show_payment_layout,payment_page,cash_payment_layout,credit_payment_layout;
    String distance;
    float fare;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialogManager = new DialogManager();
        show_payment_layout = (LinearLayout) findViewById(R.id.show_payment_layout);
        payment_page = (LinearLayout) findViewById(R.id.payment_page);
        cash_payment_layout = (LinearLayout) findViewById(R.id.cash_payment_layout);
        credit_payment_layout = (LinearLayout) findViewById(R.id.credit_payment_layout);
        progressBar = (ProgressBar) findViewById(R.id.wait_progress);
        confirm_icon = (ImageView) findViewById(R.id.confirm_icon);
        btn_cash_ok  = (Button) findViewById(R.id.btn_cash_ok);
                btn_paid = (Button) findViewById(R.id.btn_paid);
        btn_unpaid = (Button) findViewById(R.id.btn_unpaid);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cash = (Button) findViewById(R.id.btn_cash);
        btn_credit = (Button) findViewById(R.id.btn_credit);
        btn_payment_ok = (Button) findViewById(R.id.btn_payment_ok);
        waiting_payment_tv = (TextView) findViewById(R.id.waiting_payment_tv);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);
        show_payment = (TextView) findViewById(R.id.show_payment);
        credit_amount_et = (TextView) findViewById(R.id.credit_amount_et);
        cash_amount_et = (TextView) findViewById(R.id.cash_amount_et);
        avl_credit = (TextView) findViewById(R.id.avl_credit);
        show_distance = (TextView) findViewById(R.id.show_distance);
        show_fare_perkm = (TextView) findViewById(R.id.show_fare_perkm);

        gps = new GPSTracker(PaymentActivity.this);

        trip = getIntent().getParcelableExtra("tripDetails");
        distance = getIntent().getStringExtra("distance");

        name.setText(trip.getCustomerName());
        number.setText(trip.getNumber());
        address.setText(trip.getSourceAddress());
        avl_credit.setText(getString(R.string.currency)+" "+String.valueOf(AppPreferences.getAvlCredit(PaymentActivity.this)));


        Log.d("distancess", distance);
        fare = Float.parseFloat(distance) * Float.parseFloat(trip.getFare());

        show_payment.setText(String.valueOf(fare));
        show_distance.setText(distance+" "+getString(R.string.km));
        show_fare_perkm.setText(trip.getFare()+ getString(R.string.per_km));

        cash_amount_et.setText(getString(R.string.currency)+" "+show_payment.getText().toString());
        credit_amount_et.setText(getString(R.string.currency)+" "+show_payment.getText().toString());


        btn_cancel.setOnClickListener(payment_cancel);
        btn_cash_ok.setOnClickListener(cashOk);
        btn_paid.setOnClickListener(paid);
        btn_unpaid.setOnClickListener(unpaid);

       /* trip = getIntent().getParcelableExtra("tripDetails");
        name.setText(trip.getCustomerName());
        number.setText(trip.getNumber());
        address.setText(trip.getSourceAddress());*/

      //  dialogManager.showProcessDialog(PaymentActivity.this, getString(R.string.wait_payment));

 Timer timer = new Timer();
            TimerTask task;
            timer = new Timer();

            timer.scheduleAtFixedRate(task = new TimerTask() {
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            waiting_payment_tv.setVisibility(View.GONE);
                            show_payment_layout.setVisibility(View.VISIBLE);
                        }
                    });

                }
            }, 5000, 5000);


        btn_payment_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_payment_ok.setVisibility(View.GONE);
                payment_page.setVisibility(View.VISIBLE);
            }
        });

        btn_cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cash.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btn_credit.setBackgroundColor(getResources().getColor(R.color.black_overlay));
                credit_payment_layout.setVisibility(View.GONE);
                cash_payment_layout.setVisibility(View.VISIBLE);

            }
        });
        btn_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_cash.setBackgroundColor(getResources().getColor(R.color.black_overlay));
                btn_credit.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                cash_payment_layout.setVisibility(View.GONE);
                credit_payment_layout.setVisibility(View.VISIBLE);

                float avlBlance = AppPreferences.getAvlCredit(PaymentActivity.this);
                float totalFare = fare;


            }
        });



    }

    View.OnClickListener payment_cancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_cancel.setClickable(false);
            Intent intent = new Intent(PaymentActivity.this, PaymentCancelledActivity.class);
            intent.putExtra("tripDetails", trip);
            startActivity(intent);
            finish();

        }
    };
    View.OnClickListener cashOk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_cancel.setClickable(false);
            btn_cash.setClickable(false);
            new PaymentTask("cash").execute();

        }
    };
    View.OnClickListener paid = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_cancel.setClickable(false);
            btn_paid.setClickable(false);

            float avlBlance = AppPreferences.getAvlCredit(PaymentActivity.this);
            float totalFare = fare;

            if(avlBlance >= totalFare){
                new PaymentTask("paid").execute();
            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                builder.setTitle(R.string.warning);
                builder.setMessage(R.string.credit_balance_low);
                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        btn_cancel.setClickable(true);
                        btn_paid.setClickable(true);
                    }
                });
                builder.show();
            }




        }
    };
    View.OnClickListener unpaid = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_cancel.setClickable(false);
            btn_unpaid.setClickable(false);
            float avlBlance = AppPreferences.getAvlCredit(PaymentActivity.this);
            float totalFare = fare;

            if(avlBlance < totalFare){
                new PaymentTask("unpaid").execute();
            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                builder.setTitle(R.string.warning);
                builder.setMessage(R.string.you_have_avl_balance);
                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        btn_cancel.setClickable(true);
                        btn_unpaid.setClickable(true);
                    }
                });
                builder.show();
            }

        }
    };

    @Override
    public void onBackPressed() {

    }



    private class PaymentTask extends AsyncTask<Void, Void, String> {
        String paymentMode, watingTime;
        String startTime = AppPreferences.getStartTime(PaymentActivity.this);
        String arrivedTime = AppPreferences.getArrivedTime(PaymentActivity.this);

        public PaymentTask(String paymentMode){
            this.paymentMode = paymentMode;

            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            try {
                Date date1 = simpleDateFormat.parse(startTime);
                Date date2 = simpleDateFormat.parse(arrivedTime);
                watingTime = printDifference(date1, date2);
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

                jsonObject.put("driverId", AppPreferences.getDriverId(PaymentActivity.this));
                jsonObject.put("tripId", trip.getId());
                jsonObject.put("payment_mode",paymentMode);
                jsonObject.put("start_time", startTime);
                jsonObject.put("waiting_time", watingTime);
                jsonObject.put("end_time", AppPreferences.getEndTime(PaymentActivity.this));
                jsonObject.put("distance", distance);
                jsonObject.put("amount", fare);
                jsonObject.put("avl_credit", AppPreferences.getAvlCredit(PaymentActivity.this));
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

                Intent intent = new Intent(PaymentActivity.this, RateExperienceActivity.class);
                intent.putExtra("tripDetails", trip);
                startActivity(intent);
                finish();
            }else{
                btn_paid.setClickable(true);
                btn_cash.setClickable(true);
                btn_unpaid.setClickable(true);
            }
        }
    }

    public String printDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays,
                elapsedHours, elapsedMinutes, elapsedSeconds);

        String difference = elapsedHours+" hours, "+ elapsedMinutes+" minutes, "+ elapsedSeconds+" seconds";



        return difference;

    }



}
