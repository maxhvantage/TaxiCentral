package com.taxicentral.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bugsnag.android.Bugsnag;
import com.taxicentral.Classes.FusedLocationService;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.FontsOverride;

import java.util.Locale;

public class SplashActivity extends Activity {

//aa
private static final String[] LOCATION_PERMS={
        Manifest.permission.ACCESS_FINE_LOCATION
};


    public static SplashActivity instance = null;
    GPSTracker gps;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_splash);
instance =this;
        Bugsnag.init(this);

        FontsOverride.setDefaultFont(this, "MONOSPACE", "MontserratRegular.ttf");

        if(Build.VERSION.SDK_INT >= 23) {
            requestPermissions(LOCATION_PERMS, 0);
        }

       // AppPreferences.setShowDialog(this, true);
        //setLocaleFa(SplashActivity.this);


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (canAccessLocation()) {
            gps = new GPSTracker(SplashActivity.this);
            gps.getLocation();
            Log.d("checkLocation1111", gps.getLatitude() + " : " + gps.getLongitude());
        }
    }
    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }


    public static void setLocaleFa (Context context){
        Locale locale = new Locale("es");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
    }



    @Override
    protected void onResume() {
        super.onResume();

      /*  final GPSTracker gps = new GPSTracker(SplashActivity.this);
        gps.getLocation();*/

        if(Build.VERSION.SDK_INT< 23) {
            gps = new GPSTracker(SplashActivity.this);
            gps.getLocation();
        }
            final Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
//                        Log.d("checkLocation", gps.getLatitude() +" : " + gps.getLongitude());
                        //Log.d("checkLocation", fusedLocationService.getLocation().getLatitude() +" : "+ fusedLocationService.getLocation().getLongitude());
                        if(Build.VERSION.SDK_INT>= 23) {
                            if (AppPreferences.getDriverId(SplashActivity.this) == 0) {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                intent.setAction("");
                                startActivity(intent);
                              //  finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, NavigationDrawer.class);
                                //Intent intent = new Intent(SplashActivity.this, Rough.class);
                                startActivity(intent);
                               // finish();
                            }
                        } else if(gps == null){

                        }else if(gps.getLatitude() == 0.0 && gps.getLongitude() == 0.0){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    showSettingsAlert();
                                }
                            });

                        }else {

                            if (AppPreferences.getDriverId(SplashActivity.this) == 0) {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                intent.setAction("");
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, NavigationDrawer.class);
                                //Intent intent = new Intent(SplashActivity.this, Rough.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }

            };
            timerThread.start();

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
        alertDialog.setTitle("GPS Settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to enable it in settings?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
