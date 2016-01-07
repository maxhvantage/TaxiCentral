package com.taxicentral.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.taxicentral.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

public class BTParingActivity extends AppCompatActivity {

    private BluetoothAdapter bluetooth;
    private BluetoothSocket socket;
    private UUID uuid = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
    private static int DISCOVERY_REQUEST = 1;

    private ArrayList<BluetoothDevice> foundDevices;
    private ArrayAdapter<BluetoothDevice> aa;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btparing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btn_bluetooth).setOnClickListener(setting);

      /*  // Get the Bluetooth Adapter
        configureBluetooth();
// Setup the ListView of discovered devices
        setupListView();
// Setup search button
        setupSearchButton();
// Setup listen button
        setupListenButton();

        Intent disc = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(disc, DISCOVERY_REQUEST);

        registerReceiver(discoveryResult,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));
        if (!bluetooth.isDiscovering()) {
            foundDevices.clear();
            bluetooth.startDiscovery();
        }

*/

    }

    View.OnClickListener setting = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


    private void configureBluetooth() {
        bluetooth = BluetoothAdapter.getDefaultAdapter();
    }
    private void setupListenButton() {}
    private void setupSearchButton(){}

    private void setupListView() {
        aa = new ArrayAdapter<BluetoothDevice>(this,
                android.R.layout.simple_list_item_1,
                foundDevices);
        list = (ListView)findViewById(R.id.list_discovered);
        list.setAdapter(aa);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        if (requestCode == DISCOVERY_REQUEST) {
            boolean isDiscoverable = resultCode > 0;
            if (isDiscoverable) {
                String name = "bluetoothserver";
                try {
                    final BluetoothServerSocket btserver =
                            bluetooth.listenUsingRfcommWithServiceRecord(name, uuid);
                    AsyncTask<Integer, Void, BluetoothSocket> acceptThread =
                            new AsyncTask<Integer, Void, BluetoothSocket>() {
                                @Override
                                protected BluetoothSocket doInBackground(Integer... params) {

                                    try {
                                        socket = btserver.accept(params[0]*1000);
                                        return socket;
                                    } catch (IOException e) {
                                        Log.d("BLUETOOTH", e.getMessage());
                                    }

                                    return null;
                                }
                                @Override
                                protected void onPostExecute(BluetoothSocket result) {
                                  //  if (result != null)
                                        //switchUI();
                                }
                            };
                    acceptThread.execute(resultCode);
                } catch (IOException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                }
            }
        }
    }

    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice remoteDevice;
            remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetooth.getBondedDevices().contains(remoteDevice)) {
                foundDevices.add(remoteDevice);
                aa.notifyDataSetChanged();
            }
        }
    };

}
