package classifier;

import net.sf.javaml.clustering.*;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import stats.StatUnitAreaDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class KMeansClassifier {
    private HashMap<Instance, Integer> mapping;
    private Dataset ds;
    private List<StatUnitAreaDistance> bigStat;
    List<Integer> bigClassification;
    private HashMap<StatUnitAreaDistance, List<Integer>> indexMapping;
    public KMeansClassifier(List<StatUnitAreaDistance> stats,
                            int numClasses,
                            HashMap<StatUnitAreaDistance, List<Integer>> indexMapping){

        System.out.println(numClasses + " "+stats.size()+" "+indexMapping.size());
        this.bigStat = stats;
        this.bigClassification = classify2D(bigStat, numClasses);
        this.indexMapping = indexMapping;
    }

    public List<Integer> classify(List<StatUnitAreaDistance> smallStats){
        int[] classes = new int[smallStats.size()];
        for (int i=0; i<smallStats.size(); i++){
            StatUnitAreaDistance su = smallStats.get(i);
            List<Integer> lIdx = indexMapping.get(su);
            for (Integer idx: lIdx){
                int cls = this.bigClassification.get(idx);
                classes[i] = cls;
            }

        }
        List<Integer> classified = Arrays.stream(classes).boxed().collect(Collectors.toList());
        return classified;
    }


    public List<Integer> classify2D(List<StatUnitAreaDistance> stats,int numClasses){
        create2DDataset(stats);
        KMeans km = new KMeans(numClasses, 100);
        //KMedoids km = new KMedoids();

//        SOM km = new SOM();
//        Cobweb km = new Cobweb();
//        DensityBasedSpatialClustering km = new DensityBasedSpatialClustering(0.1, 2);
        Dataset[] clustered = km.cluster(ds);
        System.out.println("sup?");
        int[] classes = new int[stats.size()];
        for (int a=0; a<clustered.length; a++){
            System.out.println(">>>>"+a+" "+clustered[a].size());
        }
        //looking for the biggest cluster
        int biggest = 0;
        int size = 0;
        for (int i = 0; i<clustered.length; i++){
            int sz = clustered[i].size();
            if (sz > size){
                size = sz;
                biggest = i;
            }
        }
        //assigning 1 to biggest class instances
        for (int j = 0; j<clustered[biggest].size(); j++){
            Instance inst = clustered[biggest].instance(j);
            int idx = mapping.get(inst);
            classes[idx] = 1;
        }

        List<Integer> li = Arrays.stream(classes).boxed().collect(Collectors.toList());
        return li;
    }

    public void create2DDataset(List<StatUnitAreaDistance> stats){
        ds = new DefaultDataset();
        mapping = new HashMap<>();

        for (int i=0; i < stats.size(); i++){
            double dist = stats.get(i).getDist();
            double area = stats.get(i).getArea();
            double[] values = new double[]{dist, area};
            Instance instance = new DenseInstance(values);
            this.ds.add(instance);
            mapping.put(instance, i);
        }

    }


}
