package com.taxicentral.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.taxicentral.R;

public class AnonymousReportSentActivity extends AppCompatActivity {

    Button btn_accept;
    TextView incident_no;
    //aa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_report_sent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        incident_no = (TextView) findViewById(R.id.incident_no);
        btn_accept = (Button) findViewById(R.id.btn_accept);
        btn_accept.setOnClickListener(accept);

        incident_no.setText(getIntent().getStringExtra("id"));

    }

    View.OnClickListener accept = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*Intent intent = new Intent(AnonymousReportSentActivity.this, AnonymousReportActivity.class);
            startActivity(intent);*/
            btn_accept.setClickable(false);
            finish();
        }
    };

}
