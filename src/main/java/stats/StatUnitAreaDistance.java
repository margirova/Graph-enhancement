package stats;

import java.io.Serializable;
import java.util.Objects;

public class StatUnitAreaDistance implements Serializable {
    private double area, dist;
    private double lat, lon;
    public StatUnitAreaDistance(double lat, double lon, double dArea, double dist){
        this.lat = lat;
        this.lon = lon;
        this.area = dArea;
        this.dist = dist;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public double getArea(){
        return area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatUnitAreaDistance that = (StatUnitAreaDistance) o;
        return Double.compare(that.area, area) == 0 &&
                Double.compare(that.dist, dist) == 0 &&
                Double.compare(that.lat, lat) == 0 &&
                Double.compare(that.lon, lon) == 0;
    }

    @Override
    public int hashCode() {

        return Objects.hash(area, dist, lat, lon);
    }

    public double getDist() {
        return dist;
    }


    @Override
    public String toString() {
        return "StatUnit{" +
                "lat=" + this.getLat() +
                ", lon=" + this.getLon() +
                ", dArea=" + area +
                ", dist=" + dist +
                '}';
    }
}
