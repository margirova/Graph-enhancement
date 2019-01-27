package graph;

import com.umotional.basestructures.Graph;
import com.umotional.basestructures.Node;
import stats.*;
import trackfit.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GraphHelperFunctions {
    Graph myGraph;
    public GraphHelperFunctions(Graph graph){
        this.myGraph = graph;
    }

    public GraphHelperFunctions(){
    }

    public double gpsDistanceToMeters(double lat1, double lon1, double lat2, double lon2){
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

    public double gpsDistanceToMeters(int lat1E6, int lon1E6, int lat2E6, int lon2E6){
        double lat1 = (double)lat1E6/1000000;
        double lat2 = (double)lat2E6/1000000;
        double lon1 = (double)lon1E6/1000000;
        double lon2 = (double)lon2E6/1000000;
        double res = gpsDistanceToMeters(lat1, lon1, lat2, lon2);
        return res;
    }

    public double gpsDistanceToMeters(Node nd1, Node nd2){
        double lat1 = nd1.getLatitude();
        double lat2 = nd2.getLatitude();
        double lon1 = nd1.getLongitude();
        double lon2 = nd2.getLongitude();
        double res = gpsDistanceToMeters(lat1, lon1, lat2, lon2);
        return res;
    }

    public boolean isTriangle(double a1, double a2, double a3){
        if ((a1+a2) <= a3 ){
            return false;
        }
        if ((a1+a3) <= a2 ){
            return false;
        }
        if ((a3+a2) <= a1 ){
            return false;
        }

        return true;
    }

    public Track interpolate(Track graphTrack, double avDist){

        Track interpolatedTrack = new Track();

//        System.out.println();
        for (int i = 0; i < graphTrack.getPoints().size(); i++){
            if (i == 0){
                interpolatedTrack.addPoint(graphTrack.getPointbyPos(i).getLat(),
                        graphTrack.getPointbyPos(i).getLon(), false);
                continue;
            }
            double dist = graphTrack.getDistanceBetweenPoints(i-1, i);
            TrackPoint p1 = graphTrack.getPointbyPos(i-1);
            TrackPoint p2 = graphTrack.getPointbyPos(i);
            if (dist > avDist){
                BigDecimal p2LatBD = new BigDecimal(p2.getLat());
                BigDecimal p1LatBD = new BigDecimal(p1.getLat());
                BigDecimal dLat = p2LatBD.subtract(p1LatBD);
                BigDecimal p2LonBD = new BigDecimal(p2.getLon());
                BigDecimal p1LonBD = new BigDecimal(p1.getLon());
                BigDecimal dLon = p2LonBD.subtract(p1LonBD);
                int numPoints = (int) (dist / avDist);
                for (int j = 0; j < numPoints; j++){
                    BigDecimal mult = new BigDecimal((double)(j+1)/(numPoints+1));
                    BigDecimal additionLat = dLat.multiply(mult);
                    BigDecimal additionLon = dLon.multiply(mult);
                    BigDecimal latNew = p1LatBD.add(additionLat);
                    BigDecimal lonNew = p1LonBD.add(additionLon);
//                    System.out.println(numPoints+" "+p1.getLat()+" "
//                            +p1.getLon()+ " " + p2.getLat()+" "
//                            +p2.getLon()+" "+latNew+" "+ lonNew+" "+additionLat+" "+additionLon);
                    interpolatedTrack.addPoint(latNew.doubleValue(), lonNew.doubleValue(), true);
                }
            }
            interpolatedTrack.addPoint(p2.getLat(), p2.getLon(), false);

        }
//        System.out.println(graphTrack.getLength() + " " + interpolatedTrack.getLength());
        return interpolatedTrack;
    }

    public DistPoint hausdorff(double lat, double lon, Track track){
        int idx = 0;
        double minimum = Double.MAX_VALUE;
        for (int i = 0; i < track.getLength(); i++){
            double lat2 = track.getPointbyPos(i).getLat();
            double lon2 = track.getPointbyPos(i).getLon();
            double d = gpsDistanceToMeters(lat, lon, lat2, lon2);
            if (d < minimum){
                minimum = d;
                idx = i;
            }
        }

        return new DistPoint(track.getPointbyPos(idx), minimum);
    }

    public List<StatUnitInvDistance> createTrack2PathInterpolatedStat(Track origTrack, Track graphPath){
        List<StatUnitInvDistance> stats = new ArrayList<>();
        for (TrackPoint tp: origTrack.getPoints()){
            StatUnitInvDistance statUnit =
                    createTrack2PathInterpolatedStatUnit(tp.getLat(), tp.getLon(), graphPath);
            stats.add(statUnit);
        }

        return stats;
    }

    public List<StatUnitInterpolatedDistance> createPathInterpolated2TrackStat(Track origTrack, Track interpGraphPath){
        List<StatUnitInterpolatedDistance> stats = new ArrayList<>();
        for (TrackPoint tp: interpGraphPath.getPoints()){
            StatUnitInterpolatedDistance statUnit =
                    createPathInterpolated2TrackStatUnit(tp.getLat(), tp.getLon(), origTrack);
            stats.add(statUnit);
        }
        return stats;
    }

    public StatUnitInvDistance createTrack2PathInterpolatedStatUnit(double lat, double lon, Track graphPath){
        DistPoint dp = hausdorff(lat, lon, graphPath);
        StatUnitInvDistance statUnit = new StatUnitInvDistance(lat, lon, dp.dist, dp.tp);
        return statUnit;
    }

    public StatUnitInterpolatedDistance createPathInterpolated2TrackStatUnit(double lat, double lon, Track origTrack){
        DistPoint dp = hausdorff(lat, lon, origTrack);
        StatUnitInterpolatedDistance statUnit = new StatUnitInterpolatedDistance(lat, lon, dp.dist);
        return statUnit;
    }


    class DistPoint {
        TrackPoint tp;
        double dist;
        public DistPoint(TrackPoint tp, double dist){
            this.dist = dist;
            this.tp = tp;
        }
    }
}
