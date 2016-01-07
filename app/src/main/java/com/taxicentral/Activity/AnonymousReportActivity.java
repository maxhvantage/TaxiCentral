package com.taxicentral.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DialogManager;
import com.taxicentral.Utils.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnonymousReportActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView cameraImage;
    Button btn_send;
    String imageString;
    EditText report_description;
    DialogManager dialogManager;
    //aa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialogManager = new DialogManager();

        cameraImage = (ImageView) findViewById(R.id.cam_image);
        report_description = (EditText) findViewById(R.id.report_description);
        btn_send = (Button) findViewById(R.id.btn_send);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

        btn_send.setOnClickListener(send);


    }

    View.OnClickListener send = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btn_send.setClickable(false);
            if(TextUtils.isEmpty(imageString)){

            }else if(TextUtils.isEmpty(report_description.getText().toString())){
                report_description.setError(getString(R.string.error_field_required));
                btn_send.setClickable(true);
            }else {
                if(Function.isOnline(AnonymousReportActivity.this)) {
                    new AnonymousReportTask(imageString, report_description.getText().toString()).execute();
                }else{
                    btn_send.setClickable(true);
                    dialogManager.showAlertDialog(AnonymousReportActivity.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
                }
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            cameraImage.setImageBitmap(imageBitmap);
            imageString = encodeTobase64(imageBitmap);
        }
        if(resultCode == RESULT_CANCELED){
           finish();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    private class AnonymousReportTask extends AsyncTask<Void, Void, String> {

        String image, report, id;
        GPSTracker gps;

        public AnonymousReportTask(String image, String report){
            this.image = image;
            this.report = report;
            gps = new GPSTracker(AnonymousReportActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialogManager.showProcessDialog(AnonymousReportActivity.this,"");
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                gps.getLocation();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String date = df.format(calendar.getTime());

                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("driverId", String.valueOf(AppPreferences.getDriverId(AnonymousReportActivity.this)));
                jsonObject.put("image", image);
                jsonObject.put("reportDiscription", report);
                jsonObject.put("latitude", gps.getLatitude());
                jsonObject.put("longitude", gps.getLongitude());
                jsonObject.put("dateTime", date);

                String json = serviceHandler.makeServiceCall(AppConstants.ANONYMOUSREPORT, ServiceHandler.POST, jsonObject);
                if(json != null){
                    JSONObject object = new JSONObject(json);
                    if(object.getString("status").equalsIgnoreCase("200")){
                            JSONObject jsonObj = object.getJSONObject("result");
                            id = jsonObj.getString("id");

                        return "200";
                    }else if(object.getString("status").equalsIgnoreCase("400")){
                        return "400";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
            if(status.equalsIgnoreCase("200")){
                Intent intent = new Intent(AnonymousReportActivity.this, AnonymousReportSentActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }else{
                btn_send.setClickable(true);
                Snackbar.make(findViewById(android.R.id.content),getString(R.string.server_not_response),Snackbar.LENGTH_LONG).show();
            }

            dialogManager.stopProcessDialog();
        }
    }

}
