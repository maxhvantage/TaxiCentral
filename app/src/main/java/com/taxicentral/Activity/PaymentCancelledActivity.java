package com.taxicentral.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class PaymentCancelledActivity extends AppCompatActivity {
    //aa
    Button btn_confirm;
    String[] message = {""};
    DialogManager dialogManager;
    Trip trip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_cancelled);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(confirm);

        dialogManager = new DialogManager();
        trip = getIntent().getParcelableExtra("tripDetails");

        final RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        message[0] = getString(R.string.i_am_busy_this_moment);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                message[0] = radioButton.getText().toString();
                Log.d("(RadioButton)", radioButton.getText().toString());
            }
        });

    }

    View.OnClickListener confirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Function.isOnline(PaymentCancelledActivity.this)) {
                btn_confirm.setClickable(false);
                new PaymentCancelTask(message[0]).execute();
            } else {
                dialogManager.showAlertDialog(PaymentCancelledActivity.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
            }

        }
    };

    private class PaymentCancelTask extends AsyncTask<Void, Void, Boolean> {
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
    }

    @Override
    public void onBackPressed() {

    }
}
