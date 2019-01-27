package visualization;

import com.umotional.basestructures.*;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
//import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import graph.Track;
import graph.TrackPoint;
import net.sf.javaml.core.kdtree.KDTree;
import parser.MapNode;
import parser.MapWay;
import processing.core.PApplet;
//import de.fhpotsdam.unfolding.data.Feature;
//import de.fhpotsdam.unfolding.data.MarkerFactory;
import de.fhpotsdam.unfolding.geo.Location;
//import de.fhpotsdam.unfolding.marker.Marker;
//import de.fhpotsdam.unfolding.utils.MapUtils;
//import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PGraphics;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;



//TODO: sign start and end of the track on the map
public class Visualization extends PApplet {
    UnfoldingMap map;
    List<Location> locations;
    double maxLat, maxLon, minLat, minLon;

    public void setup() {
        size(800, 600);
        map = new UnfoldingMap(this, new OpenStreetMap.OpenStreetMapProvider());
        locations = new ArrayList<Location>();
        if ((args.length>=1) && (args[0].equalsIgnoreCase("track"))){ //track given
            trackMode();
        }
        else if ((args.length>=1) && (args[0].equalsIgnoreCase("fit"))){// also given graph trace
            fitMode();
        }
        else if ((args.length>=1) && (args[0].equalsIgnoreCase("graph"))){// just graph
            graphMode();
        }
        else if ((args.length>=1) && (args[0].equalsIgnoreCase("graphtrack"))){// graph+track
            graphtrackMode();
        }
        else if ((args.length>=1) && (args[0].equalsIgnoreCase("all"))){// graph+track+trace
            allMode();
        }

        else if ((args.length>=1) && (args[0].equalsIgnoreCase("raw"))){// graph+track+trace
            rawMode();
        }
        else if ((args.length>=1) && (args[0].equalsIgnoreCase("fitinterpolate"))){// track+path+interp_path
            fitInterpolateMode();
        }
        else if ((args.length>=1) && (args[0].equalsIgnoreCase("fitclassified"))){// classifued
            fitClassifiedMode();
        }


    }

    public void draw() {
        map.draw();
    }

    public void keyPressed(){
        if (key == '+') {
            map.zoomLevelIn();
        }
        if (key == '-') {
            map.zoomLevelOut();
        }

        if (key == 's') {
            map.panRight();
        }
        if (key == 'a') {
            map.panLeft();
        }

        if (key == 'w') {
            map.panUp();
        }
        if (key == 'z') {
            map.panDown();
        }
        if (key == 'm'){
            PGraphics pg = map.mapDisplay.getOuterPG();
            pg.save(args[1]);
            image(pg, width/2, 0);
        }


    }

