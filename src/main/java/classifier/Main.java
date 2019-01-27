package classifier;

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
        String inputFolder = "tracks_input/";
        String outputFolder = "tracks_output/";
        String interpOutFolder = "tracks_output_interpolated/";
        String interpInFolder = "tracks_input_interpolated/";
        String statsAreaDistanceFolder = "area_distance_stats/";
        String statsInterpolatedDistanceFolder = "interp_distance_stats/";
        String statsInvDistanceFolder = "inv_distance_stats/";

//        String statsAreaDistanceFolder = "a_d_stats_test/";
//        String statsInterpolatedDistanceFolder = "interp_stats_test/";
//        String statsInvDistanceFolder = "inv_stats_test/";

        String classifiedFolder = "classified/";
        String ultimateStatFolder = "ultimate_stats/";
//
        int c = 0;
//
        FileInputStream fis = new FileInputStream(ultimateStatFolder + "u_interp_stat.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
//        List<StatUnitInterpolatedDistance> ultimateInterp = (List<StatUnitInterpolatedDistance>) ois.readObject();
        ois.close();

        fis = new FileInputStream(ultimateStatFolder + "u_inv_stat.ser");
        ois = new ObjectInputStream(fis);
//        List<StatUnitInvDistance> ultimateInv = (List<StatUnitInvDistance>) ois.readObject();
        ois.close();

        fis = new FileInputStream(ultimateStatFolder + "u_2d_stat.ser");
        ois = new ObjectInputStream(fis);
//        List<StatUnitAreaDistance> ultimate2D = (List<StatUnitAreaDistance>) ois.readObject();
        ois.close();

        System.out.println("Stats read");
//        Statistic1DClassifier classifier1D = new Statistic1DClassifier(ultimateInv, ultimateInterp);
//        for (File f : new File(statsInterpolatedDistanceFolder).listFiles()) {
//            String fname = f.getName();
//            fis = new FileInputStream(statsInterpolatedDistanceFolder + fname);
//            ois = new ObjectInputStream(fis);
//            List<StatUnitInterpolatedDistance> statInterp = (List<StatUnitInterpolatedDistance>) ois.readObject();
//            ois.close();
//            c++;
//
//
//            List<Integer> clsInterpZ = classifier1D.zScoreClassifyInterp(statInterp);
//            List<Integer> clsInterpZR = classifier1D.zScoreRobustClassifyInterp(statInterp);
//            List<Integer> clsInterpIQR = classifier1D.iqrClassifyInterp(statInterp);
//
//            System.out.println(fname + " "+ c + " "+ statInterp.size() + " "+ clsInterpZR.size());
//
//            FileOutputStream fos = new FileOutputStream(classifiedFolder + fname.substring(0, 6) +"_interp_z" +".ser");
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(clsInterpZ);
//            oos.close();
//
//            fos = new FileOutputStream(classifiedFolder + fname.substring(0, 6) +"_interp_zr" +".ser");
//            oos = new ObjectOutputStream(fos);
//            oos.writeObject(clsInterpZR);
//            oos.close();
//
//            fos = new FileOutputStream(classifiedFolder + fname.substring(0, 6) +"_interp_iqr" +".ser");
//            oos = new ObjectOutputStream(fos);
//            oos.writeObject(clsInterpIQR);
//            oos.close();
//        }
//
//        System.out.println("Interp done");
//        for (File f : new File(statsInvDistanceFolder).listFiles()) {
//            String fname = f.getName();
//            fis = new FileInputStream(statsInvDistanceFolder + fname);
//            ois = new ObjectInputStream(fis);
//            List<StatUnitInvDistance> statInterp = (List<StatUnitInvDistance>) ois.readObject();
//            ois.close();
//            c++;
//            System.out.println(fname + " "+ c);
//
//            List<Integer> clsInterpZ = classifier1D.zScoreClassifyInv(statInterp);
//            List<Integer> clsInterpZR = classifier1D.zScoreRobustClassifyInv(statInterp);
//            List<Integer> clsInterpIQR = classifier1D.iqrClassifyInv(statInterp);
//
//            FileOutputStream fos = new FileOutputStream(classifiedFolder + fname.substring(0, 6) +"_inv_z" +".ser");
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(clsInterpZ);
//            oos.close();
//
//            fos = new FileOutputStream(classifiedFolder + fname.substring(0, 6) +"_inv_zr" +".ser");
//            oos = new ObjectOutputStream(fos);
//            oos.writeObject(clsInterpZR);
//            oos.close();
//
//            fos = new FileOutputStream(classifiedFolder + fname.substring(0, 6) +"_inv_iqr" +".ser");
//            oos = new ObjectOutputStream(fos);
//            oos.writeObject(clsInterpIQR);
//            oos.close();
//        }
//        //K
//        System.out.println("Inverse done");

        fis = new FileInputStream(ultimateStatFolder + "u_mapping.ser");
        ois = new ObjectInputStream(fis);
        HashMap<StatUnitAreaDistance, List<Integer>> indexMapping =
                (HashMap<StatUnitAreaDistance, List<Integer>>) ois.readObject();
        ois.close();

        System.out.println(indexMapping.size());

//        KMeansClassifier km = new KMeansClassifier(ultimate2D, 2, indexMapping);
        //List<Integer> classified2D = km.classify(ultimate2D, 4);
//
//        FileOutputStream fos = new FileOutputStream(classifiedFolder + "classified_2d" +".ser");
//        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        oos.writeObject(km.bigClassification);
//        oos.close();
//
//        for (File f : new File(statsAreaDistanceFolder).listFiles()) {
//            String fname = f.getName();
//            //System.out.println(fname);
//            fis = new FileInputStream(statsAreaDistanceFolder + fname);
//            ois = new ObjectInputStream(fis);
//            List<StatUnitAreaDistance> stat2D = (List<StatUnitAreaDistance>) ois.readObject();
//            ois.close();
//            c++;
//            //System.out.println(fname + " "+ c);
//
//            List<Integer> classified2D = km.classify(stat2D);
//
//            fos = new FileOutputStream(classifiedFolder + fname.substring(0, 6) +"_2d" +".ser");
//            oos = new ObjectOutputStream(fos);
//            oos.writeObject(classified2D);
//            oos.close();
//        }

        System.out.println("Kmeans done");
//
        // visualize
        String n = "000597";
        String filetrack = interpInFolder + n + ".ser";
        String outtrack = outputFolder + n + ".ser";
        String interpolated = interpOutFolder + n + ".ser";
        String outpicture = "picturesout/"+ n + ".jpeg";
        String classified_interp_z = classifiedFolder + n + "_interp_z"+".ser";
        String classified_interp_zr = classifiedFolder + n + "_interp_zr"+".ser";
        String classified_interp_iqr = classifiedFolder + n + "_interp_iqr"+".ser";

        String classified_inv_z = classifiedFolder + n + "_inv_z"+".ser";
        String classified_inv_zr = classifiedFolder + n + "_inv_zr"+".ser";
        String classified_inv_iqr = classifiedFolder + n + "_inv_iqr"+".ser";
        String classified_k = classifiedFolder + n + "_2d"+".ser";

        // classified interp z
//        PApplet.main(new String[] { Visualization.class.getName(), "fitclassified",
//                outpicture, filetrack, outtrack, classified_interp_z});
        // classified interp zr

//        PApplet.main(new String[] { Visualization.class.getName(), "fit",
//                outpicture, filetrack, interpolated});
//        PApplet.main(new String[] { Visualization.class.getName(), "fitclassified",
//                outpicture, filetrack, interpolated, classified_interp_zr});
//        // classified interp iqr
//        PApplet.main(new String[] { Visualization.class.getName(), "fitclassified",
//                outpicture, filetrack, interpolated, classified_interp_iqr});
//
//        // classified inv z
//        PApplet.main(new String[] { Visualization.class.getName(), "fitclassified",
//                outpicture, filetrack, outtrack, classified_inv_z});
//        // classified inv zr
//        PApplet.main(new String[] { Visualization.class.getName(), "fitclassified",
//                outpicture, filetrack, outtrack, classified_inv_zr});
//        // classified inv iqr
//        PApplet.main(new String[] { Visualization.class.getName(), "fitclassified",
//                outpicture, filetrack, outtrack, classified_inv_iqr});
//
//        // classified kmeans
//        PApplet.main(new String[] { Visualization.class.getName(), "fitclassified",
//                outpicture, filetrack, outtrack, classified_k});


        String invStat = statsInvDistanceFolder + n + ".ser";
        String interpStat = statsInterpolatedDistanceFolder + n + ".ser";
        String areaStat = statsAreaDistanceFolder + n + ".ser";

        String all2d = ultimateStatFolder+"u_2d_stat.ser";
        String classified_2d = classifiedFolder+"classified_2d.ser";

        //interp z
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatterinterpclassified",
//                interpStat, classified_interp_z});
//        //interp zr
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatterinterpclassified",
//                interpStat, classified_interp_zr});
//        //interp iqr
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatterinterpclassified",
//                interpStat, classified_interp_iqr});
//        //inv z
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatterinvclassified",
//                invStat, classified_inv_z});
//        //inv zr
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatterinvclassified",
//                invStat, classified_inv_zr});
//
//        //inv iqr
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatterinvclassified",
//                invStat, classified_inv_iqr});
//
//
//        //kmeans full
//        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatter2dclassified",
//                all2d, classified_2d});
//
//        //kmeans
        StatVisualization.main(new String[] {StatVisualization.class.getName(), "scatter2dclassified",
                areaStat, classifiedFolder + n +"_2d" +".ser"});
    }



}
