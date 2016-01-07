package com.taxicentral.Activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.taxicentral.Model.Trip;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DialogManager;
import com.taxicentral.Utils.Function;

import org.json.JSONException;
import org.json.JSONObject;

public class RateExperienceActivity extends AppCompatActivity {
    //aa
    Button btn_skip, btn_rate;
    RatingBar ratingBar;
    DialogManager dialogManager;
    Trip trip;
    TextView name, number, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_experience);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialogManager = new DialogManager();

        trip = getIntent().getParcelableExtra("tripDetails");
        btn_skip = (Button) findViewById(R.id.btn_skip);
        btn_rate = (Button) findViewById(R.id.btn_rate);
        ratingBar = (RatingBar) findViewById(R.id.rate);
        name = (TextView) findViewById(R.id.name);
        number = (TextView) findViewById(R.id.number);
        address = (TextView) findViewById(R.id.address);

        name.setText(trip.getCustomerName());
        number.setText(trip.getNumber());
        address.setText(trip.getSourceAddress());

        btn_rate.setOnClickListener(rate);
        btn_skip.setOnClickListener(skip);

        Drawable stars =  ratingBar.getProgressDrawable();
        DrawableCompat.setTint(stars, getResources().getColor(R.color.colorPrimary));

    }

    View.OnClickListener skip = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener rate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(String.valueOf(ratingBar.getRating()).equalsIgnoreCase("0.0")) {
                dialogManager.showAlertDialog(RateExperienceActivity.this,"Rate","Please select at least one rate",null);
            }else{
                if(Function.isOnline(RateExperienceActivity.this)) {
                    new RateingTask(String.valueOf(ratingBar.getRating())).execute();
                }else{
                    dialogManager.showAlertDialog(RateExperienceActivity.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
                }
            }
        }
    };

    private class RateingTask  extends AsyncTask<Void, Void, Boolean> {
        String text;
        private RateingTask(String text){
            this.text = text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(RateExperienceActivity.this,"");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ServiceHandler serviceHandler = new ServiceHandler();
            JSONObject object = new JSONObject();
            try {
                object.put("driverId", String.valueOf(AppPreferences.getDriverId(RateExperienceActivity.this)));
                object.put("rateing", text);
                object.put("tripId", trip.getId());
                String json = serviceHandler.makeServiceCall(AppConstants.RATING, ServiceHandler.POST, object);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(android.R.id.content), R.string.erro_rating_not_send,Snackbar.LENGTH_LONG).show();
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