    public void parseTrack(String path, int[] color){
        File file = new File(path);
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st = br.readLine();
            String[] splited = st.split(",");

            double lat = (double)Integer.parseInt(splited[0])/1000000;
            double lon = (double)Integer.parseInt(splited[1])/1000000;
            if (maxLat == 0.0){
                maxLat = lat;
                maxLon = lon;
                minLat = lat;
                minLon = lon;
            }

            //System.out.println("raw "+lat+" "+lon);
            Location startLocation = new Location(lat, lon);
            locations.add(startLocation);
            SimplePointMarker startMarker = new SimplePointMarker(startLocation);
            startMarker.setColor(color(color[0], color[1], color[2], color[3]));
            map.addMarkers(startMarker);
            while ((st = br.readLine()) != null) {
                splited = st.split(",");
                lat = (double)Integer.parseInt(splited[0])/1000000;
                lon = (double)Integer.parseInt(splited[1])/1000000;
                if (lat>maxLat){
                    maxLat = lat;
                }

                if (lat<minLat){
                    minLat = lat;
                }

                if (lon>maxLon){
                    maxLon = lon;
                }

                if (lon<minLon){
                    minLon = lon;
                }

                Location endLocation = new Location(lat, lon);
                locations.add(endLocation);
                SimplePointMarker endMarker = new SimplePointMarker(endLocation);
                endMarker.setColor(color(color[0], color[1], color[2], color[3]));
                SimpleLinesMarker connectionMarker = new SimpleLinesMarker(startLocation, endLocation);
                connectionMarker.setColor(color(color[0], color[1], color[2], color[3]));
                connectionMarker.setStrokeWeight(4);
                map.addMarkers(endMarker, connectionMarker);
                startLocation = new Location(endLocation);
            }


        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void parseTrackFile(String path, int[] color){
        try{
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Track track = (Track) ois.readObject();
            ois.close();

            TrackPoint start = track.getPointbyPos(0);
            Location startLocation = new Location(start.getLat(), start.getLon());
            locations.add(startLocation);
            SimplePointMarker startMarker = new SimplePointMarker(startLocation);
            startMarker.setColor(color(0, 0, 0, 0));
            map.addMarkers(startMarker);
            for (int i=1; i < track.getPoints().size(); i++){
                TrackPoint end = track.getPointbyPos(i);
                Location endLocation = new Location(end.getLat(), end.getLon());
                locations.add(endLocation);
                SimplePointMarker endMarker = new SimplePointMarker(endLocation);
                endMarker.setColor(color(color[0], color[1], color[2], color[3]));
                SimpleLinesMarker connectionMarker = new SimpleLinesMarker(startLocation, endLocation);
                connectionMarker.setColor(color(color[0], color[1], color[2], color[3]));
                connectionMarker.setStrokeWeight(4);
                map.addMarkers(endMarker, connectionMarker);
                startLocation = new Location(endLocation);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    public void parseTrackFileClass(String path, String classPath, int[] color0, int[] color1){
        try{
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Track track = (Track) ois.readObject();
            ois.close();

            fis = new FileInputStream(classPath);
            ois = new ObjectInputStream(fis);
            List<Integer> classes = (List<Integer>) ois.readObject();
            ois.close();



            TrackPoint start = track.getPointbyPos(0);
            Location startLocation = new Location(start.getLat(), start.getLon());
            locations.add(startLocation);
            SimplePointMarker startMarker = new SimplePointMarker(startLocation);
            if (classes.get(0) == 0){
                startMarker.setColor(color(0, 0, 0, 0));
                //startMarker.setColor(color(color0[0], color0[1], color0[2], color0[3]));
            }
            else{
                startMarker.setColor(color(255, 255, 100, color0[3]));
                //startMarker.setColor(color(color1[0], color1[1], color1[2], color1[3]));
            }

            map.addMarkers(startMarker);
            for (int i=1; i < track.getPoints().size(); i++){
                TrackPoint end = track.getPointbyPos(i);
                Location endLocation = new Location(end.getLat(), end.getLon());
                locations.add(endLocation);
                SimplePointMarker endMarker = new SimplePointMarker(endLocation);
                if (classes.get(i) == 0){
                    endMarker.setColor(color(color0[0], color0[1], color0[2], color0[3]));
                }
                else{
                    endMarker.setColor(color(color1[0], color1[1], color1[2], 200));
                }
                SimpleLinesMarker connectionMarker = new SimpleLinesMarker(startLocation, endLocation);
                connectionMarker.setColor(color(color0[0], color0[1], color0[2], color0[3]));
                //connectionMarker.setStrokeWeight(1);
                map.addMarkers(endMarker, connectionMarker);
                startLocation = new Location(endLocation);
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

    }



    public void parseRaw(){
        try {
            FileInputStream fis = new FileInputStream(args[2]);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap<Long, MapNode> nodes = (HashMap<Long, MapNode>) ois.readObject();
            ois.close();

            fis = new FileInputStream(args[3]);
            ois = new ObjectInputStream(fis);
            HashMap<Long, MapWay> ways = (HashMap<Long, MapWay>) ois.readObject();
            ois.close();

            for (MapWay mw:ways.values()){
                List<Long> nrefs = mw.getNodesreferences();
                for (int i = 0; i<nrefs.size()-1; i++){
                    MapNode node1 = nodes.get(nrefs.get(i));
                    MapNode node2 = nodes.get(nrefs.get(i+1));
                    double latF = node1.getLat();
                    double lonF = node1.getLon();
                    double latT = node2.getLat();
                    double lonT = node2.getLon();
                    Location startLocation = new Location(latF, lonF);
                    locations.add(startLocation);
                    SimplePointMarker startMarker = new SimplePointMarker(startLocation);
                    startMarker.setColor(color(0, 255, 0, 100));
                    map.addMarkers(startMarker);
                    Location endLocation = new Location(latT, lonT);
                    locations.add(endLocation);
                    SimplePointMarker endMarker = new SimplePointMarker(endLocation);
                    endMarker.setColor(color(0, 255, 0, 100));
                    SimpleLinesMarker connectionMarker = new SimpleLinesMarker(startLocation, endLocation);
                    connectionMarker.setColor(color(0, 255, 0, 400));
                    connectionMarker.setStrokeWeight(4);
                    map.addMarkers(endMarker, connectionMarker);
                }
            }


        }

        catch (IOException ex){
            System.out.println("nofile");
        }
        catch (ClassNotFoundException ex1){
            System.out.println("noobj");
        }
    }

    public void parseGraphPart(String path){
        if (maxLat != 0.0){
            System.out.println("gothere");
            try{
                FileInputStream fis = new FileInputStream(path);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Graph<Node, Edge> graph = (Graph<Node, Edge>) ois.readObject();
                ois.close();
                //System.out.println("2here");
                KDTree kdt = new KDTree(2);
                for (Node nd:graph.getAllNodes()){
                    double[] keys = new double[2];
                    keys[0] = nd.getLatitude();
                    keys[1] = nd.getLongitude();
                    kdt.insert(keys, nd);
                }
                //System.out.println("3here");
                double[] lowk = {minLat, minLon};
                double[] uppk = {maxLat, maxLon};
                System.out.println(lowk[0]+" "+lowk[1]+" "+uppk[0]+" "+uppk[1]);
                Object[] objs = kdt.range(lowk, uppk);
                for (Object ob:objs){
                    Node nd = (Node) ob;
                    //System.out.println(nd);
                    Collection<Edge> outedges = graph.getOutEdges(nd);
                    double latF = nd.getLatitude();
                    double lonF = nd.getLongitude();
                    Location startLocation = new Location(latF, lonF);
                    SimplePointMarker startMarker = new SimplePointMarker(startLocation);
                    startMarker.setColor(color(0, 255, 0, 10));
                    map.addMarkers(startMarker);
                    for (Edge e:outedges){
                        int toId = e.toId;
                        double latT = graph.getNode(toId).getLatitude();
                        double lonT = graph.getNode(toId).getLongitude();
                        Location endLocation = new Location(latT, lonT);
                        SimplePointMarker endMarker = new SimplePointMarker(endLocation);
                        endMarker.setColor(color(0, 255, 0, 10));
                        SimpleLinesMarker connectionMarker = new SimpleLinesMarker(startLocation, endLocation);
                        connectionMarker.setColor(color(0, 0, 0, 100));
                        connectionMarker.setStrokeWeight(2);
                        map.addMarkers(endMarker, connectionMarker);
                    }
                }
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
            catch (ClassNotFoundException ex1){
                ex1.printStackTrace();
            }
        }
    }

    public void parseGraph(String path){
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Graph<Node, Edge> graph = (Graph<Node, Edge>) ois.readObject();
            ois.close();

            Collection<Edge> edges = graph.getAllEdges();
            Collection<Node> nodes = graph.getAllNodes();
            System.out.println(edges.size());
            System.out.println(nodes.size());
            for (Node n:nodes){
                Collection<Edge> outedges = graph.getOutEdges(n);
                double latF = n.getLatitude();
                double lonF = n.getLongitude();
                Location startLocation = new Location(latF, lonF);
                SimplePointMarker startMarker = new SimplePointMarker(startLocation);
                startMarker.setColor(color(0, 255, 0, 100));
                map.addMarkers(startMarker);
                //locations.add(startLocation);
                for (Edge e:outedges){
                    int toId = e.toId;
                    double latT = graph.getNode(toId).getLatitude();
                    double lonT = graph.getNode(toId).getLongitude();
                    Location endLocation = new Location(latT, lonT);
                    SimplePointMarker endMarker = new SimplePointMarker(endLocation);
                    endMarker.setColor(color(0, 255, 0, 100));
                    SimpleLinesMarker connectionMarker = new SimpleLinesMarker(startLocation, endLocation);
                    connectionMarker.setColor(color(0, 255, 0, 400));
                    connectionMarker.setStrokeWeight(4);
                    map.addMarkers(endMarker, connectionMarker);
                    //locations.add(endLocation);
                }
            }
//            for (Edge e:edges){
//                int fromId = e.fromId;
//                int toId = e.toId;
//                double latF = graph.getNode(fromId).getLatitude();
//                double lonF = graph.getNode(fromId).getLongitude();
//                double latT = graph.getNode(toId).getLatitude();
//                double lonT = graph.getNode(toId).getLongitude();
//                Location startLocation = new Location(latF, lonF);
//                //locations.add(startLocation);
//                SimplePointMarker startMarker = new SimplePointMarker(startLocation);
//                startMarker.setColor(color(0, 255, 0, 100));
//                map.addMarkers(startMarker);
//                Location endLocation = new Location(latT, lonT);
//                //locations.add(endLocation);
//                SimplePointMarker endMarker = new SimplePointMarker(endLocation);
//                endMarker.setColor(color(0, 255, 0, 100));
//                SimpleLinesMarker connectionMarker = new SimpleLinesMarker(startLocation, endLocation);
//                connectionMarker.setColor(color(0, 255, 0, 400));
//                connectionMarker.setStrokeWeight(4);
//                map.addMarkers(endMarker, connectionMarker);
//            }
            System.out.println("done");
        }
        catch (IOException ex){
            System.out.println("nofile");
        }
        catch (ClassNotFoundException ex1){
            System.out.println("noobj");
        }
    }

    public void trackMode(){
        int[] color = new int[4];
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
        color[3] = 100;
        parseTrackFile(args[2], color);
        //map.zoomAndPanToFit(locations);
        map.zoomAndPanTo(12, locations.get(0));
        //map.zoomAndPanToFit(locations);
        //System.out.println(locations.size());
    }



    public void fitMode(){
        int[] color = new int[4];
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
        color[3] = 100;
        parseTrackFile(args[2], color);
        int[] color1 = new int[4];
        color1[0] = 0;
        color1[1] = 0;
        color1[2] = 255;
        color1[3] = 100;
        parseTrackFile(args[3], color1);
        map.zoomAndPanTo(13, locations.get(0));
    }


    public void fitInterpolateMode(){
        int[] color = new int[4];
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
        color[3] = 100;
        parseTrackFile(args[2], color);
        int[] color1 = new int[4];
        color1[0] = 0;
        color1[1] = 0;
        color1[2] = 255;
        color1[3] = 100;
        parseTrackFile(args[3], color1);
        int[] color2 = new int[4];
        color1[0] = 0;
        color1[1] = 255;
        color1[2] = 0;
        color1[3] = 100;
        parseTrackFile(args[4], color2);
        map.zoomAndPanTo(13, locations.get(0));
    }

    public void fitClassifiedMode(){
        int[] color = new int[4];
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
        color[3] = 100;
        parseTrackFile(args[2], color); //track init

        int[] color1 = new int[4];
        color1[0] = 0;
        color1[1] = 0;
        color1[2] = 255;
        color1[3] = 100;
        int[] color2 = new int[4];
        color2[0] = 0;
        color2[1] = 255;
        color2[2] = 0;
        color2[3] = 100;
        parseTrackFileClass(args[3], args[4], color1, color2);
        map.zoomAndPanTo(13, locations.get(0));
    }

    public void graphMode(){
        parseGraph(args[2]);
        //map.zoomAndPanToFit(locations);
        map.zoomAndPanTo(18, locations.get(129));
    }

    public void graphtrackMode(){
        int[] color = new int[4];
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
        color[3] = 100;
        System.out.println("here");
        parseTrack(args[2], color);
        System.out.println("done");
        parseGraph(args[3]);
        map.zoomAndPanToFit(locations);
    }

    public void allMode(){
        int[] color = new int[4];
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
        color[3] = 100;
        parseTrack(args[2], color);
        int[] color1 = new int[4];
        color1[0] = 0;
        color1[1] = 0;
        color1[2] = 255;
        color1[3] = 100;
        parseTrack(args[3], color1);
        parseGraphPart(args[4]);
        map.zoomAndPanTo(15, locations.get(0));
    }

    public void rawMode(){
        parseRaw();
        map.zoomAndPanTo(17, locations.get(150));
    }
}
