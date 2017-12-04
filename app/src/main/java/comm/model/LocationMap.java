package comm.model;

/**
 * Created by Alexander on 27/09/2017.
 */

public class LocationMap {
    private int id;
    private double lat;
    private double lng;

    public LocationMap(int id, double lat, double lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
