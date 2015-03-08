package merloni.android.washer.util;

/**
 * Created by Ivan Grinichenko on 08.03.2015.
 */
public class WasherManager {

    public static WasherManager instance;

    public static WasherManager getInstance() {
        if (instance == null) {
            instance = new WasherManager();
        }
        return instance;
    }

    public void sendPackage(merloni.android.washer.model.Package pack) {
        BTManager.getInstance().sendPackage(pack);
    }
}
