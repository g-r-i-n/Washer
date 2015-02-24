package merloni.android.washer.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import merloni.android.washer.R;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class SearchActivity extends Activity {

    public static final int CODE = 1002;

    private boolean receiverRegistered;

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter arrayAdapter;
    private ListView listView;

    private String curDeviceAddress;
    private Intent result;
    private BTBroadcastReceiver receiverNewDevice;
    private BTBroadcastReceiver receiverFinishSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            setContentView(R.layout.activity_search_no_bt);
        } else {
            setContentView(R.layout.activity_search);
            listView = (ListView)findViewById(R.id.list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String)arrayAdapter.getItem(position);
                    curDeviceAddress = item.substring(item.lastIndexOf("\n") + 1);
                    result = new Intent();
                    result.putExtra("mac", curDeviceAddress);
                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, result);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, result);
                    }
                    finish();
                }
            });
            findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search();
                }
            });
            if (bluetoothAdapter.isEnabled()) {
                start();
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, CODE);
            }
        }
        result = new Intent();
        if (getParent() == null) {
            setResult(Activity.RESULT_CANCELED, result);
        } else {
            getParent().setResult(Activity.RESULT_CANCELED, result);
        }
    }

    private void start() {
        arrayAdapter = new ArrayAdapter(this, R.layout.item_device, R.id.device);
        listView.setAdapter(arrayAdapter);
        if (loadBondedDevices()) {
            arrayAdapter.notifyDataSetChanged();
        } else {
            search();
        }
    }

    private void search() {
        findViewById(R.id.general_progress).setVisibility(View.VISIBLE);
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        if (!receiverRegistered) {
            receiverNewDevice = new BTBroadcastReceiver();
            registerReceiver(receiverNewDevice, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            receiverFinishSearch = new BTBroadcastReceiver();
            registerReceiver(receiverFinishSearch, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            receiverRegistered = true;
//            arrayAdapter.add("start");
//            arrayAdapter.notifyDataSetChanged();
        }
        bluetoothAdapter.startDiscovery();
    }

    private boolean loadBondedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        return arrayAdapter.getCount() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        start();
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private class BTBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            arrayAdapter.add("work");
//            arrayAdapter.notifyDataSetChanged();
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                arrayAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                findViewById(R.id.general_progress).setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.cancelDiscovery();
        if (receiverRegistered) {
            unregisterReceiver(receiverNewDevice);
            unregisterReceiver(receiverFinishSearch);
        }
    }

}
