package merloni.android.washer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import merloni.android.washer.R;
import merloni.android.washer.model.Package;
import merloni.android.washer.util.BTManager;
import merloni.android.washer.util.WasherManager;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class ReadActivity extends Activity implements BTManager.BluetoothExchangeListener {

    private static final String TAG = ReadActivity.class.getSimpleName();

    public static final int FILE_CHOOSING = 1002;
    private static final String SEND_CONST = "a5 ee 02 91 30 05 a0";
    private static final byte[] bytes = Package.hexStringToBytes(SEND_CONST);
    final static byte CONTROL_SUMM_BASE_CONST = (byte)(bytes[0] + bytes[1] + bytes[2] + bytes[3] + bytes[4] + bytes[5] + bytes[6]);
    final static int packsAmount = 16 * 256 / 64;

    private static final String PACK_11 = "a5 ee 02 95 49 02 90 24 29";
    private static final String PACK_12 = "a5 ee 02 93 10 05 0c 10 f1 f1 00 3b";
    private static final String PACK_13 = "a5 ee 02 93 10 05 0c 10 f1 f1 00 3b";
    private static final String PACK_2 = "a5 ee 02 93 10 05 0c 1d 80 94 0f 89";
    private static final String PACK_3 = "a5 ee 02 93 10 05 0c 10 36 37 00 c6";
    private static final String PACK_4 = "a5 ee 02 93 10 05 0c 10 ec ee 00 33";
    private static final String PACK_5 = "a5 ee 02 93 20 05 0c 20 00 db 00 54";

    private static final String PACK_6 = "a5 ee 02 93 10 05 0c 1e 8f 8f 0f 94";
    private static final String PACK_7 = "a5 ee 02 93 10 05 0c 1e 97 97 0f a4";
    private static final String PACK_8 = "a5 ee 02 93 10 05 0c 1e 21 21 0f b8";

    private Package[] packs;
    private String toSend = "";
    private int firstPacksAmount;
    private int curPackNumber;

    private TextView data;
    private merloni.android.washer.model.Package pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        BTManager.getInstance().listener = this;
        BTManager.getInstance().startClientMode();
        data = (TextView)findViewById(R.id.text);

        int firstPacksAmount = 7;
        packs = new Package[packsAmount + firstPacksAmount + 3];
        packs[0] = new Package(PACK_11);
        packs[1] = new Package(PACK_12);
        packs[2] = new Package(PACK_13);
        packs[3] = new Package(PACK_2);
        packs[4] = new Package(PACK_3);
        packs[5] = new Package(PACK_4);
        packs[6] = new Package(PACK_5);
        packs[packsAmount + firstPacksAmount] = new Package(PACK_6);
        packs[packsAmount + firstPacksAmount + 1] = new Package(PACK_7);
        packs[packsAmount + firstPacksAmount + 2] = new Package(PACK_8);
        for (int i = 0; i < packsAmount; i++) {
            packs[firstPacksAmount + i] = new Package(getPackByNumber(i));
        }
        WasherManager.getInstance().sendPackage(packs[0]);
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
    public void onReceiveData(final Package p) {
        Log.d(TAG, "Received data: " + p.stringToRead);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data.setText(data.getText().toString() + p.stringToRead + "\n");
            }
        });
        curPackNumber++;
        if (curPackNumber < packsAmount + firstPacksAmount + 3) {
            WasherManager.getInstance().sendPackage(packs[curPackNumber]);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    data.setText(data.getText().toString() + "\nFINISH!");
                }
            });
        }
    }

    public void onReceiveData_(final Package p) {
        Log.d(TAG, "Received data: " + p.stringToRead);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data.setText(data.getText().toString() + p.stringToRead + "\n\n");
            }
        });
        if (p.getStringToSend().equals(PACK_13)) {
            pack = new Package(PACK_2);
            WasherManager.getInstance().sendPackage(pack);
        } else if (p.getStringToSend().equals(PACK_2)) {
            pack = new Package(PACK_3);
            WasherManager.getInstance().sendPackage(pack);
        } else if (p.getStringToSend().equals(PACK_3)) {
            pack = new Package(PACK_4);
            WasherManager.getInstance().sendPackage(pack);
        } else if (p.getStringToSend().equals(PACK_4)) {
            pack = new Package(PACK_5);
            WasherManager.getInstance().sendPackage(pack);
        } else {
            toSend = getPackByNumber(curPackNumber);
            if (curPackNumber < packsAmount) {
                if (p.getStringToSend().equals(PACK_5)) {
                    pack = new Package(toSend);
                    WasherManager.getInstance().sendPackage(pack);
                }
            } else if (p.getStringToSend().equals(PACK_4)) {
                pack = new Package(PACK_5);
                WasherManager.getInstance().sendPackage(pack);
            }
        }
    }

    private String getPackByNumber(int number) {
        String result = "";
        byte b = (byte)(number * 64 % 256);
        byte controlSumm = (byte)(CONTROL_SUMM_BASE_CONST + b);
        result += " " + Package.byteToHexString(b);
        b = (byte)(number / 4);
        controlSumm += b;
        result += " " + Package.byteToHexString(b);
        b = (byte)(((number + 1) * 64 - 1) % 256);
        controlSumm += b;
        result += " " + Package.byteToHexString(b);
        b = (byte)(((number + 1) * 64 - 1) / 256);
        controlSumm += b;
        result += " " + Package.byteToHexString(b);
        result = SEND_CONST + result + " " + Package.byteToHexString(controlSumm);
        Log.d(TAG, "To send: " + result);
        return result;
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
        pack = new Package(PACK_11);
        WasherManager.getInstance().sendPackage(pack);
        pack = new Package(PACK_12);
        WasherManager.getInstance().sendPackage(pack);
        pack = new Package(PACK_13);
        WasherManager.getInstance().sendPackage(pack);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTManager.getInstance().listener = null;
        BTManager.getInstance().stopBT();
    }
}
