package comm.model;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Alexander on 27/09/2017.
 */
public class WebSocketConection {

    private Socket socketPassenger;
    private Socket socketDriver;

    private OnEventListener listener;

    public WebSocketConection(OnEventListener listener) {
        /*IO.Options options = new IO.Options();
        options.secure = true;
        options.forceNew = true;
         */
        this.listener = listener;

        try {
            socketPassenger = IO.socket("https://javasocketio.herokuapp.com/passenger");

            socketDriver = IO.socket("https://javasocketio.herokuapp.com/driver");
            //socket = IO.socket("http://localhost:8080");
            initListeners();
            //socketPassenger.connect();
            //socketDriver.connect();

        } catch (URISyntaxException e) {
            System.out.println(e.toString());
        }
    }

    public Socket getSocketPassenger() {
        return socketPassenger;
    }

    public Socket getSocketDriver() {
        return socketDriver;
    }

    private void initListeners() {
        if (socketPassenger == null) {
            System.out.println("socket puntero nulo");
        } else {
            socketPassenger.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socketPassenger.emit("new-message", "hi");
                    //socket.disconnect();
                }
            }).on("locations", new Emitter.Listener() {
                    public void call(Object... args) {
                        JSONArray jsonArray = (JSONArray) args[0];
                        String json = jsonArray.toString();

                        System.out.println(json);

                        LocationMap[] locations;
                        locations = JSONManager.jsonToLocationMap(json);
                        listener.onReceiveLocations(locations);

                    }
            }).on("driver-disconnect", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    int id = Integer.parseInt((String)args[0]);
                    listener.onDriverDisconnect(id);
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    System.out.println("Se ha desconectado");}

            });
        }

        if (socketDriver == null) {
            System.out.println("socket puntero nulo");
        } else {
            socketDriver.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                }
            })
            .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    System.out.println("Se ha desconectado");}

            });
        }
    }

    public void sendLocation(JSONObject loc) {
        socketDriver.emit("receive-location", loc);
    }

}
