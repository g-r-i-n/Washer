package merloni.android.washer.util;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import merloni.android.washer.model.*;
import merloni.android.washer.model.Package;

/**
 * Created by Ivan Grinichenko on 25.02.2015.
 */
public class BTManager implements AbstractManager {

    private static final String TAG = BTManager.class.getSimpleName();

    //    private static final UUID MY_UUID = UUID.randomUUID();
//    private static final UUID MY_UUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String BT_NAME = "WasherApp";

    private static final int MESSAGE_READ = 1;

    private static BTManager instance;
    private merloni.android.washer.model.Package curPackage;

    public ArrayAdapter arrayAdapter;
    public BluetoothExchangeListener listener;

    public BluetoothAdapter bluetoothAdapter;
    public ArrayList<BluetoothDevice> bluetoothDevices;
    public BluetoothDevice currentBluetoothDevice;

    private AcceptThread acceptThread;
    //    private ConnectThread connectThreadRead;
//    private ConnectThread connectThreadWrite;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;


    private BTManager() {
        bluetoothDevices = new ArrayList<BluetoothDevice>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BTManager getInstance() {
        if (instance == null) {
            instance = new BTManager();
        }
        return instance;
    }

    private void sendToCurrentDevice(byte[] values) {
        sendToDevice(currentBluetoothDevice, values);
    }

    private void sendToDevice(BluetoothDevice device, byte[] values) {
        if (connectedThread != null) {
            connectedThread.write(values);
        } else {
            listener.onDeviceDisconnected();
        }
    }

    public void startClientMode() {
        connectThread = new ConnectThread(currentBluetoothDevice);
        connectThread.start();
    }

    public void startServerMode() {
        acceptThread = new AcceptThread();
        acceptThread.start();
    }

    public void setCurrentDevice(String mac) {
        for (BluetoothDevice device : bluetoothDevices) {
            if (device.getAddress().equals(mac)) {
                currentBluetoothDevice = device;
                break;
            }
        }
    }

    public interface BluetoothExchangeListener {

        public void onSearchFinished();
        public void onSearchError(String text);
        public void oDataSent();
//        public void onReceiveData(byte[] values, int bytes);
        public void onReceiveData(Package pack);
        public void onDataSendingError(String text);
        public void onGeneralError(String text);
        void onDeviceDisconnected();
        void onDeviceConnected();
    }






    public boolean loadBondedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                BTManager.getInstance().bluetoothDevices.add(device);
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        return arrayAdapter.getCount() > 0;
    }

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(BT_NAME, MY_UUID);
                Log.d(TAG, "Accepting1");
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        public void run() {
            // Keep listening until exception occurs or a socket is returned
            BluetoothSocket socket = null;
            boolean ok = true;
            while (ok) {
                try {
                    Log.d(TAG, "Accepted0");
                    socket = serverSocket.accept();
                    Log.d(TAG, "Accepted1");
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    Log.d(TAG, "Accepted2");
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
//                    connectedThread = new ConnectedThread(socket);
                    Log.d(TAG, "Accepted3");
///                    sendToCurrentDevice("abcde".getBytes());
                    ok = false;

//                    final StringBuffer sb = new StringBuffer();
//                    try {
//                        InputStream is = socket.getInputStream();
//                        byte[] buffer = new byte[1024];
//                        int bufferLength = 0;
//                        while ( (bufferLength = is.read(buffer)) > 0 ) {
//                            sb.append(new String(buffer));
////    	                downloadedSize += bufferLength;
////    	                updateProgress(downloadedSize, totalSize);
//
//                        }
//                        is.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

//                    Log.d(TAG, "Accepted4");
//                    try {
//                        serverSocket.close();
//                        Log.d(TAG, "Accepted5");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) { }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private class ConnectThread extends Thread {

        private BluetoothSocket socket;
        private BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            init(device);
        }

        public void init(BluetoothDevice device) {
            // Use a temporary object that is later assigned to socket,
            // because socket is final
            BluetoothSocket tmp = null;
            this.device = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
//                Method method = device.getClass().getMethod("getUuids", null);
//                ParcelUuid[] phoneUuids = (ParcelUuid[]) method.invoke(device, null);
                ParcelUuid[] phoneUuids = device.getUuids();
                if (phoneUuids.length > 0) {
                    Log.d(TAG, "Connecting1");
                    tmp = device.createRfcommSocketToServiceRecord(phoneUuids[0].getUuid());
                    Log.d(TAG, "Connecting2");
                } else {
                    listener.onGeneralError("Unknown UUID");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Connecting3");
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                listener.onDeviceConnected();
                socket.connect();
                Log.d(TAG, "Connected1");
            } catch (IOException connectException) {
                connectException.printStackTrace();
                // Unable to connect; close the socket and get out
                try {
                    socket.close();
                    Log.d(TAG, "Connected2");
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(socket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        Log.d(TAG, "Managed1");
        connectedThread = new ConnectedThread(socket);
        Log.d(TAG, "Managed2");
        connectedThread.start();
        Log.d(TAG, "Managed3");
    }

    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            final byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
//                    if (path == null) {
                        // Read from the InputStream
                        Log.d(TAG, "Managed4");
                        bytes = mmInStream.read(buffer);
                        Log.d(TAG, "Managed5. " + bytes + " bytes.");
                        Log.d(TAG, new String(buffer, 0, bytes));
                        listener.onReceiveData(curPackage);
                        // Send the obtained bytes to the UI activity
                        ///                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
//                    } else {
//                        File file = new File(path);
//                        FileInputStream fis = new FileInputStream(file);
//                        int bufferLength = 0;
//                        while ( (bufferLength = fis.read(buffer)) > 0 ) {
//                            mmOutStream.write(buffer, 0, bufferLength);
//                        }
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    stopBT();
                    break;
                }
//                try {
//                    mmOutStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                Log.d(TAG, "Write1");
                mmOutStream.write(bytes);
                Log.d(TAG, "Write2");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    void resetConnection() {
//        if(inputStream != null) {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if(outputStream != null) {
//            try {
//                outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if(startConnectionThread != null) {
//            startConnectionThread.cancel();
//        }
//        if(acceptConnectionThread != null) {
//            acceptConnectionThread.cancel();
//        }
//        if(manageConnectedDevicesThread != null) {
//            manageConnectedDevicesThread.close();
//        }
//    }

    public boolean isBTSupported() {
        return bluetoothAdapter != null;
    }

    public void startDiscovery() {
        bluetoothAdapter.startDiscovery();
    }

    public void cancelDiscovery() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public boolean isDiscovering() {
        return bluetoothAdapter.isDiscovering();
    }

    /**
     * Stop all threads
     */
    public synchronized void stopBT() {
        Log.d(TAG, "stop");
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
    }

    public void sendPackage(merloni.android.washer.model.Package pack) {
        curPackage = pack;
        sendToCurrentDevice(curPackage.bytesToSend);
    }

}
