package comm.model;

import android.content.Context;

import java.util.EventListener;

/**
 * Created by Alexander on 24/11/2017.
 */

public interface EventsMainActivity extends EventListener{

    public void onSwitchUserMode(Context activity);
}
