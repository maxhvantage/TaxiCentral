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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DialogManager;
import com.taxicentral.Utils.Function;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentCancelledActivity extends AppCompatActivity {
    //aa
    Button btn_confirm;
    String[] message = {""};
    DialogManager dialogManager;
    Trip trip;
    String distance,descriptionTxt;
    float fare;
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_cancelled);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        description = (EditText) findViewById(R.id.description_et);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(confirm);

        dialogManager = new DialogManager();
        trip = getIntent().getParcelableExtra("tripDetails");
        trip = getIntent().getParcelableExtra("tripDetails");
        distance = getIntent().getStringExtra("distance");
        fare = getIntent().getFloatExtra("fare", 0);

        final RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        message[0] = "";
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                message[0] = radioButton.getText().toString();
                if(radioButton.getText().toString().equalsIgnoreCase(getString(R.string.client_paid_cash))){
                    message[0] = "cash";
                    description.setVisibility(View.GONE);
                    description.setText("");
                }else if(radioButton.getText().toString().equalsIgnoreCase(getString(R.string.client_not_paid))){
                    message[0] = "unpaid";
                    description.setVisibility(View.GONE);
                    description.setText("");
                }else if(radioButton.getText().toString().equalsIgnoreCase(getString(R.string.destination_wrong))){
                    message[0] = "Destination Address Wrong";
                    description.setVisibility(View.GONE);
                    description.setText("");
                }else if(radioButton.getText().toString().equalsIgnoreCase(getString(R.string.client_never_boarded))){
                    message[0] = "Client never boarded";
                    description.setVisibility(View.GONE);
                    description.setText("");
                }else if(radioButton.getText().toString().equalsIgnoreCase(getString(R.string.other))){
                    message[0] = "Other";
                    description.setVisibility(View.VISIBLE);
                }
                Log.d("(RadioButton)", radioButton.getText().toString());
            }
        });

    }

    View.OnClickListener confirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Function.isOnline(PaymentCancelledActivity.this)) {
                if(message[0].equalsIgnoreCase("")){
                    Snackbar.make(findViewById(android.R.id.content),"Please select atleast one option", Snackbar.LENGTH_LONG).show();
                }else if(message[0].equalsIgnoreCase("Other")){
                    descriptionTxt =  description.getText().toString();
                    if(TextUtils.isEmpty(descriptionTxt)){
                        description.setError("Ender description");
                    }else{
                        btn_confirm.setClickable(false);
                        new PaymentTask(message[0]).execute();
                    }
                }else{
                    btn_confirm.setClickable(false);
                    new PaymentTask(message[0]).execute();
                }

                //new PaymentCancelTask(message[0]).execute();

            } else {
                dialogManager.showAlertDialog(PaymentCancelledActivity.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
            }

        }
    };


    private class PaymentTask extends AsyncTask<Void, Void, String> {
        String paymentMode, watingTime;
        String startTime = AppPreferences.getStartTime(PaymentCancelledActivity.this);
        String arrivedTime = AppPreferences.getArrivedTime(PaymentCancelledActivity.this);

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

                jsonObject.put("driverId", AppPreferences.getDriverId(PaymentCancelledActivity.this));
                jsonObject.put("tripId", trip.getId());
                jsonObject.put("payment_mode",paymentMode);
                jsonObject.put("start_time", startTime);
                jsonObject.put("waiting_time", watingTime);
                jsonObject.put("end_time", AppPreferences.getEndTime(PaymentCancelledActivity.this));
                jsonObject.put("distance", distance);
                jsonObject.put("amount", fare);
                jsonObject.put("description", descriptionTxt);
                jsonObject.put("avl_credit", AppPreferences.getAvlCredit(PaymentCancelledActivity.this));
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

                Intent intent = new Intent(PaymentCancelledActivity.this, RateExperienceActivity.class);
                intent.putExtra("tripDetails", trip);
                startActivity(intent);
                finish();
            }else{
                btn_confirm.setClickable(true);
            }
        }
    }

    /*private class PaymentCancelTask extends AsyncTask<Void, Void, Boolean> {
        String text;
        private PaymentCancelTask(String text){
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(PaymentCancelledActivity.this,"");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            JSONObject object = new JSONObject();
            try {
                object.put("driverId", String.valueOf(AppPreferences.getDriverId(PaymentCancelledActivity.this)));
                object.put("message", text);
                object.put("tripId", trip.getId());
                String json = serviceHandler.makeServiceCall(AppConstants.PAYMENTCANCELED, ServiceHandler.POST, object);
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
                finish();
            }else{
                btn_confirm.setClickable(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(android.R.id.content), R.string.server_not_response,Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            dialogManager.stopProcessDialog();
        }
    }*/

    @Override
    public void onBackPressed() {

    }
}
