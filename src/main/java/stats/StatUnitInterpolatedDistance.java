package stats;

import java.io.Serializable;

public class StatUnitInterpolatedDistance implements Serializable {
    private double lat, lon;
    private double interpDist;
    public StatUnitInterpolatedDistance(double lat, double lon, double dist){
        this.lat = lat;
        this.lon = lon;
        this.interpDist = dist;
    }

    public double getDist() {
        return interpDist;
    }


    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "StatUnit{" +
                "lat=" + this.getLat() +
                ", lon=" + this.getLon() +
                ", InDist=" + interpDist +
                '}';
    }
}
