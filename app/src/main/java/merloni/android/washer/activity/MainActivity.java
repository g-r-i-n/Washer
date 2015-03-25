package merloni.android.washer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import merloni.android.washer.R;
import merloni.android.washer.model.*;
import merloni.android.washer.model.Package;
import merloni.android.washer.util.BTManager;
import merloni.android.washer.util.FIle;
import merloni.android.washer.util.FilesManager;
import merloni.android.washer.util.NoInternetConnectionException;
import merloni.android.washer.util.ServerAnswerListener;
import merloni.android.washer.util.WasherManager;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class MainActivity extends Activity implements BTManager.BluetoothExchangeListener, ServerAnswerListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BT_SEARCH = 1001;

    private String mac;
    private String name;

    private Program program;
    private boolean answerReceived;


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
//                Intent intent = new Intent(MainActivity.this, ReadActivity.class);
////                intent.putExtra("mac", mac);
//                startActivity(intent);
                BTManager.getInstance().listener = program;
///                program.startRead();
Program program = new Platform3("", "");
program.context = MainActivity.this;
Package pack = new Package("");
pack.mode = Package.MODE_SIZE;
pack.stringToRead = "5a a5 ee 02 93 1a 02 0b be 0d";
program.onBtReceiveData(pack);
            }
        });
//        findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, SearchActivity.class));
//                }
//        });
        startActivityForResult(new Intent(MainActivity.this, SearchActivity.class), BT_SEARCH);

//        final String SEND_CONST = "A5 EE 02 91 30 05 A0";
//        byte[] bytes = Package.hexStringToBytes(SEND_CONST);
//        final byte CONTROL_SUMM_BASE_CONST = (byte)(bytes[0] + bytes[1] + bytes[2] + bytes[3] + bytes[4] + bytes[5] + bytes[6]);
//        String toSend = "";
//        int num = 16 * 256 / 64;
//        for (byte i = 0; i < num; i++) {
//            byte b = (byte)(i * 64 % 256);
//            byte controlSumm = (byte)(CONTROL_SUMM_BASE_CONST + b);
//            toSend += " " + Package.byteToHexString(b);
//            b = (byte)(i / 4);
//            controlSumm += b;
//            toSend += " " + Package.byteToHexString(b);
//            b = (byte)(((i + 1) * 64 - 1) % 256);
//            controlSumm += b;
//            toSend += " " + Package.byteToHexString(b);
//            b = (byte)(((i + 1) * 64 - 1) / 256);
//            controlSumm += b;
//            toSend += " " + Package.byteToHexString(b);
//            toSend = SEND_CONST + toSend + " " + Package.byteToHexString(controlSumm);
//        }
//        Log.d(TAG, toSend);
//        Platform2 p2 = new Platform2(toSend, "456767654345");
//        p2.saveToFile("test2.bin");
//        p2.setServerAnswerListener(this);
//        try {
//            p2.send(this);
//        } catch (NoInternetConnectionException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

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

                BTManager.getInstance().startClientMode();
                program = new Platform3("", "");
                program.context = this;
                Package pack = new Package(program.startPack);
                pack.mode = Package.MODE_START;
                WasherManager.getInstance().sendPackage(pack);
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String result = getResources().getString(R.string.ready);
                                if (!answerReceived) {
                                    result = getResources().getString(R.string.bt_not_connected);
                                }
                                ((TextView)findViewById(R.id.stats)).setText(result);
                            }
                        });
                    }
                };
                new Timer().schedule(tt, 1000);
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
        program.context = null;
        program = null;
        BTManager.getInstance().listener = null;
        BTManager.getInstance().stopBT();
    }

    @Override
    public void onBtSearchFinished() {

    }

    @Override
    public void onBtSearchError(String text) {

    }

    @Override
    public void onBtDataSent() {

    }

    @Override
    public void onBtReceiveData(merloni.android.washer.model.Package pack) {
        answerReceived = true;
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

    @Override
    public void onServerError(Sendable caller, String message) {

    }

    @Override
    public void onServerLoaded(Sendable caller, boolean ok) {

    }
}
