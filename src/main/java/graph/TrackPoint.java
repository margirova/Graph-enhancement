package graph;

import java.io.Serializable;

public class TrackPoint implements Serializable {
    private double lat, lon;
    private boolean interpolated;
    public TrackPoint(int lat, int lon, boolean interpolated){
        this.lat = (double) lat/1000000;
        this.lon = (double) lon/1000000;
        this.interpolated = interpolated;
    }

    public TrackPoint(double lat, double lon, boolean interpolated){
        this.lat = lat;
        this.lon = lon;
        this.interpolated = interpolated;
    }


    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public boolean isInterpolated() {
        return interpolated;
    }
}
