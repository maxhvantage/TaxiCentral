package com.taxicentral.Activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.taxicentral.Gcm.Config;
import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;

public class RecivedMessage extends Activity {
    //aa
    TextView message, header;
    Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recived_activity_message);

        message = (TextView) findViewById(R.id.message);
        header = (TextView) findViewById(R.id.text);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(ok);

        header.setText(R.string.received_message);
        message.setText(getIntent().getStringExtra("message"));


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(Config.TRIP_RECIVE_MSG_ID);

    }

    View.OnClickListener ok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
        }
    };


}
