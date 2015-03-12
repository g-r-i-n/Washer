package merloni.android.washer.model;

import merloni.android.washer.util.WasherManager;

/**
 * Created by Ivan Grinichenko on 12.03.2015.
 */
public class Platform2 extends Program {


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

    private merloni.android.washer.model.Package pack;

    public Platform2(String toSend, String imei) {
        super(toSend, imei);
        sendConst = "a5 ee 02 91 30 05 a0";
        bytes = Package.hexStringToBytes(sendConst);
        controlSummBaseConst = (byte)(bytes[0] + bytes[1] + bytes[2] + bytes[3] + bytes[4] + bytes[5] + bytes[6]);

//        pageAddress = "/washer.php";
//        parameters = "?action=fileUpload&owner=vasya&file_name=proshivka";
    }

    public void prepareToSend() {
        super.prepareToSend();
    }

    public void readFirmware() {
        pack = new Package(PACK_11);
        WasherManager.getInstance().sendPackage(pack);
        pack = new Package(PACK_12);
        WasherManager.getInstance().sendPackage(pack);
        firstPacksAmount = 5;
        packs = new Package[packsAmount + firstPacksAmount + 3];
        packs[0] = new Package(PACK_13);
        packs[1] = new Package(PACK_2);
        packs[2] = new Package(PACK_3);
        packs[3] = new Package(PACK_4);
        packs[4] = new Package(PACK_5);
        packs[packsAmount + firstPacksAmount] = new Package(PACK_6);
        packs[packsAmount + firstPacksAmount + 1] = new Package(PACK_7);
        packs[packsAmount + firstPacksAmount + 2] = new Package(PACK_8);
        for (int i = 0; i < packsAmount; i++) {
            packs[firstPacksAmount + i] = new Package(getPackByNumber(i));
        }
        WasherManager.getInstance().sendPackage(packs[0]);
    }

}
