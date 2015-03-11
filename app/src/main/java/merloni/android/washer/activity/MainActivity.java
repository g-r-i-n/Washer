package merloni.android.washer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import merloni.android.washer.R;
import merloni.android.washer.model.*;
import merloni.android.washer.model.Package;
import merloni.android.washer.util.BTManager;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class MainActivity extends Activity implements BTManager.BluetoothExchangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BT_SEARCH = 1001;

    private String mac;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WriteActivity.class));
            }
        });
        findViewById(R.id.read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReadActivity.class);
//                intent.putExtra("mac", mac);
                startActivity(intent);
            }
        });
        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });
///        startActivityForResult(new Intent(MainActivity.this, SearchActivity.class), BT_SEARCH);

        final String SEND_CONST = "A5 EE 02 91 30 05 A0";
        byte[] bytes = Package.hexStringToBytes(SEND_CONST);
        final byte CONTROL_SUMM_BASE_CONST = (byte)(bytes[0] + bytes[1] + bytes[2] + bytes[3] + bytes[4] + bytes[5] + bytes[6]);
        String toSend = "";
        int num = 16 * 256 / 64;
        for (byte i = 0; i < num; i++) {
            byte b = (byte)(i * 64 % 256);
            byte controlSumm = (byte)(CONTROL_SUMM_BASE_CONST + b);
            toSend = "";
            toSend += " " + Package.byteToHexString(b);
            b = (byte)(i / 4);
            controlSumm += b;
            toSend += " " + Package.byteToHexString(b);
            b = (byte)(((i + 1) * 64 - 1) % 256);
            controlSumm += b;
            toSend += " " + Package.byteToHexString(b);
            b = (byte)(((i + 1) * 64 - 1) / 256);
            controlSumm += b;
            toSend += " " + Package.byteToHexString(b);
            toSend = SEND_CONST + toSend + " " + Package.byteToHexString(controlSumm);
            Log.d(TAG, toSend);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == BT_SEARCH) {
                mac = data.getStringExtra("mac");
                name = data.getStringExtra("name");
                BTManager.getInstance().listener = this;
                BTManager.getInstance().setCurrentDevice(mac);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BTManager.getInstance().listener = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTManager.getInstance().stopBT();
    }

    @Override
    public void onSearchFinished() {

    }

    @Override
    public void onSearchError(String text) {

    }

    @Override
    public void oDataSent() {

    }

    @Override
    public void onReceiveData(merloni.android.washer.model.Package pack) {

    }

    @Override
    public void onDataSendingError(String text) {

    }

    @Override
    public void onGeneralError(String text) {

    }

    @Override
    public void onDeviceDisconnected() {

    }

    @Override
    public void onDeviceConnected() {

    }
}
