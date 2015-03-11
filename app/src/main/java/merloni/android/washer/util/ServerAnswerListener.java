package merloni.android.washer.util;

import merloni.android.washer.model.Sendable;

/**
 * Created by Ivan Grinichenko on 15.01.2015.
 */
public interface ServerAnswerListener {

    //TODO: rename the message
    /*
     * 	If error occured while server exchange
     */
    public void onError(Sendable caller, String value);

    /*
     * If server returned ok, but answer format is wrong or other mistakes occured then @param ok is false
     */
    public void onLoaded(Sendable caller, boolean ok);

}
