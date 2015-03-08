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

    private static final String PACK_11 = "a5 ee 02 95 49 02 90 24 29";
    private static final String PACK_12 = "a5 ee 02 93 10 05 0c 10 f1 f1 00 3b";
    private static final String PACK_13 = "a5 ee 02 93 10 05 0c 10 f1 f1 00 3b";
    private static final String PACK_2 = "a5 ee 02 93 10 05 0c 1d 80 94 0f 89";
    private static final String PACK_3 = "a5 ee 02 93 10 05 0c 10 36 37 00 c6";

    public static final int FILE_CHOOSING = 1002;
    private static final String TAG = ReadActivity.class.getSimpleName();

    private TextView data;
    private merloni.android.washer.model.Package pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        BTManager.getInstance().listener = this;
        BTManager.getInstance().startClientMode();
        data = (TextView)findViewById(R.id.text);
//        startExchange();
    }

//    private void startExchange() {
//        pack = new Package("a5 ee 02 95 49 02 90 24 29");
//        WasherManager.getInstance().sendPackage(pack);
//        pack = new Package("a5 ee 02 93 10 05 0c 10 f1 f1 00 3b");
//        WasherManager.getInstance().sendPackage(pack);
//        pack = new Package("a5 ee 02 93 10 05 0c 10 f1 f1 00 3b");
//        WasherManager.getInstance().sendPackage(pack);
//    }

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
                data.setText(data.getText().toString() + p.stringToRead + "\n\n");
            }
        });
        if (p.getStringToSend().equals(PACK_13)) {
            pack = new Package(PACK_2);
            WasherManager.getInstance().sendPackage(pack);
        } else if (p.getStringToSend().equals(PACK_2)) {
            pack = new Package(PACK_3);
            WasherManager.getInstance().sendPackage(pack);
        }
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
