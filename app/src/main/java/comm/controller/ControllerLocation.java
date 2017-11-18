package comm.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import comm.locationmap.MainActivity;
import comm.model.JSONManager;
import comm.model.LocationMap;
import comm.model.OnEventListener;
import comm.model.WebSocketConection;

/**
 * Created by Alexander on 05/10/2017.
 */

public class ControllerLocation implements LocationListener, OnEventListener {



    private LocationManager locationManager;
    private Context mainActivity;
    private String provider;

    Location lastKnownLocation = null;

    private WebSocketConection socketConection;

    private Thread thread;

    public static final int PERMISO_LOCALIZACION = 1;
    private static final int REQUEST_UPDATES = 2;

    public ControllerLocation(Context mainActivity) {
        this.mainActivity = mainActivity;
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        provider = LocationManager.GPS_PROVIDER;

        enableLocationUpdates();
        socketConection = new WebSocketConection(this);
        initVariables();
    }

    private void initVariables() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                socketConection.requestLocations();
            }
        });
    }

    private void enableLocationUpdates() {
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions((Activity) mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_UPDATES);
        } else {
            locationManager.requestLocationUpdates(provider, 5000, 5, this);
        }
    }

    public Location getLocation() {
        Location location = null;
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions((Activity) mainActivity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISO_LOCALIZACION);
        } else {
            location = locationManager.getLastKnownLocation(provider);
        }
        return location;
    }
    /*public void connectToServer() {
        Location loc = getLocation();
        double lat = loc.getLatitude();
        double lng = loc.getLongitude();
        socketConection.connectUserToServer(new LocationMap((int) (Math.random() * 100)
                , lat, lng));
    }*/

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISO_LOCALIZACION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //lastKnownLocation = getLocation();
                }
            break;
            case REQUEST_UPDATES:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableLocationUpdates();
                }
                break;
        }
    }

    public void sendLocation(LocationMap loc) {
        JSONObject jsonObject = JSONManager.locationMapToJSONObject(loc);
        //socketConection.sendLocation(jsonObject);
    }

    @Override
    public void onReceiveLocations(final LocationMap[] locations) {
        MainActivity m = (MainActivity)mainActivity;
        Handler handler = m.getHandler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                MainActivity m = (MainActivity)mainActivity;
                m.updateMarkers(locations);
                //Toast.makeText(mainActivity, "Length: " + locations.length, Toast.LENGTH_LONG).show();
                //m.showToast();
            }
        });
        /*Message msg = new Message ();
        Bundle data = new Bundle();
        data.putString("mensaje", "Hola desde un mensaje en handler");

        msg.setData(data);

        handler.sendMessage(msg);*/
    }

    @Override
    public void onReceiveMessages(Object message) {

    }

    private void setMyLocation() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean startLocationRequest() {
        if(socketConection.isConnected()) {
            thread.start();
        }
        return false;
    }
}
