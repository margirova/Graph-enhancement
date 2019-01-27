package detector;

import classifier.KMeansClassifier;
import classifier.Statistic1DClassifier;
import com.umotional.basestructures.Edge;
import com.umotional.basestructures.Graph;
import com.umotional.basestructures.Node;
import graph.Track;
import net.sf.javaml.core.kdtree.KDTree;
import processing.core.PApplet;
import stats.StatUnitAreaDistance;
import stats.StatUnitInterpolatedDistance;
import stats.StatUnitInvDistance;
import statsvisualization.StatVisualization;
import visualization.Visualization;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        //String trackFolder = "tracked_Prague_05-2015_01-2016/";
        String trackFolder = "test_f/";
        String inputFolder = "tracks_input/";
        String outputFolder = "tracks_output/";
        String interpOutFolder = "tracks_output_interpolated/";
        String interpInFolder = "tracks_input_interpolated/";

        String statsAreaDistanceFolder = "area_distance_stats/";
        String statsInterpolatedDistanceFolder = "interp_distance_stats/";
        String statsInvDistanceFolder = "inv_distance_stats/";
        String ultimateStatFolder = "ultimate_stats/";

//        String classifiedFolder = "classified/";
        String classifiedFolder = "classified_test/";
        String detectedFolder = "detected/";

        int c = 0;

        FileInputStream fis = new FileInputStream("graphs/graph_oneway.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Graph<Node, Edge> graph = (Graph<Node, Edge>) ois.readObject();
        ois.close();

        KDTree kdt = new KDTree(2);
        for (Object obj:graph.getAllNodes()){
            Node nd = (Node) obj;
            double[] keys = new double[2];
            keys[0] = nd.getLatitude();
            keys[1] = nd.getLongitude();
            kdt.insert(keys, nd);
        }

        System.out.println("graph read");
        Detector det = new Detector(graph, kdt);
        for (File f : new File(classifiedFolder).listFiles()) {
            String fname = f.getName();
            if (!fname.substring(6, 16).equals("_interp_zr")){
                continue;
            }
            System.out.println(fname);
            fis = new FileInputStream(statsInterpolatedDistanceFolder + fname.substring(0, 6)+".ser");
            ois = new ObjectInputStream(fis);
            List<StatUnitInterpolatedDistance> statInterp = (List<StatUnitInterpolatedDistance>) ois.readObject();
            ois.close();
            c++;

            fis = new FileInputStream(classifiedFolder+fname);
            ois = new ObjectInputStream(fis);
            List<Integer> classified = (List<Integer>) ois.readObject();
            ois.close();

            fis = new FileInputStream(interpOutFolder+fname.substring(0,6)+".ser");
            ois = new ObjectInputStream(fis);
            Track trackInterpOut = (Track) ois.readObject();
            ois.close();

            fis = new FileInputStream(interpInFolder+fname.substring(0,6)+".ser");
            ois = new ObjectInputStream(fis);
            Track trackInterpIn = (Track) ois.readObject();
            ois.close();

            List<Integer> detected = det.detectPrev(fname.substring(0, 6), classified, trackInterpOut);
            FileOutputStream fos = new FileOutputStream(detectedFolder + fname.substring(0, 6)  +".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(detected);
            oos.close();

        }

        // visualize
        String n = "000030";
        String filetrack = interpInFolder + n + ".ser";
        String outtrack = outputFolder + n + ".ser";
        String interpolated = interpOutFolder + n + ".ser";
        String outpicture = "picturesout/"+ n + ".jpeg";
        String classified_interp_zr = classifiedFolder + n + "_interp_zr"+".ser";
        String detected = detectedFolder + n + ".ser";


//        PApplet.main(new String[] { Visualization.class.getName(), "fit",
//                outpicture, filetrack, interpolated});
        PApplet.main(new String[] { Visualization.class.getName(), "fitclassified",
                outpicture, filetrack, interpolated, detected});

        String interpStat = statsInterpolatedDistanceFolder + n + ".ser";


        //interp zr
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatterinterpclassified",
//                interpStat, classified_interp_zr});


    }
}
