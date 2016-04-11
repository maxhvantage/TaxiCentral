package com.taxicentral.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.maps.model.LatLng;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Gcm.Config;
import com.taxicentral.Model.User;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Services.UpdateLocationToServer;
import com.taxicentral.Services.ZoneControlServices;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.DialogManager;
import com.taxicentral.Utils.Function;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    //aa
    DBAdapter db;
    GPSTracker gps;
    DialogManager dialogManager;
    private UserLoginTask mAuthTask = null;
    // UI references.
    private EditText mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    String regId;
    GoogleCloudMessaging gcm;
LoginActivity instance = null;
    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialogManager = new DialogManager();
        db = new DBAdapter(LoginActivity.this);
        gps = new GPSTracker(LoginActivity.this);
instance = this;
        /* GCM */
        if (TextUtils.isEmpty(regId)) {
            regId = registerGCM();
            Log.d("GcmRegisterActivity", "GCM RegId: " + regId);
        }
        /* GCM */

        // Set up the login form.
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mUserNameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.sign_in_button || id == EditorInfo.IME_NULL) {
                    if(TextUtils.isEmpty(regId)) {
                       regId = registerGCM();
                    }else{
                        attemptLogin();
                    }
                    return true;
                }
                return false;
            }
        });

        if(Function.isServiceRunning(LoginActivity.this, "com.taxicentral.Services.ZoneControlServices")){
            stopService(new Intent(LoginActivity.this, ZoneControlServices.class));

        }

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Function.hideSoftKeyboard(LoginActivity.this);

                if(TextUtils.isEmpty(regId)) {
                    regId = registerGCM();
                }else{
                    attemptLogin();
                }
            }
        });
        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, Register.class);
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        String action = getIntent().getAction();
        if(action.equalsIgnoreCase("")){

        }else if(action.equalsIgnoreCase("autoLogout")){
            dialogManager.showAlertDialog(LoginActivity.this, "Log out", getString(R.string.already_login),null);
        }

    }


    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(LoginActivity.this);

        if (TextUtils.isEmpty(regId)) {

            registerInBackground();

        } else {
           /* Toast.makeText(getApplicationContext(),
                    "RegId already available. RegId: " + regId,
                    Toast.LENGTH_LONG).show();*/
        }
        return regId;
    }
    private String getRegistrationId(Context context) {

        String registrationId = AppPreferences.getDeviceid(context);
        if (registrationId.isEmpty()) {
            return "";
        }

        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LoginActivity.this);
                    }
                    Log.d("GcmRegisterActivity", "instanceID - regId:1 "
                            );

                    /*InstanceID instanceID = InstanceID.getInstance(instance);
                    Log.d("GcmRegisterActivity", "instanceID - regId:2 "
                            +instanceID.getId());
                    String token = instanceID.getToken(Config.GOOGLE_PROJECT_ID,
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.d("GcmRegisterActivity", "instanceID - regId: 3"
                            + token);*/
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);

                    // the action is used to distinguish

                    Log.d("GcmRegisterActivity", "registerInBackground - regId: "
                            + regId);

                    msg = "Device registered, registration ID=" + regId;

                   AppPreferences.setDeviceid(LoginActivity.this, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("GcmRegisterActivity", "Error: " + msg);
                }
                Log.d("GcmRegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                /*Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();*/
            }
        }.execute(null, null, null);
    }


    private void attemptLogin() {


        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(regId)){
            regId = registerGCM();
        }else if (TextUtils.isEmpty(user)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            focusView.requestFocus();
        } else if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            focusView.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            focusView.requestFocus();
        } else {

            if (Function.isOnline(LoginActivity.this)) {
                gps.getLocation();
                dialogManager.showProcessDialog(LoginActivity.this, "");
                mAuthTask = new UserLoginTask(gps.getLatitude(), gps.getLongitude(), user, password);
                mAuthTask.execute((Void) null);
            } else {

                dialogManager.showAlertDialog(LoginActivity.this, "Internet Problem", "No internet connection available", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 2;
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUser;
        private final String mPassword;
        ArrayList<User> userList;
        double latitude, longitude;
        int driverId = 0;

        UserLoginTask(double latitude, double longitude, String user, String password) {
            mUser = user;
            mPassword = password;
            userList = new ArrayList<User>();
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String android_id="";

            android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", mUser);
                jsonObject.put("email", mUser);
                jsonObject.put("password", mPassword);
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
                jsonObject.put("regId", regId);
                jsonObject.put("dateTime", Function.getCurrentDateTime());
                jsonObject.put("android_id",android_id);
                String json = serviceHandler.makeServiceCall(AppConstants.LOGIN, ServiceHandler.POST, jsonObject);

                try {
                    // Simulate network access.
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    return "";
                }

                if (json != null) {
                    JSONObject jsonObject1 = new JSONObject(json);
Log.d("Response json", jsonObject1.toString());
                    if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                        JSONArray jsonArray = jsonObject1.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            driverId = Integer.parseInt(object.getString("id"));
                            User user = new User();
                            user.setId(Long.parseLong(object.getString("id")));
                            //user.setName(object.getString("name"));
                            user.setUserName(object.getString("username"));
                            user.setUserImage(object.getString("driverImage"));
                            user.setPhone(object.getString("mobile"));
                            user.setAddress(object.getString("email"));
                            //user.setAddress(object.getString("address"));

                            userList = db.getUserList();
                            if (userList.isEmpty()) {
                                db.insertUser(user);
                            } else {
                                if (userList.contains(object.getString("id"))) {
                                    db.updateUser(user);
                                } else {
                                    db.insertUser(user);
                                }
                            }
                        }
                        return "200";
                    }

                    if (jsonObject1.getString("status").equalsIgnoreCase("600")) {

                        return "600";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            //showProgress(false);


            if (success.equalsIgnoreCase("200")) {

                AppPreferences.setDriverId(LoginActivity.this, driverId);
                //startService(new Intent(LoginActivity.this, ZoneControlServices.class));
                startService(new Intent(LoginActivity.this, UpdateLocationToServer.class));
                //startService(new Intent(LoginActivity.this, GetTripServices.class));

                /*-------------*/

               /* Intent intent = new Intent(LoginActivity.this, NavigationDrawer.class);
                startActivity(intent);
                finish();*/
                new GetDriverZoneArea().execute();


            }else if(success.equalsIgnoreCase("600")){
                dialogManager.showAlertDialog(LoginActivity.this, "Login", getString(R.string.already_login_with_other_device),null);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
            try {
                dialogManager.stopProcessDialog();
            }catch (IllegalArgumentException e){}

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
            dialogManager.stopProcessDialog();
        }

        private class GetDriverZoneArea extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {


               /* AppPreferences.setRadius(LoginActivity.this, 1000.0);
                AppPreferences.setRadiusLatitude(LoginActivity.this, 22.72144663);
                AppPreferences.setRadiusLongitude(LoginActivity.this, 75.88194609);

                ArrayList<String> vertices1 = new ArrayList<String>();
                vertices1.add("22.73007586/75.87924242");
                vertices1.add("22.72390087/75.87314844");
                vertices1.add("22.72144663/75.88194609");
                vertices1.add("22.72738423/75.88748217");
                vertices1.add("22.72540506/75.87888837");
                //vertices1.add("22.72544464/75.88047624");
                vertices1.add("22.73007586/75.87924242");

                AppPreferences.setVertices(LoginActivity.this, vertices1);*/


                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("driverId", AppPreferences.getDriverId(LoginActivity.this));
                    ServiceHandler serviceHandler = new ServiceHandler();
                    String json = serviceHandler.makeServiceCall(AppConstants.ZONEDRIVEDATA, ServiceHandler.POST, jsonObject);
                    if(json!=null){
                        ArrayList<String> vertices = new ArrayList<String>();
                        JSONObject jsonObject1 = new JSONObject(json);
                        if(jsonObject1.getString("status").equalsIgnoreCase("200")){
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            JSONObject jsonObj = jsonArray.getJSONObject(0);

                            String zoneType = jsonObj.getString("zone_type");
                            if(zoneType.equalsIgnoreCase("circle")){

                                String cordinated = jsonObj.getString("cordinated");
                                Log.d("cordinated", cordinated);
                                //"(22.718191 75.859359, 75.859359),(22.718027, 75.856698),(22.718077, 75.855384),(22.718121, 75.858281)";

                               // (22.7218058872191, 75.87645471691894),(1941.2374108097704)
                                String cordinated1 = cordinated.replace("(","").replace(")", "").replace(" ","");
                                String cortinat[] = cordinated1.split(",");
                               // String cortinatlatlng[] = cortinat[0].split(" ");


                                AppPreferences.setRadiusLatitude(LoginActivity.this, new Double(cortinat[0]));
                                AppPreferences.setRadiusLongitude(LoginActivity.this, new Double(cortinat[1]));
                                AppPreferences.setRadius(LoginActivity.this, new Double(cortinat[2]));


                            }else if(zoneType.equalsIgnoreCase("polygon")){
                                String cordinated = jsonObj.getString("cordinated");
                                Log.d("cordinated", cordinated);
                                        //"(22.718191, 75.859359),(22.718027, 75.856698),(22.718077, 75.855384),(22.718121, 75.858281)";
                                String cordinated1 = cordinated.replace("(","").replace(")","").replace(" ","");
                                String cortinat[] = cordinated1.split(",");
                                for(int i=0;i<cortinat.length; i++){
                                    String lat = cortinat[i];
                                    Log.d("cordinated", lat.toString());
                                    i++;
                                    String lng = cortinat[i];
                                    Log.d("cordinated", lng.toString());
                                    vertices.add(lat+"/"+lng);
                                }
                                Log.d("cordinated", vertices.toString());
                               /* JSONArray jsonArray = jsonObject1.getJSONArray(zoneType);
                                for(int i=0; i<jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    vertices.add(object.getString("lat")+"/"+object.getString("lng"));
                                }*/
                                AppPreferences.setVertices(LoginActivity.this, vertices);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Intent intent = new Intent(LoginActivity.this, NavigationDrawer.class);
                startActivity(intent);
                finish();
            }
        }
    }
}

