package graph;

import com.umotional.basestructures.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Track implements Serializable {
    private List<TrackPoint> points;
    private List<Double> distancesBetween;
    private List<Double> distancesToFirst;

    public List<Double> getDistancesBetween() {
        return distancesBetween;
    }

    public List<Double> getDistancesToFirst() {
        return distancesToFirst;
    }

    //private double trackFullDist;
    public Track(){
        this.points = new ArrayList<TrackPoint>();
        this.distancesBetween = new ArrayList<Double>();
        this.distancesToFirst = new ArrayList<Double>();
    }

    private double gpsDistanceToMeters(double lat1, double lon1, double lat2, double lon2){
        //System.out.println(lat1+" "+lon1+" "+lat2+" "+lon2);
        double rlat1 = lat1/180*Math.PI;
        double rlat2 = lat2/180*Math.PI;
        double rlon1 = lon1/180*Math.PI;
        double rlon2 = lon2/180*Math.PI;
        double R = 6371000.0;
        double dlat = rlat2 - rlat1;
        double dlon = rlon2 - rlon1;
        double a = Math.sin(dlat/2.0) * Math.sin(dlat/2.0) +
                Math.sin(dlon/2.0) * Math.sin(dlon/2.0) * Math.cos(rlat1) * Math.cos(rlat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double res = R*c;
        return res;
    }

    private void addPointRaw(double lat, double lon, boolean interp){
        if (points.isEmpty()){
            distancesBetween.add(0.0);
            distancesToFirst.add(0.0);
        }
        else {
            double res = gpsDistanceToMeters(lat, lon, points.get(points.size()-1).getLat(),
                    points.get(points.size()-1).getLon());

            distancesBetween.add(res);
            double prev = distancesToFirst.get(distancesToFirst.size()-1);
            distancesToFirst.add(res+prev);
        }
        TrackPoint p = new TrackPoint(lat, lon, interp);
        points.add(p);
    }

    public void addPoint(double lat, double lon, boolean interpFlag){
        addPointRaw(lat, lon, interpFlag);
    }

    public void addPoint(int lat, int lon, boolean interpFlag){
        addPointRaw((double)lat/1000000, (double)lon/1000000, interpFlag);

    }


    public void addPoint(Node node, boolean interpFlag){
        addPointRaw(node.getLatitude(), node.getLongitude(), interpFlag);
    }

    public List<TrackPoint> getPoints(){
        return this.points;
    }

    public List<TrackPoint> getReversedPoints(){
        List<TrackPoint> l = new ArrayList<>(this.points);
        Collections.reverse(l);
        return l;
    }

    public void reverseTrack(){
        Collections.reverse(this.points);
    }

    public TrackPoint getPointbyPos(int i){
        return this.points.get(i);
    }

    public int getLength(){
        return points.size();
    }

    public double getFullDist() {
        return distancesToFirst.get(distancesToFirst.size()-1);
    }

    public double getDistanceBetweenPoints(int idx1, int idx2){
        double d1 = distancesBetween.get(idx1);
        double d2 = distancesBetween.get(idx2);
        double res = Math.abs(d1-d2);
        return res;
    }

    @Override
    public String toString() {
        String st = "";
        for (TrackPoint tp:points){
            st+=tp.getLat()+" "+tp.getLon()+"\n";
        }
        return st;
    }
}
