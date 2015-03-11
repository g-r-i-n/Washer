package merloni.android.washer.model;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Ivan Grinichenko on 15.01.2015.
 */
public class Program extends BaseObject {

    private static final String TAG = Program.class.getSimpleName();

//    public boolean ;

    public Program() {
        pageAddress = "/search.ashx";
    }

    public void prepareToSend() {
    }

}