package merloni.android.washer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.text.ParseException;

import merloni.android.washer.R;
import merloni.android.washer.model.Package;
import merloni.android.washer.model.Platform2;
import merloni.android.washer.model.Program;
import merloni.android.washer.model.Sendable;
import merloni.android.washer.util.BTManager;
import merloni.android.washer.util.NoInternetConnectionException;
import merloni.android.washer.util.ServerAnswerListener;
import merloni.android.washer.util.WasherManager;

/**
 * Created by Ivan Grinichenko on 21.02.2015.
 */
public class ReadActivity extends Activity implements ServerAnswerListener {

    private static final String TAG = ReadActivity.class.getSimpleName();

    public static final int FILE_CHOOSING = 1002;
    private TextView dataView;

    private Program program;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        dataView = (TextView)findViewById(R.id.text);
        program = new Platform2("", "456767654345");
        program.context = this;
        BTManager.getInstance().startClientMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        program.context = null;
        program = null;
        BTManager.getInstance().listener = null;
        BTManager.getInstance().stopBT();
    }

    @Override
    public void onServerError(Sendable caller, String value) {

    }

    @Override
    public void onServerLoaded(Sendable caller, boolean ok) {
        Program program = (Program)caller;
        Log.d(TAG, "Server result: " + program.xmlData);
    }
}
