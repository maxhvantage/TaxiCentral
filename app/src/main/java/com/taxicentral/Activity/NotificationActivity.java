package com.taxicentral.Activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.taxicentral.Adapter.NotificationAdapter;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Model.NotificationModel;
import com.taxicentral.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    NotificationAdapter notificationAdapter;
    List<NotificationModel> modelList;
    ListView notify_listview;
    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notify_listview = (ListView) findViewById(R.id.notify_listview);
        modelList = new ArrayList<NotificationModel>();

        db = new DBAdapter(NotificationActivity.this);
        modelList = db.getAllNotification();

        notificationAdapter = new NotificationAdapter(NotificationActivity.this, modelList);
        notify_listview.setAdapter(notificationAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
