package merloni.android.washer.model;

import android.content.Context;

import java.text.ParseException;

import merloni.android.washer.util.NoInternetConnectionException;

/**
 * Created by Ivan Grinichenko on 15.01.2015.
 */
public interface Sendable {

    public void prepareToSend();
    public void send(Context context) throws NoInternetConnectionException, ParseException;
    public void parse();
    public void onParsed();
    public void onReceive();
    public void initParsed();

}
