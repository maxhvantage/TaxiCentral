package com.taxicentral.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Gcm.Config;
import com.taxicentral.Model.User;
import com.taxicentral.R;
import com.taxicentral.Services.GetTripServices;
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

    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialogManager = new DialogManager();
        db = new DBAdapter(LoginActivity.this);
        gps = new GPSTracker(LoginActivity.this);

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
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Function.hideSoftKeyboard(LoginActivity.this);
                attemptLogin();
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
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
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
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

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
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", mUser);
                jsonObject.put("email", mUser);
                jsonObject.put("password", mPassword);
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
                jsonObject.put("regId", regId);

                String json = serviceHandler.makeServiceCall(AppConstants.LOGIN, ServiceHandler.POST, jsonObject);

                try {
                    // Simulate network access.
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    return false;
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
                            user.setUserImage(object.getString("userImage"));
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
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);


            if (success) {
                Intent intent = new Intent(LoginActivity.this, NavigationDrawer.class);
                startActivity(intent);
                AppPreferences.setDriverId(LoginActivity.this, driverId);
                startService(new Intent(LoginActivity.this, ZoneControlServices.class));
                startService(new Intent(LoginActivity.this, UpdateLocationToServer.class));
                //startService(new Intent(LoginActivity.this, GetTripServices.class));

                /*-------------*/
                AppPreferences.setRadius(LoginActivity.this, 1000.0);
                AppPreferences.setRadiusLatitude(LoginActivity.this, 22.726498);
                AppPreferences.setRadiusLongitude(LoginActivity.this, 75.879785);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
            dialogManager.stopProcessDialog();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
            dialogManager.stopProcessDialog();
        }
    }
}

