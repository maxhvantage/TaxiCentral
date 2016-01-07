package com.taxicentral.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentRecivedActivity extends AppCompatActivity {
    //aa
    Button  btn_continue;
    ImageView confirm_icon;
    TextView waiting_payment_tv, name, number, address;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_recived);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        confirm_icon = (ImageView) findViewById(R.id.confirm_icon);
        btn_continue = (Button) findViewById(R.id.btn_continue);
        waiting_payment_tv = (TextView) findViewById(R.id.waiting_payment_tv);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);

        btn_continue.setOnClickListener(continues);

        trip = getIntent().getParcelableExtra("tripDetails");
        name.setText(trip.getCustomerName());
        number.setText(trip.getNumber());
        address.setText(trip.getSourceAddress());


        confirm_icon.setVisibility(View.VISIBLE);
        btn_continue.setVisibility(View.VISIBLE);
    }

    View.OnClickListener continues = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_continue.setClickable(false);
            Intent intent = new Intent(PaymentRecivedActivity.this, RateExperienceActivity.class);
            intent.putExtra("tripDetails", trip);
            startActivity(intent);
            finish();

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
}
