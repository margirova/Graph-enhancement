package trackfit;

import com.umotional.basestructures.Edge;
import com.umotional.basestructures.Graph;
import com.umotional.basestructures.Node;
import graph.Track;
import graph.TrackPoint;
import net.sf.javaml.core.kdtree.KDTree;
import stats.StatUnitAreaDistance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class GraphProcessor {
    private Graph graph;
    private KDTree kdt;
    private Track inputTrack;
    private Track outputTrack;
    private Node lastnode;
    private HashMap<Node, Segment> nodesInfo;
    private double finaldistance;
    private Node prevnode;
    private List<StatUnitAreaDistance> stats;
    public GraphProcessor(Graph graph, KDTree kdt, Track track){
        this.graph = graph;
        this.kdt = kdt;
        this.inputTrack = track;
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

    public Pair hausdorff(double lat, double lon){
        int idx = 0;
        double minimum = Double.MAX_VALUE;
        List<Double> dsts = new ArrayList<Double>();
        for (int i = 0; i<inputTrack.getLength(); i++){
            double lat2 = inputTrack.getPointbyPos(i).getLat();
            double lon2 = inputTrack.getPointbyPos(i).getLon();
            double d = gpsDistanceToMeters(lat, lon, lat2, lon2);
            dsts.add(d);
            if (d < minimum){
                minimum = d;
                idx = i;
            }
        }
        Pair p = new Pair(idx, minimum, dsts); //!!
        return p;
    }

    public double computeArea(Segment s1, Segment s2){ //approximation
        double area = 0.0;
        int idx1 = s1.getIndexclosest();
        int idx2 = s2.getIndexclosest();
        if (idx1 == idx2){
            //triangle
            double s1s2d = gpsDistanceToMeters(s1.getNode(), s2.getNode());
            double d1 = s1.getDistclosest();
            double d2 = s2.getDistclosest();
            if (!isTriangle(s1s2d, d1, d2)){
                //area = Math.max(d1, d2);
                return area;
            }
            double p = (s1s2d + d1 + d2)/2;
            area = Math.sqrt(p*(p-d1)*(p-d2)*(p-s1s2d));
            //System.out.println("a "+area+" "+p+" "+d1+" "+d2+" "+s1s2d);
        }
        else{
            //multiple triangles
            double s1s2d = gpsDistanceToMeters(s1.getNode(), s2.getNode());
            double d2 = s2.getDists().get(idx1);
            double d1 = s1.getDistclosest();
            double p = 0.0;
            if (isTriangle(s1s2d, d1, d2)){
                p = (s1s2d + d1 + d2)/2;
                area += Math.sqrt(p*(p-d1)*(p-d2)*(p-s1s2d));
            }
            else{
                area += Math.max(d1, d2);
            }
            //triangle rule!

            //System.out.println("b "+area+" "+s1s2d+" "+d2+" "+d1+" "+p);
            ///add 1sr
            for (int i = Math.min(idx1, idx2); i< Math.max(idx1, idx2); i++){
                //add all thers
                //not compute, keep dists
                d1 = s2.getDists().get(i);
                d2 = s2.getDists().get(i+1);
                double d1d2 = inputTrack.getDistanceBetweenPoints(i, i+1);
                if (!isTriangle(d1d2, d1, d2)){
                    //area += Math.max(d1, d2);
                    continue;
                }
                p = (d1d2 + d1 + d2)/2;
                area += Math.sqrt(p*(p-d1)*(p-d2)*(p-d1d2));
                //System.out.println("b "+area+" "+d1d2+" "+d2+" "+d1+" "+p);
            }
            //System.out.println("b "+area+" "+idx1+" "+idx2);
        }
        //System.out.println(area);
        return area;
    }

    public void dijkstra(){
        HashMap<TrackInfo, Double> distTracks = new HashMap<TrackInfo, Double>();
        TrackPoint start = inputTrack.getPointbyPos(0);
        TrackPoint goal = inputTrack.getPointbyPos(inputTrack.getLength()-1);
        double[] key = new double[2];
        key[0] = start.getLat();
        key[1] = start.getLon();
        //System.out.println(kdt.nearest(key, 3).);
        java.lang.Object[] neareststarts = kdt.nearest(key, 3);
        double[] key1 = new double[2];
        key1[0] = goal.getLat();
        key1[1] = goal.getLon();
        Node nearestgoal = (Node) kdt.nearest(key1);
        if (!isValidDistance(goal, nearestgoal)){
            System.out.println("1st");
            outputTrack = null;
            stats = null;
            return;
        }

        Comparator<Segment> comp = new Comparator<Segment>() {
            public int compare(Segment o1, Segment o2) {
                if (o1.getAreasofar() > o2.getAreasofar()) {
                    return 1;
                }
                if (o1.getAreasofar() < o2.getAreasofar()) {
                    return -1;
                }
                return 0;
            }
        };

        for (int i = 0; i < neareststarts.length; i++){
            Node neareststart = (Node) neareststarts[i];

            if (!isValidDistance(start, neareststart)){
                System.out.println("2nd");
                continue;
            }

            this.nodesInfo = new HashMap<Node, Segment>();
            Segment segstart = new Segment(neareststart);
            segstart.setAreasofar(0.0);
            segstart.setIndexclosest(0);
            segstart.setdArea(0.0);
            Pair temp = hausdorff(neareststart.getLatitude(), neareststart.getLongitude());
            segstart.setDists(temp.getDsts());
            temp = null;
            nodesInfo.put(neareststart, segstart);
            double dst = gpsDistanceToMeters(key[0], key[1],neareststart.getLatitude(), neareststart.getLongitude());
            //System.out.println(key[0]+" "+key[1]+" "+neareststart.getLatitude()+" "+neareststart.getLongitude());
            segstart.setDistclosest(dst);
            PriorityQueue<Segment> pq = new PriorityQueue<Segment>(10, comp);
            pq.add(segstart);
            while (!pq.isEmpty()){
                Segment s = pq.poll();
                Node nd = s.getNode();
                if (gpsDistanceToMeters(nd.getLatitude(),nd.getLongitude(),
                        nearestgoal.getLatitude(),  nearestgoal.getLongitude())<10){
                    lastnode = nd;
                    break;
                }
                List<Edge> edges = graph.getOutEdges(nd);
                for (Edge e:edges){
                    Node nd2 = graph.getNode(e.toId);
                    if (nodesInfo.containsKey(nd2)){
                        Segment seg2 = nodesInfo.get(nd2);
                        double area2 = seg2.getAreasofar();
                        double area1 = s.getAreasofar();
                        double smallarea = computeArea(s, seg2);
                        if (pq.contains(seg2)){
                            if ((area1+smallarea)<area2){
                                seg2.setAreasofar(area1+smallarea);
                                seg2.setParent(nd);
                                seg2.setdArea(smallarea);
                            }
                        }
                    }
                    else {
                        Segment seg2 = new Segment(nd2);
                        seg2.setParent(nd);
                        temp = hausdorff(nd2.getLatitude(), nd2.getLongitude());
                        seg2.setIndexclosest(temp.getIdx());
                        seg2.setDistclosest(temp.getDist());
                        seg2.setDists(temp.getDsts());

                        temp = null;
                        double areaold = s.getAreasofar();
                        double newarea = computeArea(s, seg2);
                        seg2.setAreasofar(areaold+newarea);
                        seg2.setdArea(newarea);
                        nodesInfo.put(nd2, seg2);
                        pq.add(seg2);
                    }
                }
            }
            if (lastnode!=null) {
                Double dist = nodesInfo.get(lastnode).getAreasofar();
                TrackInfo midtrack = backtrackMid();
                lastnode = null;
                distTracks.put(midtrack, dist);
                pq = null;
            }
        }
        //System.out.println(distTracks);
        minimizeDist(distTracks);
    }








    public boolean isForward(TrackPoint target1, TrackPoint target2, Node nd1, Node nd2){
        //cross product to obtain lines coordinates (approximation)
        double a1 = target1.getLon()-target2.getLon();
        double a2 = target2.getLat() - target1.getLat();
        double a3 = target1.getLat()*target2.getLon() - target1.getLon()*target2.getLat();

        double b1 = nd1.getLongitude()-nd2.getLongitude();
        double b2 = nd2.getLatitude() - nd1.getLatitude();
        double b3 = nd1.getLatitude()*nd2.getLongitude() - nd1.getLongitude()*nd2.getLatitude();
        double cos = (a1*b1+a2*b2+a3*b3)/(Math.sqrt(a1*a1+a2*a2+a3*a3)*Math.sqrt(b1*b1+b2*b2+b3*b3));
        if (cos > 0) return true;
        //System.out.println("here");
        return false;
    }




    public void storeToOutput(Path pth){
        //store exceptfirstnode
        for (int i = 0; i<pth.size(); i++){
            outputTrack.addPoint(pth.getNodeByIndex(i).getLatitude(), pth.getNodeByIndex(i).getLongitude(), false);
        }
    }




    public void minimizeDist(HashMap<TrackInfo, Double> dt){
        this.finaldistance = Double.MAX_VALUE;
        for (TrackInfo ti:dt.keySet()){
            if (dt.get(ti) < finaldistance){
                finaldistance = dt.get(ti);
                this.outputTrack = ti.getTrack();
                this.stats = ti.getStats();
            }
        }
    }


    public TrackInfo backtrackMid(){
        Track t = new Track();
        List<StatUnitAreaDistance> statsmid = new ArrayList<>();
        if (lastnode != null){
            //System.out.println(lastnode);
            //System.out.println(nodesInfo);
            Node parent = nodesInfo.get(lastnode).getParent();
            Double latestArea = nodesInfo.get(lastnode).getdArea();
            Double closestDist = nodesInfo.get(lastnode).getDistclosest();
            statsmid.add(new StatUnitAreaDistance(lastnode.getLatitude(), lastnode.getLongitude(), latestArea, closestDist)); //here
            t.addPoint(lastnode, false);
            while (parent != null){
                t.addPoint(parent, false);
                latestArea = nodesInfo.get(parent).getdArea();
                closestDist = nodesInfo.get(parent).getDistclosest();
                statsmid.add(new StatUnitAreaDistance(parent.getLatitude(), parent.getLongitude(), latestArea, closestDist));
                parent = nodesInfo.get(parent).getParent();
            }
            //System.out.println(outputTrack);
        }
        else {
            //System.out.println(nodesInfo);
            System.out.println(":(");
        }
        t.reverseTrack();
        return new TrackInfo(t, statsmid);
    }

    public boolean isValidDistance(TrackPoint origin, Node target){
        double distance = gpsDistanceToMeters(origin.getLat(), origin.getLon(), target.getLatitude(), target.getLongitude());
        //System.out.println(distance);
        if (distance > 1000){
            return false;
        }
        return true;
    }


    public Track getInputTrack() {
        return inputTrack;
    }

    public Track getOutputTrack() {
        return outputTrack;
    }

    public List<StatUnitAreaDistance> getStats() {
        Collections.reverse(stats);
        return stats;}

    public void saveOutputTrack(String path) throws IOException {
        if (outputTrack != null){
            FileWriter writer = new FileWriter(path, false);
            String st = "";
            for (TrackPoint tp:outputTrack.getReversedPoints()){
                double lat = (tp.getLat()*1000000);
                double lon = (tp.getLon()*1000000);
                st = Integer.toString((int)lat)+","+Integer.toString((int)lon)+"\n";
                writer.write(st);
            }
            writer.close();
        }

    }
}
