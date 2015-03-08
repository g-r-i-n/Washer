package merloni.android.washer.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import merloni.android.washer.R;
import merloni.android.washer.util.BTManager;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class ReadActivity extends Activity implements BTManager.BluetoothExchangeListener {

    public static final int FILE_CHOOSING = 1002;
    private static final String TAG = ReadActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        BTManager.getInstance().listener = this;
        BTManager.getInstance().startClientMode();
        findViewById(R.id.read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView)findViewById(R.id.text)).setText("Sending STARTED!");
                String value = "a5 ee 02 95 49 02 90 24 29";
                BTManager.getInstance().sendToCurrentDevice(stringToBytes(value));
                value = "a5 ee 02 93 10 05 0c 10 f1 f1 00 3b";
                BTManager.getInstance().sendToCurrentDevice(stringToBytes(value));
                value = "a5 ee 02 93 10 05 0c 10 f1 f1 00 3b";
                BTManager.getInstance().sendToCurrentDevice(stringToBytes(value));
            }
        });
    }

    public byte[] stringToBytes(String value) {
        int size = (value.length() + 1) / 3;
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) ((Character.digit(value.charAt(i * 3), 16) << 4) + Character.digit(value.charAt(i * 3 + 1), 16));
            Log.d(TAG, "Cur byte: " + result[i]);
        }
        return result;
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
    public void onReceiveData(byte[] values, int bytes) {
        Log.d(TAG, "Received data: " + new String(values));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTManager.getInstance().listener = null;
        BTManager.getInstance().stopBT();
    }
}
