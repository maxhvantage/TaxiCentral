package com.taxicentral.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MMFA-MANISH on 11/10/2015.
 */
public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, GetTripServices.class);
        context.startService(i);
    }
}