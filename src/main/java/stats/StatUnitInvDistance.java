package stats;


import graph.TrackPoint;

import java.io.Serializable;

public class StatUnitInvDistance implements Serializable {
    private double lat, lon;
    private double invDist;
    private TrackPoint trackPoint;



    public StatUnitInvDistance(double lat, double lon,
                               double dist, TrackPoint tp){
        this.lat = lat;
        this.lon = lon;
        this.invDist = dist;
        this.trackPoint = tp;

    }

    public TrackPoint getTrackPoint() {
        return trackPoint;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public double getDist() {
        return invDist;
    }


    @Override
    public String toString() {
        return "StatUnit{" +
                "lat=" + this.getLat() +
                ", lon=" + this.getLon() +
                ", InvInDist=" + invDist +
                '}';
    }
}
