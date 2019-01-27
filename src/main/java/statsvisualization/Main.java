package statsvisualization;

import com.umotional.basestructures.Edge;
import com.umotional.basestructures.Graph;
import com.umotional.basestructures.Node;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import processing.core.PApplet;
import stats.TrackStatistics;
import visualization.Visualization;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String name = "000030";
        String pathStat = "stats/"+name+".ser";
//        List<Double> xaxis = new ArrayList<>();
//
//        FileInputStream fis = new FileInputStream(pathStat);
//        ObjectInputStream ois = new ObjectInputStream(fis);
//        List<StatUnit> stats = (List<StatUnit>) ois.readObject();
//        ois.close();
//
//        for (StatUnit su:stats){
//            if (su.getdArea()!=0.0){
//                xaxis.add(su.getdArea());
//            }
//
//        }
//        Statistics st = new Statistics();
//        st.histogram(xaxis, 1500);
//        String histpath = "hists/histogram"+name+".hi";
//        st.saveHist(histpath);

//        String folder = "stats/";
//        List<Double> xaxis = new ArrayList<>();
//        for (File f : new File(folder).listFiles()) {
//            FileInputStream fis = new FileInputStream(folder+f.getName());
//            ObjectInputStream ois = new ObjectInputStream(fis);
//            List<StatUnit> stats = (List<StatUnit>) ois.readObject();
//            ois.close();
//
//            for (StatUnit su:stats){
//                if (su.getdArea()!=0.0){
//                    xaxis.add(su.getdArea());
//                }
//
//            }
//        }

        String folder = "stats/";
        List<Double> xaxis = new ArrayList<>();
        File f = new File(folder+name+".ser");
        FileInputStream fis = new FileInputStream(folder+f.getName());
        ObjectInputStream ois = new ObjectInputStream(fis);
        TrackStatistics stats = (TrackStatistics) ois.readObject();
        ois.close();

//        for (StatUnit su:stats.getPathStat()){
//            if (su.getdArea() != 0.0){
//                xaxis.add(su.getdArea());
//                //!!!
//            }
//        }
        Statistics st = new Statistics();
//        double mean = st.mean(xaxis);
//        double std = st.std(xaxis, mean);
        //List<Double> yaxis = st.gauss(xaxis, mean, std);
        st.histogram(xaxis, 1500);
        String histpath = "hists/histogram_"+name+"_1"+".hi";
        st.saveHist(histpath);
//        System.out.println(mean+" "+std);
//        for (Double[] key:st.getHist().keySet()){
//            System.out.println(key[0]+" "+key[1]+" "+st.getHist().get(key));
//        }

        //StatVisualization.main(new String[] {StatVisualization.class.getName(), "line", pathStat});

        String imgFolderPath = "imgs/"+name+"/";

        File theDir = new File(imgFolderPath);
        if (!theDir.exists()) {
            theDir.mkdirs();
            System.out.println("created");
        }

//        if (!theDir.exists()) {
//            System.out.println("creating directory: " + theDir.getName());
//            boolean result = false;
//
//            try{
//                theDir.mkdir();
//                result = true;
//            }
//            catch(SecurityException se){
//                se.printStackTrace();
//            }
//            if(result) {
//                System.out.println("DIR created");
//            }
//        }

        String linePath = imgFolderPath + "line.png";
        String histPath = imgFolderPath + "hist.png";
        String mapPath = imgFolderPath + "map.jpeg";


        String filetrack = "tracks_regrouped/"+name+".tra";
        String outtrack = "outputtracks/output"+name+".tra";
        StatVisualization.main(new String[] {StatVisualization.class.getName(), "line", pathStat, linePath, "dist"});
        //StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatter", pathStat, linePath, "dist"});
        //StatVisualization.main(new String[] {StatVisualization.class.getName(), "histogram", histpath, histPath});
        //PApplet.main(new String[] { Visualization.class.getName(), "fit", mapPath, filetrack, outtrack});

    }
}
