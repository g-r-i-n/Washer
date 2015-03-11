package merloni.android.washer.model;

import android.util.Log;

/**
 * Created by Ivan Grinichenko on 08.03.2015.
 */
public class Package {

    private static final String TAG = Package.class.getSimpleName();
    final protected static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private String stringToSend;
    public String stringToRead;

    public byte[] bytesToSend;
    public byte[] bytesToRead;

    public Package(String value) {
        initDataToSend(value);
    }

    public byte[] initDataToSend(String value) {
        stringToSend = value;
        bytesToSend = hexStringToBytes(stringToSend);
        return bytesToSend;
    }

    public String initDataToRead(byte[] bytes, int offset, int length) {
        bytesToRead = bytes;
//        stringToRead = new String(bytes, offset, length);
        stringToRead = bytesToHexString(bytes, offset, length);
        return stringToRead;
    }

    public static byte[] hexStringToBytes(String value) {
        int size = (value.length() + 1) / 3;
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) ((Character.digit(value.charAt(i * 3), 16) << 4) + Character.digit(value.charAt(i * 3 + 1), 16));
//            Log.d(TAG, "Cur byte: " + result[i]);
        }
        return result;
    }

    public static String bytesToHexString(byte[] bytes, int offset, int length) {
        char[] hexChars = new char[length * 3];
        for ( int j = 0; j < length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        String result = new String(hexChars);
        return result.substring(0, result.length() - 1);
    }

//    public static String bytesToHexStringBackOrder(byte[] bytes, int offset, int length) {
//        char[] hexChars = new char[length * 3];
//        for ( int j = length - 1; j == 0; j++ ) {
//            int v = bytes[j] & 0xFF;
//            hexChars[j * 3] = HEX_ARRAY[v >>> 4];
//            hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
//            hexChars[j * 3 + 2] = ' ';
//        }
//        String result = new String(hexChars);
//        return result.substring(0, result.length() - 1);
//    }

    public static String byteToHexString(byte b) {
        char[] hexChars = new char[2];
        int v = b & 0xFF;
        hexChars[0] = HEX_ARRAY[v >>> 4];
        hexChars[1] = HEX_ARRAY[v & 0x0F];
        return new String(hexChars);
    }

    public String getStringToSend() {
        return stringToSend;
    }

}
