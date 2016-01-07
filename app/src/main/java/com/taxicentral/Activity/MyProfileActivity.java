package com.taxicentral.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Model.User;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;

import java.io.IOException;
import java.net.URI;
import java.net.URL;


public class MyProfileActivity extends AppCompatActivity {

    Button btn_change_pass;
    DBAdapter db;
    EditText name_tv, phone_tv, email_tv, address_tv;
    ImageView userImage;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DBAdapter(MyProfileActivity.this);
        user = new User();

        userImage = (ImageView) findViewById(R.id.userImage);
        name_tv = (EditText) findViewById(R.id.name_tv);
                phone_tv = (EditText) findViewById(R.id.phone_tv);
        email_tv = (EditText) findViewById(R.id.email_tv);
                address_tv = (EditText) findViewById(R.id.address_tv);
        btn_change_pass = (Button) findViewById(R.id.btn_change_pass);
        btn_change_pass.setOnClickListener(changePassword);

        user = db.getUser(AppPreferences.getDriverId(MyProfileActivity.this));
        Log.d("userData", user.getUserName() + " : " + user.getPhone() + " : " + user.getEmailId() + " : " + user.getAddress() + " : " + user.getUserImage());
        name_tv.setText(user.getUserName());
        phone_tv.setText(user.getPhone());
        email_tv.setText(user.getEmailId());
        address_tv.setText(user.getAddress());

        Picasso.with(this)
                .load(user.getUserImage())
                .error(R.drawable.ic_action_user)
                .resize(200, 200)
                .into(userImage);

    }

    View.OnClickListener changePassword = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MyProfileActivity.this, ChangePasswordActivity.class);
            intent.putExtra("userImage", user.getUserImage());
            startActivity(intent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
