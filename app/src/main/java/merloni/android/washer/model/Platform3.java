package merloni.android.washer.model;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import merloni.android.washer.util.WasherManager;

/**
 * Created by Ivan Grinichenko on 12.03.2015.
 */
public class Platform3 extends Program {

    private static final String TAG = Platform3.class.getSimpleName();

    private static final String FOR_SIZE = "a5 ee 02 93 10 05 90 1a 00 01 01 e9";

//    private static final String PACK_3 = "a5 ee 02 93 10 05 90 18 06 06 01 f2";
//    private static final String PACK_4 = "a5 ee 02 93 10 05 90 18 07 07 01 f4";
//    private static final String PACK_5 = "a5 ee 02 93 10 05 90 1a 00 01 01 e9";
//    private static final String PACK_6 = "a5 ee 02 93 10 05 90 11 7e 83 03 e2";
//    private static final String PACK_7 = "a5 ee 02 93 10 05 90 11 84 89 03 ee";
//    private static final String PACK_8 = "a5 ee 02 93 10 05 90 11 8a 8f 03 fa";
//    private static final String PACK_9 = "a5 ee 02 93 10 05 90 11 90 9d 03 0e";
//    private static final String PACK_10 = "a5 ee 02 93 10 05 90 1c dd dd 00 a3";
//    private static final String PACK_11 = "a5 ee 02 93 10 05 90 1f 31 31 00 4e";
//    private static final String PACK_12 = "a5 ee 02 93 10 05 90 1c dd de 00 a4";
//    private static final String PACK_13 = "a5 ee 02 93 10 05 90 1f 31 3a 00 57";
//
////    private static final String PACK_6 = "a5 ee 02 93 10 05 0c 1e 8f 8f 0f 94";
////    private static final String PACK_7 = "a5 ee 02 93 10 05 0c 1e 97 97 0f a4";
////    private static final String PACK_8 = "a5 ee 02 93 10 05 0c 1e 21 21 0f b8";

    private Package pack;

    private int proshSize;

    public Platform3(String toSend, String imei) {
        super(toSend, imei);
        startPack = "a5 ee 02 95 49 02 90 24 29";
        sendConst = "a5 ee 02 93 10 05 90";
        bytes = Package.hexStringToBytes(sendConst);
        controlSummBaseConst = (byte)(bytes[0] + bytes[1] + bytes[2] + bytes[3] + bytes[4] + bytes[5] + bytes[6]);

//        pageAddress = "/washer.php";
//        parameters = "?action=fileUpload&owner=vasya&file_name=proshivka";
    }

    public void startRead() {
        pack = new Package(FOR_SIZE);
        pack.mode = Package.MODE_SIZE;
        WasherManager.getInstance().sendPackage(pack);
    }

    @Override
    public void onBtReceiveData(Package pack) {
        Log.d(TAG, "Received: " + pack.stringToRead);
        if (pack.mode == Package.MODE_SIZE) {
            String l = pack.stringToRead.substring(21, 26);
            Log.d(TAG, "Length: " + l);
            proshSize = Package.bytesToInt(Package.hexStringToBytes(l));
            String zapros = getData(proshSize, "05 f8", 5);
            Log.d(TAG, "Request for serial: " + zapros);
            pack = new Package(zapros);
            pack.mode = Package.MODE_SERIAL;
            WasherManager.getInstance().sendPackage(pack);
        } else if (pack.mode == Package.MODE_SERIAL) {
            String zapros = getData(proshSize, "05 f2", 5);
            Log.d(TAG, "Request for firmware: " + zapros);
            pack = new Package(zapros);
            pack.mode = Package.MODE_FIRMWARE_NAME;
            WasherManager.getInstance().sendPackage(pack);
        } else if (pack.mode == Package.MODE_FIRMWARE_NAME) {
            String zapros = getData(proshSize, "05 ec", 13);
            Log.d(TAG, "Request for firmware: " + zapros);
            pack = new Package(zapros);
            pack.mode = Package.MODE_MODEL;
            WasherManager.getInstance().sendPackage(pack);
        } else if (pack.mode == Package.MODE_MODEL) {
        }
    }

    private String getData(int value, String offset, int length) {
        String data = sendConst;
        value = value * 2 - Package.bytesToInt(Package.hexStringToBytes(offset));
        byte[] bytes = Package.intToBytes(value);
        data += " " + Package.bytesToHexString(bytes, 0, bytes.length);
        byte b = bytes[bytes.length - 1];
        bytes = new byte[1];
        bytes[0] = (byte)(b + length);
        data += " " + Package.bytesToHexString(bytes, 0, 1) + " 03";
        data += " " + Package.getControlSum(data);
        return data;
    }

    public void prepareToSend() {
        super.prepareToSend();
    }

    public void readFirmware() {
//        pack = new Package(PACK_11);
//        WasherManager.getInstance().sendPackage(pack);
//        pack = new Package(PACK_12);
//        WasherManager.getInstance().sendPackage(pack);
//        firstPacksAmount = 13;
//        packs = new Package[packsAmount + firstPacksAmount + 3];
//        packs[0] = new Package(PACK_13);
//        packs[1] = new Package(PACK_2);
//        packs[2] = new Package(PACK_3);
//        packs[3] = new Package(PACK_4);
//        packs[4] = new Package(PACK_5);
//        packs[packsAmount + firstPacksAmount] = new Package(PACK_6);
//        packs[packsAmount + firstPacksAmount + 1] = new Package(PACK_7);
//        packs[packsAmount + firstPacksAmount + 2] = new Package(PACK_8);
//        for (int i = 0; i < packsAmount; i++) {
//            packs[firstPacksAmount + i] = new Package(getPackByNumber(i));
//        }
//        WasherManager.getInstance().sendPackage(packs[0]);
    }

}
