package merloni.android.washer.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import merloni.android.washer.R;
import merloni.android.washer.util.BTManager;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class SearchActivity extends Activity implements BTManager.BluetoothExchangeListener {

    public static final int CODE = 1002;

    private BTManager btManager;

    private boolean receiverRegistered;

    private ListView listView;

    private BTBroadcastReceiver receiverNewDevice;
    private BTBroadcastReceiver receiverFinishSearch;
    private String curDeviceAddress;
    private Intent result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btManager = BTManager.getInstance();
        if (!btManager.isBTSupported()) {
            setContentView(R.layout.activity_search_no_bt);
        } else {
            setContentView(R.layout.activity_search);
            BTManager.getInstance().listener = this;
            listView = (ListView)findViewById(R.id.list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = (String)BTManager.getInstance().arrayAdapter.getItem(position);
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
            if (BTManager.getInstance().bluetoothAdapter.isEnabled()) {
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
        BTManager.getInstance().arrayAdapter = new ArrayAdapter(this, R.layout.item_device, R.id.device);
        listView.setAdapter(BTManager.getInstance().arrayAdapter);
        if (BTManager.getInstance().loadBondedDevices()) {
            BTManager.getInstance().arrayAdapter.notifyDataSetChanged();
        } else {
            search();
        }
    }

    private void search() {
        findViewById(R.id.general_progress).setVisibility(View.VISIBLE);
        if (BTManager.getInstance().isDiscovering()) {
            BTManager.getInstance().cancelDiscovery();
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
        BTManager.getInstance().startDiscovery();
    }

    private class BTBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                BTManager.getInstance().arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTManager.getInstance().arrayAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                onBtSearchFinished();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTManager.getInstance().listener = null;
        BTManager.getInstance().cancelDiscovery();
        if (receiverRegistered) {
            unregisterReceiver(receiverNewDevice);
            unregisterReceiver(receiverFinishSearch);
        }
    }

    @Override
    public void onBtSearchFinished() {
        findViewById(R.id.general_progress).setVisibility(View.GONE);
    }

    @Override
    public void onBtSearchError(String text) {
        findViewById(R.id.general_progress).setVisibility(View.GONE);
    }

    @Override
    public void onBtDataSent() {

    }

    @Override
    public void onBtReceiveData(merloni.android.washer.model.Package pack) {

    }

    @Override
    public void onBtDataSendingError(String text) {

    }

    @Override
    public void onBtGeneralError(String text) {

    }

    @Override
    public void onBtDeviceDisconnected() {

    }

    @Override
    public void onBtDeviceConnected() {

    }
}
