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

    private Socket socket;

    private OnEventListener listener;

    public WebSocketConection(OnEventListener listener) {
        /*IO.Options options = new IO.Options();
        options.secure = true;
        options.forceNew = true;
         */
        this.listener = listener;

        try {
            socket = IO.socket("https://javasocketio.herokuapp.com/");
            //socket = IO.socket("http://localhost:8080");
            initListeners();
            socket.connect();

        } catch (URISyntaxException e) {
            System.out.println(e.toString());
        }
    }

    private void initListeners() {
        if (socket == null) {
            System.out.println("socket puntero nulo");
        } else {
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.emit("new-message", "hi");
                    //socket.disconnect();
                }
            }).
                    on("receive_locations", new Emitter.Listener() {
                        public void call(Object... args) {
                            JSONArray jsonArray = (JSONArray) args[0];
                            String json = jsonArray.toString();

                            System.out.println(json);

                            LocationMap[] locations;
                            locations = JSONManager.jsonToLocationMap(json);
                            listener.onReceiveLocations(locations);

                        }
                    })
                    .on("messages", new Emitter.Listener() {
                        public void call(Object... args) {
                            listener.onReceiveMessages(args[0]);
                        }
                    })
                    .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            System.out.println("Se ha desconectado");
                        }

                    });
        }

    }

    public void sendLocation(JSONObject loc) {
        System.out.println("Hola");
        socket.emit("location", loc);
    }

    public void connectUserToServer(JSONObject loc) {

        socket.emit("first_connection", loc);
    }

    public void sendConfirmation() {
        socket.emit("confirmation", "Android");
    }

    public void requestLocations() {
        socket.emit("location", "");
    }

    public boolean isConnected() {
        return socket.connected();
    }

    /*public void addEventListener(OnEventListener l) {
        this.listener = l;
    }*/
}
