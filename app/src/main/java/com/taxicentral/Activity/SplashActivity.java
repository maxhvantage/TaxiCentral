package com.taxicentral.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bugsnag.android.Bugsnag;
import com.taxicentral.Classes.GPSTracker;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;
import com.taxicentral.Utils.FontsOverride;

import java.util.Locale;

public class SplashActivity extends Activity {

//aa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        setContentView(R.layout.activity_splash);

        Bugsnag.init(this);

        FontsOverride.setDefaultFont(this, "MONOSPACE", "MontserratRegular.ttf");


        //setLocaleFa(SplashActivity.this);
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

        final GPSTracker gps = new GPSTracker(SplashActivity.this);
        gps.getLocation();

            final Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                        if(gps.getLatitude() == 0.0 && gps.getLongitude() == 0.0){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    showSettingsAlert();
                                }
                            });

                        }else {
                            Log.d("checkLocation", gps.getLatitude() +" : "+ gps.getLongitude());
                            if (AppPreferences.getDriverId(SplashActivity.this) == 0) {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SplashActivity.this, NavigationDrawer.class);
                                //Intent intent = new Intent(SplashActivity.this, PaymentActivity.class);
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
}
