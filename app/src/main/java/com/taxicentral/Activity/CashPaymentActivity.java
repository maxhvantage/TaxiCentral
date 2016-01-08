package com.taxicentral.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DialogManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CashPaymentActivity extends AppCompatActivity {

    Trip trip;
    TextView name, number, source_address_tv, destination_address_tv, show_distance_tv, fare_perkm_tv,total_amount_tv;
    EditText cash_amount_et;
    Button btn_pay;
    String distance;
    float fare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        trip = getIntent().getParcelableExtra("tripDetails");
        distance = getIntent().getStringExtra("distance");

        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        source_address_tv = (TextView) findViewById(R.id.source_address_tv);
        destination_address_tv = (TextView) findViewById(R.id.destination_address_tv);
        show_distance_tv = (TextView) findViewById(R.id.show_distance_tv);
        fare_perkm_tv = (TextView) findViewById(R.id.fare_perkm_tv);
        total_amount_tv = (TextView) findViewById(R.id.total_amount_tv);

        cash_amount_et = (EditText) findViewById(R.id.cash_amount_et);
        btn_pay = (Button) findViewById(R.id.btn_pay);

//aa
        fare = Float.parseFloat(distance) * Float.parseFloat(trip.getFare());

        Log.d("totalFare", fare+" : "+Float.parseFloat(distance)+" : "+ Float.parseFloat(trip.getFare()));

        show_distance_tv.setText(distance + " " + getString(R.string.km));
        fare_perkm_tv.setText(trip.getFare()+ getString(R.string.per_km));
        total_amount_tv.setText(String.valueOf(fare));

        name.setText(trip.getCustomerName());
        number.setText(trip.getNumber());
        source_address_tv.setText(trip.getSourceAddress());
        destination_address_tv.setText(trip.getDestinationAddress());

        cash_amount_et.setText(getString(R.string.currency)+fare);



        btn_pay.setOnClickListener(pay);


    }

    View.OnClickListener pay = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String amount = cash_amount_et.getText().toString();
            if(TextUtils.isEmpty(amount)){
                cash_amount_et.setError("Enter Amount");
            }else {
                btn_pay.setClickable(false);
                new UserCashPaymentTask(amount).execute();
            }
        }
    };

    private class UserCashPaymentTask extends AsyncTask<Void, Void, String> {
        String amount;
        DialogManager dialogManager;
        public UserCashPaymentTask(String amount){
            this.amount = amount;
            dialogManager = new DialogManager();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(CashPaymentActivity.this, "");
        }

        @Override
        protected String doInBackground(Void... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("driverId", AppPreferences.getDriverId(CashPaymentActivity.this));
                jsonObject.put("tripId",trip.getId());
                jsonObject.put("amount",amount);
                String json = serviceHandler.makeServiceCall(AppConstants.PAYPAYMENT,ServiceHandler.POST, jsonObject);
                if(json!=null){
                    JSONObject object = new JSONObject(json);

                    return object.getString("status");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("200")){
                Intent intent = new Intent(CashPaymentActivity.this, RateExperienceActivity.class);
                intent.putExtra("tripDetails", trip);
                startActivity(intent);
                finish();
            }else{
                btn_pay.setClickable(true);
            }
            dialogManager.stopProcessDialog();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
