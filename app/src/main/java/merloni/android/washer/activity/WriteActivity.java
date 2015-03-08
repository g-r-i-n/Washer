package merloni.android.washer.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import merloni.android.washer.R;
import merloni.android.washer.model.*;
import merloni.android.washer.util.BTManager;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class WriteActivity extends Activity implements BTManager.BluetoothExchangeListener {

    public static final int FILE_CHOOSING = 1002;
    private static final String TAG = WriteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        showFileChooser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == FILE_CHOOSING) {
                Uri uri = data.getData();
                Log.d(TAG, "File Uri: " + uri.getPath());
                sendFile(uri.getPath());
            }
        }
    }

    private void sendFile(String path) {
        File file = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int bufferLength = 0;
///        while ( (bufferLength = fis.read(buffer)) > 0 ) {
//            mmOutStream.write(buffer, 0, bufferLength);
//        }
//
//        connectThreadWrite = new ConnectThread(bluetoothDevice, path);
//        connectThreadWrite.start();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.choose_file)), FILE_CHOOSING);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getResources().getString(R.string.install_fm),
                    Toast.LENGTH_SHORT).show();
        }
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
