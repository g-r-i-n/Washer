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

    private static final String PACK_1 = "a5 ee 02 95 49 02 90 24 29 a5 ee 02 93 10 05 0c 10 f1 f1 00 3b a5 ee 02 93 10 05 0c 10 f1 f1 00 3b";
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
        pack = new Package(PACK_1);
        WasherManager.getInstance().sendPackage(pack);
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
    public void onReceiveData(Package pack) {
        Log.d(TAG, "Received data: " + pack.stringToRead);
        data.setText(pack.stringToRead + "\n\n");
        if (pack.getStringToSend().equals(PACK_1)) {
            pack = new Package(PACK_2);
            WasherManager.getInstance().sendPackage(pack);
        } else if (pack.getStringToSend().equals(PACK_2)) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTManager.getInstance().listener = null;
        BTManager.getInstance().stopBT();
    }
}
