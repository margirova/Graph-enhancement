package graph;

import com.umotional.basestructures.*;
import parser.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NodesEdgesConverter {
    private List<Edge> graphedges;
    private List<Node> graphnodes;
    public NodesEdgesConverter(){
        this.graphedges = new ArrayList<Edge>();
        this.graphnodes = new ArrayList<Node>();
    }

    //radians??????
    public void convert(HashMap<Long, MapNode> mapnodes, HashMap<Long, MapWay> mapways){
        int ctr = 0;
        HashMap<Long, Integer> sourceToId = new HashMap<Long, Integer>();
        System.out.println(mapnodes.size());
        for (MapNode mn: mapnodes.values()){
            if (!mn.isReferenced()){
                //System.out.println("alarm");
                continue;
            }
            long sourceId = mn.getId();
            double lat = mn.getLat();
            double lon = mn.getLon();
            double latrad = lat*Math.PI/180;
            //double lonrad = lon*Math.PI/180;
            int id = ctr;
            sourceToId.put(sourceId, id);
            int elev = 0; //??????????
            int projLat, projLon;
            projLat = (int)(111132.92-559.82*Math.cos(2*latrad)+1.175*Math.cos(4*latrad)-0.0023*Math.cos(6*latrad))*1000;
            projLon = (int) (111412.84*Math.cos(latrad)-93.5*Math.cos(3*latrad)+0.118*Math.cos(5*latrad))*1000;
            Node nd = new Node(id, sourceId, lat, lon,projLat, projLon, elev);
            graphnodes.add(nd);
            ctr++;
        }

        for (MapWay mw: mapways.values()){
            for (int i = 0; i< mw.getNodesreferences().size()-1; i++){
                long fromSId = mw.getNodesreferences().get(i);
                long toSId = mw.getNodesreferences().get(i+1); //????? maybe not exist
                int fromId = 0;
                int toId = 0;
                if (!mapnodes.containsKey(fromSId) || (!mapnodes.containsKey(toSId))){
                    continue;
                }
                else {
                    fromId = sourceToId.get(fromSId);
                    toId = sourceToId.get(toSId);
                }


                //here comes something awful
                double lat1 = mapnodes.get(fromSId).getLat();
                double lat2 = mapnodes.get(toSId).getLat();
                double lon1 = mapnodes.get(fromSId).getLon();
                double lon2 = mapnodes.get(toSId).getLon();
                if ((lat1!=0.0)&&(lat2!=0.0)){
                    int R = 6371000;
                    double df = (lat2-lat1)*Math.PI/180;
                    double dl = (lon2-lon1)*Math.PI/180;
                    double a = Math.sin(df/2)*Math.sin(df/2)+
                            Math.cos(lat1*Math.PI/180)*Math.cos(lat2*Math.PI/180)*Math.sin(dl/2)*Math.sin(dl/2);
                    double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                    int dist = (int) (R*c);
                    Edge e = new Edge(fromId, toId, dist);
                    graphedges.add(e);
                    if (!mw.isOneway()){
                        Edge e1 = new Edge(toId, fromId, dist);
                        graphedges.add(e1);
                    }
                }
            }
        }
    }

    public List<Edge> getGraphedges() {
        return graphedges;
    }

    public List<Node> getGraphnodes() {
        return graphnodes;
    }

}
