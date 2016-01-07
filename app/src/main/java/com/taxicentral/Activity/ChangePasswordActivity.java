package com.taxicentral.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.games.snapshot.Snapshot;
import com.squareup.picasso.Picasso;
import com.taxicentral.R;
import com.taxicentral.Services.ServiceHandler;
import com.taxicentral.Utils.AppConstants;
import com.taxicentral.Utils.AppPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {

    Button btn_save_pass;
    EditText old_pass, new_pass, verify_pass;
    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        old_pass = (EditText) findViewById(R.id.old_pass);
        new_pass = (EditText) findViewById(R.id.new_pass);
        verify_pass = (EditText) findViewById(R.id.verify_pass);
        userImage = (ImageView) findViewById(R.id.userImage);
        btn_save_pass = (Button) findViewById(R.id.btn_save_pass);
        btn_save_pass.setOnClickListener(savePassword);

        verify_pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.btn_save_pass || id == EditorInfo.IME_NULL) {
                    savePassword();
                    return true;
                }
                return false;
            }
        });

        Picasso.with(this)
                .load(getIntent().getStringExtra("userImage"))
                .error(R.drawable.ic_action_user)
                .resize(200, 200)
                .into(userImage);

    }

    View.OnClickListener savePassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            savePassword();
        }
    };

    public void savePassword(){
        String old_password = old_pass.getText().toString();
        String new_password = new_pass.getText().toString();
        String verify_password = verify_pass.getText().toString();

        View focusView = null;

        if(TextUtils.isEmpty(old_password)){
            old_pass.setError(getString(R.string.error_field_required));
            focusView = old_pass;
            focusView.requestFocus();
        }else if(TextUtils.isEmpty(new_password)){
            new_pass.setError(getString(R.string.error_field_required));
            focusView = new_pass;
            focusView.requestFocus();
        }else if(TextUtils.isEmpty(verify_password)){
            verify_pass.setError(getString(R.string.error_field_required));
            focusView = verify_pass;
            focusView.requestFocus();
        }else if(!new_password.equals(verify_password)){
            verify_pass.setError(getString(R.string.error_password_not_matched));
            focusView = verify_pass;
            focusView.requestFocus();
        }else{
            new SavePasswordTask(old_password, new_password).execute();
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

    private class SavePasswordTask extends AsyncTask<String, Void, Integer> {

        String old_password;
        String new_password;

        public  SavePasswordTask(String old_password, String new_password){
                this.old_password = old_password;
            this.new_password = new_password;
        }
        @Override
        protected Integer doInBackground(String... params) {
            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject object = new JSONObject();
                object.put("driverId", String.valueOf(AppPreferences.getDriverId(ChangePasswordActivity.this)));
                object.put("oldPassword", old_password);
                object.put("newPassword", new_password);

                String json = serviceHandler.makeServiceCall(AppConstants.CHANGEPASSWORD, ServiceHandler.POST, object);
                if(json != null){
                    Log.d("Password Change", json);
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getString("status").equals("200")){
                        return 200;
                    }else if(jsonObject.getString("status").equals("400")){
                        return 400;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 999;
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            if(status == 200){
                finish();
            }else if(status == 400) {
                Snackbar.make(findViewById(android.R.id.content), R.string.erro_password_not_matched, Snackbar.LENGTH_LONG)
                        .show();
            }else if(status == 999) {
                Snackbar.make(findViewById(android.R.id.content), R.string.server_not_response, Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }
}

