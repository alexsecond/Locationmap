package comm.locationmap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import comm.controller.ControllerLocation;
import comm.model.EventsMainActivity;
import comm.model.JSONManager;
import comm.model.LocationMap;
import comm.model.User;

import static android.support.v7.app.AlertDialog.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.OnConnectionFailedListener {


    GoogleMap gMap;
    GoogleApiClient googleApiClient;
    MarkerOptions lugarRecogida;
    Marker init;

    TextView lugarMapa;

    private ControllerLocation controllerLocation;
    private User user;

    private Handler handler;

    private HashMap<Integer, Marker> markerMaps = new HashMap<Integer, Marker>();

    private EventsMainActivity myEvents;

    static final int PLACE_PICKER_REQUEST = 1;
    static final int PERMISO_LOCALIZACION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //ubicacion = new Ubicacion(this);

        /*googleApiClient = new GoogleApiClient.Builder(this).
                addApi(Places.GEO_DATA_API).
                addApi(Places.PLACE_DETECTION_API).
                enableAutoManage(this, this).
                build();

        lugarMapa = (TextView) findViewById(R.id.sitio_mapa);

        lugarMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new PlacePicker()
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });*/

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        user = JSONManager.instanceUserFromJson(
                this.getIntent().getExtras().getString("jsonUser")
        );
        controllerLocation = new ControllerLocation(this, user);
        /*handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                Toast.makeText(getApplicationContext(), bundle.getString("mensaje"), Toast.LENGTH_LONG).show();
                gMap.addMarker(new MarkerOptions().position(new LatLng(-17.383561, -66.275178)));
            }
        };*/
        handler = new Handler();

        myEvents = controllerLocation;
    }

    public void updateMarkers(LocationMap[] locations) {
        Toast.makeText(this,"Length: " + locations.length, Toast.LENGTH_LONG).show();
        for(int i = 0; i < locations.length; i++) {
            LocationMap loc = locations[i];
            if (markerMaps.containsKey(loc.getId())) {
                markerMaps.get(loc.getId()).
                        setPosition(new LatLng(loc.getLat(), loc.getLng()));
            } else {
                markerMaps.put(loc.getId(), gMap.addMarker(new MarkerOptions().
                        position(new LatLng(loc.getLat(), loc.getLng()))));
            }
        }
    }

    public void removeMarker(int id) {
        markerMaps.get(id).remove();
        markerMaps.remove(id);
    }

    public void clearMarkers() {
        markerMaps.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setOnMyLocationButtonClickListener(this);

        enableMyLocation();

        double lat, lng;
        Location loc = controllerLocation.getLocation();
        if(loc != null) {
            lat = loc.getLatitude();
            lng = loc.getLongitude();

            LatLng latLng = new LatLng(lat, lng);
            CameraPosition position = CameraPosition.builder().target(latLng).
                    zoom(16).build();
            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000, null);
        }


        //Toast.makeText(this, lat + " " + lng, Toast.LENGTH_LONG);


        //LatLng latLng = new LatLng(0.0, 0.0);

        //init = gMap.addMarker(new MarkerOptions().position(latLng));




        /*new Thread(new Runnable() {
            @Override
            public void run() {
                //init.setPosition(new LatLng(-17.384118, -66.273984));
                double lat = -17.384118;
                double lng = -66.273984;
                for (int i = 0; i < 10; i++) {
                    lat += 0.001;
                    lng += 0.001;
                    //init.setPosition(new LatLng(-17.384118, -66.273984));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/


    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISO_LOCALIZACION);
        } else if (gMap != null) {
            // Access to the location has been granted to the app.
            gMap.setMyLocationEnabled(true);
        }
    }

    public GoogleMap getGmap() {
        return gMap;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Se hizo click en boton mi localizacion", Toast.LENGTH_SHORT);

        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displayPlace(PlacePicker.getPlace(data, this));
        }
    }

    private void displayPlace(Place place) {
        CameraPosition position = CameraPosition.builder().target(place.getLatLng()).
                zoom(16).build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000, null);
        lugarMapa.setText(place.getName());
        LatLng latLng = place.getLatLng();
        lugarRecogida = new MarkerOptions().position(latLng).title("Lugar de recogida");

        gMap.addMarker(lugarRecogida);

        Toast.makeText(this, place.getName(), Toast.LENGTH_SHORT).show();
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISO_LOCALIZACION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void showToast() {
        Toast.makeText(this, "Length: ", Toast.LENGTH_LONG).show();
    }

    public void putMarkers() {
        gMap.addMarker(new MarkerOptions().position(new LatLng(0.0, 0.0)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            showDialogSwitchUser();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDialogSwitchUser() {
        String mode = "";
        if(user.getCurrentMode() == User.MODE_DRIVER) {
            mode = "Pasajero";
        }
        else {
            mode = "Conductor";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Cambiar a: " + mode);
        builder.setTitle("switch user");
        builder.setPositiveButton("Cambiar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        user.switchModeUser();
                        if(user.getCurrentMode() == User.MODE_DRIVER) {
                            gMap.clear();
                            markerMaps.clear();
                            Toast.makeText(getApplicationContext(),
                                    "Intentando borrar los marcadores", Toast.LENGTH_SHORT);
                            myEvents.onSwitchUserMode(getApplicationContext());
                        }
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.show();
    }

    public Handler getHandler() {
        return handler;
    }
}
