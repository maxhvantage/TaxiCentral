package com.taxicentral.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;

public class RecivedMessage extends Activity {

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



    }

    View.OnClickListener ok = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();
        }
    };

}
