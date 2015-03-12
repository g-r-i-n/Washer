package merloni.android.washer.util;

import merloni.android.washer.model.Sendable;

/**
 * Created by Ivan Grinichenko on 15.01.2015.
 */
public interface ServerAnswerListener {

    /*
     * 	If error occured while server exchange
     */
    public void onServerError(Sendable caller, String message);

    /*
     * If server returned ok, but answer format is wrong or other mistakes occured then @param ok is false
     */
    public void onServerLoaded(Sendable caller, boolean ok);

}
