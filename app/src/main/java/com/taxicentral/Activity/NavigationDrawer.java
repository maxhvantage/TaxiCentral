package com.taxicentral.Activity;

import android.app.Dialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxicentral.Database.DBAdapter;
import com.taxicentral.Model.NotificationModel;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class NavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //aa
    User user;
    DBAdapter db;
    ImageView driverimage;
    TextView driverName;
    NavigationDrawer instance = null;
    NavigationView navigationView;
    DialogManager dialogManager;
    Fragment fragment;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instance = this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.nav_activity).setChecked(true);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_navigation_drawer);

        driverimage = (ImageView) headerLayout.findViewById(R.id.driverimage);
        driverName = (TextView) headerLayout.findViewById(R.id.driver_tv);


        if(!Function.isServiceRunning(instance, "com.taxicentral.Services.ZoneControlServices")){
            startService(new Intent(instance, ZoneControlServices.class));
        }
        if(!Function.isServiceRunning(instance, "com.taxicentral.Services.UpdateLocationToServer")){
            startService(new Intent(instance, UpdateLocationToServer.class));
        }
        if(!Function.isServiceRunning(instance, "com.taxicentral.Services.GetTripServices")){
           // startService(new Intent(instance, GetTripServices.class));
        }



        dialogManager = new DialogManager();

        user = new User();
        db = new DBAdapter(instance);
        user = db.getUser(AppPreferences.getDriverId(instance));
        driverName.setText(user.getUserName());
        Picasso.with(this)
                .load(user.getUserImage())
                .error(R.drawable.ic_action_user)
                .resize(200, 200)
                .into(driverimage);
        data = getIntent().getStringExtra("notificationData");
        openHomeActivity(data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        Drawable drawable = menu.findItem(R.id.action_call).getIcon();

       /* drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.white));
        menu.findItem(R.id.action_call).setIcon(drawable);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_call) {
            call();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        navigationView.getMenu().findItem(R.id.nav_activity).setChecked(true);

        if (id == R.id.nav_activity) {
            data= null;
            openHomeActivity(data);
        } else if (id == R.id.nav_trip_history) {
            Intent intent = new Intent(instance, TripHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_account_statement) {
            Intent intent = new Intent(instance, AccountStatementActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_my_profile) {
            Intent intent = new Intent(instance, MyProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(instance, NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            Intent intent = new Intent(instance, NewsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_anonymous_report) {
            Intent intent = new Intent(instance, AnonymousReportActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_bt_paiting) {
            Intent intent = new Intent(instance, BTParingActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NavigationDrawer.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.logout_dialog, null);
            dialogBuilder.setView(dialogView);

            dialogBuilder.setCancelable(false);View header = inflater.inflate(R.layout.dialog_heading, null);
            TextView textView = (TextView) header.findViewById(R.id.text);
            textView.setText(R.string.logout);
            dialogBuilder.setCustomTitle(header);

            final AlertDialog alertDialog = dialogBuilder.create();

            Button accept = (Button) dialogView.findViewById(R.id.btn_confirm);
            Button cancel = (Button) dialogView.findViewById(R.id.btn_cancel);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Function.isOnline(NavigationDrawer.this)) {
                        new LogoutTask().execute();
                    } else {
                       // dialogManager.showAlertDialog(NavigationDrawer.this, getString(R.string.error_connection_problem), getString(R.string.error_check_internet_connection), null);
                        Snackbar.make(findViewById(android.R.id.content),getString(R.string.error_check_internet_connection),Snackbar.LENGTH_LONG).show();
                    }

                    alertDialog.dismiss();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();


        }
        navigationView.getMenu().findItem(R.id.nav_activity).setChecked(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   public void openHomeActivity(String data1) {


       if( data1!= null){

           try {
               JSONObject object = new JSONObject(data1);
               String trip_id = object.getString("trip_id");
               String message = object.getString("canceltaxirequest");

           AlertDialog.Builder builder = new AlertDialog.Builder(NavigationDrawer.this);
           //builder.setTitle(getString(R.string.cancel_trip));
           //builder.setIcon(R.drawable.ic_launcher);
           LayoutInflater inflater = getLayoutInflater();
           View header = inflater.inflate(R.layout.dialog_heading, null);
           TextView textView = (TextView) header.findViewById(R.id.text);
           ImageView icon = (ImageView) header.findViewById(R.id.icon);
           icon.setImageResource(R.drawable.ic_launcher);
           textView.setText(R.string.cancel_trip);
           builder.setCustomTitle(header);
           builder.setCancelable(false);
           builder.setMessage("Trip id : " + trip_id + "\nMessage : " + message);

           builder.setPositiveButton(getString(R.string.cancel),
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                       }
                   });

           builder.show();

           } catch (JSONException e) {
               e.printStackTrace();
           }

           data = null;
       }

       fragment = new MainScreen();
       if (fragment != null) {
           FragmentManager fragmentManager = getSupportFragmentManager();
           fragmentManager.beginTransaction()
                   .replace(R.id.frame_container, fragment).commit();

       } else {
           // error in creating fragment
           Log.e("MainScreen", "Error in creating fragment");
       }
    }

    private void call() {
        Intent in = new Intent(Intent.ACTION_CALL);
        in.setData(Uri.parse("tel:123456789"));
        try {
            startActivity(in);
        } catch (android.content.ActivityNotFoundException ex) {

            Snackbar.make(findViewById(android.R.id.content), R.string.call_not_allow, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private class LogoutTask extends AsyncTask<String, Void, Boolean> {


        public LogoutTask(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialogManager.showProcessDialog(instance, "");
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                ServiceHandler serviceHandler = new ServiceHandler();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("driverId", String.valueOf(AppPreferences.getDriverId(instance)));
                String json = serviceHandler.makeServiceCall(AppConstants.LOGOUT, ServiceHandler.POST, jsonObject);
                if(json != null){
                    JSONObject object = new JSONObject(json);
                    if(object.getString("status").equalsIgnoreCase("200")){
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            super.onPostExecute(status);
            if(status){
                stopService(new Intent(instance, ZoneControlServices.class));
                stopService(new Intent(instance, UpdateLocationToServer.class));
                //stopService(new Intent(instance, GetTripServices.class));
                GetTripServices.shouldContinue = false;
                AppPreferences.setDriverId(instance, 0);
                db.deleteAllTrip();
                dialogManager.stopProcessDialog();
                Intent intent = new Intent(instance, LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                dialogManager.stopProcessDialog();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.setShowDialog(instance, false);
    }
}
