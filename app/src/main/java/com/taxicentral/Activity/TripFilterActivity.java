package com.taxicentral.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.taxicentral.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TripFilterActivity extends AppCompatActivity {
    //aa
    EditText from_date,to_date;
    Button btn_filter;
    SimpleDateFormat dateFormatter_fromDate;
    DatePickerDialog DatePickerDialog_fromDate;
    SimpleDateFormat dateFormatter_toDate;
    DatePickerDialog DatePickerDialog_toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        from_date = (EditText) findViewById(R.id.from_date);
        to_date = (EditText) findViewById(R.id.to_date);
        btn_filter = (Button) findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(filter);


        from_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DatePickerDialog_fromDate.show();
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.add(Calendar.MONTH, -1);
        dateFormatter_fromDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        DatePickerDialog_fromDate = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                       String fromDate = dateFormatter_fromDate.format(newDate
                                .getTime());

                        from_date.setText(fromDate);

                    }

                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));



        to_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DatePickerDialog_toDate.show();
            }
        });
        //Calendar newCalendar1 = Calendar.getInstance();
        newCalendar.add(Calendar.MONTH, -1);
        dateFormatter_toDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        DatePickerDialog_toDate = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        String toDate = dateFormatter_toDate.format(newDate
                                .getTime());

                        to_date.setText(toDate);

                    }

                }, newCalendar.get(Calendar.YEAR),
                newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    View.OnClickListener filter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
