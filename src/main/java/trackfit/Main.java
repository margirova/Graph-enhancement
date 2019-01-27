package trackfit;

import com.umotional.basestructures.*;
import graph.*;
import net.sf.javaml.core.kdtree.KDTree;
import processing.core.PApplet;
import stats.StatUnitAreaDistance;
import stats.StatUnitInterpolatedDistance;
import stats.StatUnitInvDistance;
import statsvisualization.StatVisualization;
import visualization.Visualization;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException{
        String trackFolder = "tracked_Prague_05-2015_01-2016/";
//        String trackFolder = "test_f/";
        String inputFolder = "tracks_input/";
        String outputFolder = "tracks_output/";
        String interpOutFolder = "tracks_output_interpolated/";
        String interpInFolder = "tracks_input_interpolated/";
        String statsAreaDistanceFolder = "area_distance_stats/";
        String statsInterpolatedDistanceFolder = "interp_distance_stats/";
        String statsInvDistanceFolder = "inv_distance_stats/";
        String ultimateStatFolder = "ultimate_stats/";

        int c = 0;
//
        FileInputStream fis = new FileInputStream("graphs/graph_oneway.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Graph<Node, Edge> graph = (Graph<Node, Edge>) ois.readObject();
        ois.close();

        System.out.println(graph.getAllNodes().size());
////
//        FileInputStream fis = new FileInputStream("graphs/graph_bothways.ser");
//        ObjectInputStream ois = new ObjectInputStream(fis);
//        Graph<Node, Edge> graph = (Graph<Node, Edge>) ois.readObject();
//        ois.close();
////        System.out.println("graph read");
//
        KDTree kdt = new KDTree(2);
        for (Node nd:graph.getAllNodes()){
            double[] keys = new double[2];
            keys[0] = nd.getLatitude();
            keys[1] = nd.getLongitude();
            kdt.insert(keys, nd);
        }

        System.out.println("kdt established");

        List<StatUnitInterpolatedDistance> ultimateInterpStat = new ArrayList<>();
        List<StatUnitInvDistance> ultimateInvStat = new ArrayList<>();
        List<StatUnitAreaDistance> ultimate2DStat = new ArrayList<>();
        HashMap<StatUnitAreaDistance, List<Integer>> statMapping = new HashMap<>();
        IndexMapper im = new IndexMapper();
        for (File f : new File(trackFolder).listFiles()) {
            String fname = f.getName();
            c++;
//            System.out.println(fname);
            String filetrack = trackFolder +fname;
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
            if (t.getLength() <= 3){
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

            if (grpr.getOutputTrack() != null){
                Track out = grpr.getOutputTrack();
                if (out.getLength() <= 3){
                    continue;
                }
                List<StatUnitAreaDistance> statsAreaDistance = grpr.getStats();

                FileOutputStream fos = new FileOutputStream(inputFolder+fname.substring(0, 6)+".ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(t);
                oos.close();

                im.addUnits(statMapping, ultimate2DStat, statsAreaDistance);


                fos = new FileOutputStream(statsAreaDistanceFolder + fname.substring(0, 6) + ".ser");
                oos = new ObjectOutputStream(fos);
                oos.writeObject(statsAreaDistance);
                oos.close();

                fos = new FileOutputStream(outputFolder + fname.substring(0, 6) + ".ser");
                oos = new ObjectOutputStream(fos);
                oos.writeObject(out);
                oos.close();

//                System.out.println("outt "+out);


                GraphHelperFunctions ghf = new GraphHelperFunctions();

                double avDistT = t.getFullDist()/(t.getLength() - 1);
                double avDistP = out.getFullDist()/(out.getLength() - 1);
                double avDist = Math.min(avDistP, avDistT);
                Track interpOut = ghf.interpolate(out, avDist);
                Track interpIn = ghf.interpolate(t, avDist);

                fos = new FileOutputStream(interpOutFolder + fname.substring(0, 6) + ".ser");
                oos = new ObjectOutputStream(fos);
                oos.writeObject(interpOut);
                oos.close();

                fos = new FileOutputStream(interpInFolder + fname.substring(0, 6) + ".ser");
                oos = new ObjectOutputStream(fos);
                oos.writeObject(interpIn);
                oos.close();

//                System.out.println("intt "+it);

                List<StatUnitInterpolatedDistance> statinterp = ghf.createPathInterpolated2TrackStat(interpIn, interpOut);
                ultimateInterpStat.addAll(statinterp);

                fos = new FileOutputStream(statsInterpolatedDistanceFolder + fname.substring(0, 6) + ".ser");
                oos = new ObjectOutputStream(fos);
                oos.writeObject(statinterp);
                oos.close();

                List<StatUnitInvDistance> statinv  = ghf.createTrack2PathInterpolatedStat(t, out);
                ultimateInvStat.addAll(statinv);

                fos = new FileOutputStream(statsInvDistanceFolder + fname.substring(0, 6) + ".ser");
                oos = new ObjectOutputStream(fos);
                oos.writeObject(statinv);
                oos.close();

                grpr = null;
                System.out.println(fname+" "+c+" success ");
            }
            else{
                System.out.println(fname+" "+c+" fail");
                continue;
            }

        }

        FileOutputStream fos = new FileOutputStream(ultimateStatFolder + "u_inv_stat" + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(ultimateInvStat);
        oos.close();

        fos = new FileOutputStream(ultimateStatFolder + "u_interp_stat" + ".ser");
        oos = new ObjectOutputStream(fos);
        oos.writeObject(ultimateInterpStat);
        oos.close();

        fos = new FileOutputStream(ultimateStatFolder + "u_2d_stat" + ".ser");
        oos = new ObjectOutputStream(fos);
        oos.writeObject(ultimate2DStat);
        oos.close();

        fos = new FileOutputStream(ultimateStatFolder + "u_mapping" + ".ser");
        oos = new ObjectOutputStream(fos);
        oos.writeObject(statMapping);
        oos.close();


//        List<StatUnitAreaDistance> ultimate2DStat = new ArrayList<>();
//        HashMap<StatUnitAreaDistance, List<Integer>> statMapping = new HashMap<>();
//        IndexMapper im = new IndexMapper();
//        for (File f : new File(statsAreaDistanceFolder).listFiles()) {
//            String fname = f.getName();
//            FileInputStream fis = new FileInputStream(statsAreaDistanceFolder+fname);
//            ObjectInputStream ois = new ObjectInputStream(fis);
//            List<StatUnitAreaDistance> stats = (List<StatUnitAreaDistance>) ois.readObject();
//            ois.close();
//            im.addUnits(statMapping, ultimate2DStat, stats);
//        }
//        System.out.println("done "+ statMapping.size()+" "+ultimate2DStat.size());
//        FileOutputStream fos = new FileOutputStream(ultimateStatFolder + "u_2d_stat" + ".ser");
//        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        oos.writeObject(ultimate2DStat);
//        oos.close();
//
//        fos = new FileOutputStream(ultimateStatFolder + "u_mapping" + ".ser");
//        oos = new ObjectOutputStream(fos);
//        oos.writeObject(statMapping);
//        oos.close();

        // visualize
        String n = "000620";
        String filetrack = inputFolder + n + ".ser";
        //String filetrack = inputFolder + n + ".ser";
        String outtrack = outputFolder + n + ".ser";
        String interpolated = interpOutFolder + n + ".ser";
        String outpicture = "picturesout/"+ n + ".jpeg";
//        PApplet.main(new String[] { Visualization.class.getName(), "track", outpicture, filetrack});
//        PApplet.main(new String[] { Visualization.class.getName(), "fit", outpicture, filetrack, outtrack});
        //PApplet.main(new String[] { Visualization.class.getName(), "track", outpicture, interpolated});
//        PApplet.main(new String[] { Visualization.class.getName(), "fit", outpicture, filetrack, outtrack});
//        PApplet.main(new String[] { Visualization.class.getName(), "fitinterpolate",
//                outpicture, filetrack, outtrack, interpolated});
//
        String invStat = statsInvDistanceFolder + n + ".ser";
        String interpStat = statsInterpolatedDistanceFolder + n + ".ser";
        String areaStat = statsAreaDistanceFolder + n + ".ser";

//
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "lineinterp", interpStat});
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "lineinverse", invStat});
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatter", areaStat});
    }



}
