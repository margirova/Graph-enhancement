package stats;

import com.umotional.basestructures.Graph;
import com.umotional.basestructures.Node;
import graph.Track;
import com.umotional.basestructures.Edge;
import net.sf.javaml.core.kdtree.KDTree;
import processing.core.PApplet;
import trackfit.GraphProcessor;
import visualization.Visualization;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        FileInputStream fis = new FileInputStream("graphs/graph_oneway.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Graph<Node, Edge> graph = (Graph<Node, Edge>) ois.readObject();
        ois.close();

        System.out.println("graph read");

        KDTree kdt = new KDTree(2);
        for (Node nd:graph.getAllNodes()){
            double[] keys = new double[2];
            keys[0] = nd.getLatitude();
            keys[1] = nd.getLongitude();
            kdt.insert(keys, nd);
        }

        System.out.println("kdt established");

        String pathToFolder = "tracks_regrouped";
        int c = 0;
        for (File f : new File(pathToFolder).listFiles()) {
            String fname = f.getName();
            c++;
            System.out.println(fname);
            String filetrack = "tracks_regrouped/"+fname;
            File file = new File(filetrack);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            Track t = new Track();
            while ((st = br.readLine()) != null) {
                String[] splited = st.split(",");
                int lat = Integer.parseInt(splited[0]);
                int lon = Integer.parseInt(splited[1]);
                t.addPoint(lat, lon, false);
            }

            if (t.getLength() < 2){
                continue;
            }

            GraphProcessor grpr = new GraphProcessor(graph, kdt, t);
            try{
                grpr.dijkstra();
            }
            catch (OutOfMemoryError ex){
                grpr = null;
                System.out.println("outofmemory");
                continue;
            }
            String outputtrack = "outputtracks/output"+fname;
            if (grpr.getOutputTrack() != null){
                grpr.saveOutputTrack(outputtrack);
                Double trackFullDist = t.getFullDist();
                List<Double> trackDists = t.getDistancesBetween();

                Double pathFullDist = grpr.getOutputTrack().getFullDist();
                List<Double> pathDists = grpr.getOutputTrack().getDistancesBetween();
//                List<StatUnit> pathStats = grpr.getStats();
////                TrackStatistics fullStatistics = new TrackStatistics(trackDists, pathDists, pathStats,
////                                                            trackFullDist, pathFullDist);
//                FileOutputStream fos = new FileOutputStream("stats/"+fname.substring(0, 6)+".ser");
//                ObjectOutputStream oos = new ObjectOutputStream(fos);
//                oos.writeObject(fullStatistics);
//                oos.close();

                grpr = null;
                pathDists = null;
//                pathStats = null;
                trackDists = null;

                System.out.println(fname+" "+c+" success");
            }
            else{
                System.out.println(fname+" "+c+" fail");
            }
        }
    }

}
