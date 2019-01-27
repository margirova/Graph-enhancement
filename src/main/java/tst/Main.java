package tst;

import com.umotional.basestructures.Edge;
import com.umotional.basestructures.Graph;
import com.umotional.basestructures.Node;
import graph.Track;
import stats.StatUnitAreaDistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String inputFolder = "tracks_input/";
        String outputFolder = "tracks_output/";
        String interpOutFolder = "tracks_output_interpolated/";
        String ultimateStatFolder = "ultimate_stats/";

        int c = 0;

        FileInputStream fis = new FileInputStream("graphs/graph_oneway.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Graph<Node, Edge> graph = (Graph<Node, Edge>) ois.readObject();
        ois.close();

        System.out.println("num nodes " + graph.getAllNodes().size());
        System.out.println("num edges " + graph.getAllEdges().size());

        fis = new FileInputStream(ultimateStatFolder + "u_mapping.ser");
        ois = new ObjectInputStream(fis);
        HashMap<StatUnitAreaDistance, List<Integer>> indexMapping =
                (HashMap<StatUnitAreaDistance, List<Integer>>) ois.readObject();
        ois.close();

        System.out.println("processed " + indexMapping.size());

        System.out.println("processed %  " + (double) indexMapping.size()/(double)graph.getAllNodes().size());

        double sumLen = 0;
        double sumBetween = 0;

        double mLen = 0;
        double mBetween = 0;

        List<Double> lLen = new ArrayList<>();
        List<Double> lBetween = new ArrayList<>();

        for (File f : new File(inputFolder).listFiles()) {
            String fname = f.getName();
            c++;
            String filetrack = inputFolder + fname;
            File file = new File(filetrack);
            fis = new FileInputStream(inputFolder + fname);
            ois = new ObjectInputStream(fis);

            try {
                Track t = (Track) ois.readObject();
                ois.close();
                sumLen += t.getFullDist();
                lLen.add(t.getFullDist());
                if (t.getLength()!=0) {
                    sumBetween += t.getFullDist() / t.getLength();
                    lBetween.add(t.getFullDist() / t.getLength());
                }
            }
            catch (Exception e){
                System.out.println("aaaa "+c);
            }

        }
        mLen = sumLen/lLen.size();
        mBetween = sumBetween/lLen.size();

        double stdLen = 0;
        double stdBetween = 0;

        for (int i = 0; i < lLen.size(); i ++){
            stdLen += (lLen.get(i) - mLen)*(lLen.get(i) - mLen);
            stdBetween += (lBetween.get(i) - mBetween)*(lBetween.get(i) - mBetween);
        }

        stdLen = Math.sqrt(stdLen)/(lLen.size()-1);
        stdBetween = Math.sqrt(stdBetween)/(lLen.size()-1);

        System.out.println("av Lne "+mLen+" stdLen "+stdLen);
        System.out.println("av Betw "+mBetween+" stdBetw "+stdBetween);


        System.out.println("paaaths");

        sumLen = 0;
        sumBetween = 0;

        mLen = 0;
        mBetween = 0;

        lLen = new ArrayList<>();
        lBetween = new ArrayList<>();
        c=0;
        for (File f : new File(interpOutFolder).listFiles()) {
            String fname = f.getName();
            c++;
            String filetrack = interpOutFolder + fname;
            File file = new File(filetrack);
            fis = new FileInputStream(interpOutFolder + fname);
            ois = new ObjectInputStream(fis);
            try {
                Track t = (Track) ois.readObject();
                ois.close();
                sumLen += t.getFullDist();
                lLen.add(t.getFullDist());
                if (t.getLength()!=0) {
                    sumBetween += t.getFullDist() / t.getLength();
                    lBetween.add(t.getFullDist() / t.getLength());
                }
            }
            catch (Exception e){
                System.out.println("aaaa "+c);
            }
        }
        mLen = sumLen/lLen.size();
        mBetween = sumBetween/lLen.size();

        stdLen = 0;
        stdBetween = 0;

        for (int i = 0; i < lLen.size(); i ++){
            stdLen += (lLen.get(i) - mLen)*(lLen.get(i) - mLen);
            stdBetween += (lBetween.get(i) - mBetween)*(lBetween.get(i) - mBetween);
        }

        stdLen = Math.sqrt(stdLen)/(lLen.size()-1);
        stdBetween = Math.sqrt(stdBetween)/(lLen.size()-1);

        System.out.println("av Lne "+mLen+" stdLen "+stdLen);
        System.out.println("av Betw "+mBetween+" stdBetw "+stdBetween);



    }
}
