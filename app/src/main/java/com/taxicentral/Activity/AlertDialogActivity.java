package com.taxicentral.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.taxicentral.R;
import com.taxicentral.Utils.AppPreferences;

public class AlertDialogActivity extends Activity {

    Button btn_showmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);

        btn_showmap = (Button) findViewById(R.id.showmap);
        btn_showmap.setOnClickListener(showMap);

        this.setFinishOnTouchOutside(false);

    }

    View.OnClickListener showMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(AlertDialogActivity.this, ShowMapsActivity.class));
            AppPreferences.setShowDialog(AlertDialogActivity.this, true);
            //finish();
        }
    };

}
