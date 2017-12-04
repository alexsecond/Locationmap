package comm.model;

import java.util.EventListener;

/**
 * Created by Alexander on 02/10/2017.
 */

public interface OnEventListener extends EventListener {
    public void onReceiveLocations(LocationMap [] locations);

    public void onReceiveMessages(Object message);

    public void onDriverDisconnect(int id);

}

