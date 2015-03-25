package merloni.android.washer.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;

import merloni.android.washer.util.BTManager;
import merloni.android.washer.util.FIle;
import merloni.android.washer.util.FilesManager;
import merloni.android.washer.util.NoInternetConnectionException;
import merloni.android.washer.util.ServerAnswerListener;
import merloni.android.washer.util.WasherManager;

/**
 * Created by Ivan Grinichenko on 15.01.2015.
 */
public class Program extends BaseObject implements BTManager.BluetoothExchangeListener {

    private static final String TAG = Program.class.getSimpleName();

    public String startPack;
    public String serialPack;

    public Context context;

    protected String sendConst;
    protected byte[] bytes;
    byte controlSummBaseConst;
    final static int packsAmount = 16 * 256 / 64;

//    private static final String SEND_CONST = "a5 ee 02 91 30 05 a0";

    private String toSend = "";
    protected Package[] packs;
    protected int firstPacksAmount;
    private int curPackNumber;

    public Program(String toSend, String imei) {
        this.toSend = toSend;
        BTManager.getInstance().listener = this;
        sendPost = true;
        pageAddress = "/android.php";
        parameters = "emai=" + imei + "&firmware=";
    }

    public void startRead() {

    }

    public void prepareToSend() {
        postData = toSend;
    }

    public void saveToFile(String fileName) {
        FIle file = new FIle(fileName, true);
        file.data = Package.hexStringToBytes(toSend);
        file.saveMode = FilesManager.SAVE_MODE_NEW;
        file.save();
    }

    public void readFirmware() {
    }

    protected String getPackByNumber(int number) {
        String result = "";
        byte b = (byte)(number * 64 % 256);
        byte controlSumm = (byte)(controlSummBaseConst + b);
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
        result = sendConst + result + " " + Package.byteToHexString(controlSumm);
        Log.d(TAG, "To send: " + result);
        return result;
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
    public void onBtReceiveData(Package pack) {
        Log.d(TAG, "Received: " + pack.stringToRead);
//        toSend += " " + pack.stringToRead;
////        runOnUiThread(new Runnable() {
////            @Override
////            public void run() {
////                dataView.setText(dataView.getText().toString() + pack.stringToRead + "\n");
////            }
////        });
//        curPackNumber++;
//        if (curPackNumber < packsAmount + firstPacksAmount + 3) {
//            WasherManager.getInstance().sendPackage(packs[curPackNumber]);
//        } else {
//            try {
//                Log.d(TAG, "Firmware: " + toSend);
//                toSend = toSend.replace(" ", "");
//                saveToFile("test.txt");
//                send(context);
//                setServerAnswerListener((ServerAnswerListener)context);
//            } catch (NoInternetConnectionException e) {
//                e.printStackTrace();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
////            runOnUiThread(new Runnable() {
////                @Override
////                public void run() {
////                    dataView.setText(dataView.getText().toString() + pack.stringToRead + "\nFINISH!");
////                }
////            });
//        }
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
        readFirmware();
    }

    @Override
    public void parse() {

    }
}