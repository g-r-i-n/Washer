package merloni.android.washer.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import merloni.android.washer.R;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class MainActivity extends Activity {

//    private static final UUID MY_UUID = UUID.randomUUID();
//    private static final UUID MY_UUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String BT_NAME = "WasherApp";

    public static final int CODE = 1001;
    private static final int MESSAGE_READ = 1;
    private static final String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private AcceptThread acceptThread;
    private ConnectThread connectThread;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        ((Button)findViewById(R.id.connect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, SearchActivity.class), CODE);
            }
        });
        ((Button)findViewById(R.id.write)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptThread = new AcceptThread();
                acceptThread.start();
            }
        });
        ((Button)findViewById(R.id.read)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectThread = new ConnectThread(bluetoothDevice);
                connectThread.start();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ((Button)findViewById(R.id.connect)).setText(data.getStringExtra("mac"));
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(data.getStringExtra("mac"));
            ((Button)findViewById(R.id.send)).setText(bluetoothDevice.getName());
        }
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Button) findViewById(R.id.send)).setText("Accepting1");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        public void run() {
            // Keep listening until exception occurs or a socket is returned
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = serverSocket.accept();
                    Log.d(TAG, "Accepted1");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((Button) findViewById(R.id.send)).setText("Accepted1");
                        }
                    });
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    Log.d(TAG, "Accepted2");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((Button) findViewById(R.id.send)).setText("Accepted2");
                        }
                    });
                    // Do work to manage the connection (in a separate thread)
//                    manageConnectedSocket(socket);
                    ConnectedThread connectedThread = new ConnectedThread(socket);
                    Log.d(TAG, "Accepted3");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((Button) findViewById(R.id.send)).setText("Accepted3");
                        }
                    });
                    connectedThread.write("abcde".getBytes());
                    Log.d(TAG, "Accepted4");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((Button) findViewById(R.id.send)).setText("Accepted4");
                        }
                    });
                    try {
                        serverSocket.close();
                        Log.d(TAG, "Accepted5");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((Button) findViewById(R.id.send)).setText("Accepted5");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
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

        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
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
//                    ((Button) findViewById(R.id.send)).setText(phoneUuids[0].toString());
                    // MY_UUID is the app's UUID string, also used by the server code-
                    Log.d(TAG, "Connecting1");
                    tmp = device.createRfcommSocketToServiceRecord(phoneUuids[0].getUuid());
//                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                    Log.d(TAG, "Connecting2");
                } else {
                    ((Button) findViewById(R.id.send)).setText("Unknown UUID");
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
        ConnectedThread connectedThread = new ConnectedThread(socket);
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
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            final byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    Log.d(TAG, "Managed4");
                    bytes = mmInStream.read(buffer);
                    Log.d(TAG, "Managed5. " + bytes + " bytes.");
                    Log.d(TAG, new String(buffer));
                    // Send the obtained bytes to the UI activity
///                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Managed6");
                            ((Button)findViewById(R.id.send)).setText(new String(buffer));
                            Log.d(TAG, "Managed7");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                Log.d(TAG, "Write1");
                mmOutStream.write(bytes);
                Log.d(TAG, "Write2");
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
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

}
