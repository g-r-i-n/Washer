package merloni.android.washer.model;

import android.content.Context;
import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.Reader;
import java.text.ParseException;

import merloni.android.washer.util.NetManager;
import merloni.android.washer.util.NoInternetConnectionException;
import merloni.android.washer.util.ServerAnswerListener;

/**
 * Created by Ivan Grinichenko on 14.01.2015.
 */

public class BaseObject implements Sendable {

    private static final String TAG = BaseObject.class.getSimpleName();

    public String serverAddress = NetManager.SERVER_ADDRESS;
    public String pageAddress;
    protected String parameters;
    public String postData;
    public boolean sendPost;

    protected Reader reader;
//    protected Serializer serializer;

    protected ServerAnswerListener listener;

    public String xmlData;

    @Override
    public void prepareToSend() {
    }

    @Override
    public void send(final Context context) throws NoInternetConnectionException, ParseException {
        prepareToSend();
        if (NetManager.isOnline(context, false)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String fullAddress = serverAddress + pageAddress + parameters;
                    try {
                        Log.i(TAG, "Start server request");
                        Log.i(TAG, "URL: " + fullAddress);
                        if (sendPost) {
                            xmlData = NetManager.sendPost(context, fullAddress, postData, HTTP.UTF_8);
                        } else {
                            xmlData = NetManager.sendGet(context, fullAddress, HTTP.UTF_8);
                        }
                        Log.i(TAG, "Finish server request");
                        xmlData = prepareXml(xmlData);
                        parse();
                        onReceive();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Full server address: " + fullAddress);
                        if (xmlData != null) {
                            Log.e(TAG, "XML: " + xmlData);
                        }
                        listener.onServerError(BaseObject.this, e.getMessage());
                    }
                }
            }).start();
        } else {
            throw new NoInternetConnectionException(NetManager.noConnectionString);
        }
    }

    //TODO: replace String with StringBuffer
    protected String prepareXml(String xml) {
        return xml;
    }

    @Override
    public void parse() {
//        Log.i(TAG, "Start parsing");
//        try {
//            serializer = new Persister();
//            reader = new StringReader(xmlData);
//            serializer.read(this, reader);
//            initParsed();
//            onParsed();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d(TAG, "Parse error");
//            if (xmlData != null) {
//                Log.d(TAG, "XML: " + xmlData);
//            }
//        }
    }

    @Override
    public void onParsed() {
        Log.i(TAG, "Finish parsing");
    }

    //TODO: another logic of error should be realized
    @Override
    public void onReceive() {
        try {
            listener.onServerLoaded(this, true);
        } catch (Exception e) {
            e.printStackTrace();
            if (xmlData != null) {
                Log.d(TAG, "XML: " + xmlData);
            }
            listener.onServerLoaded(this, false);
        }
    }

    @Override
    public void initParsed() {
    }

    public void setServerAnswerListener(ServerAnswerListener listener) {
        this.listener = listener;
    }

}
