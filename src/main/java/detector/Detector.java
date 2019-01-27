package detector;

import com.umotional.basestructures.Graph;
import com.umotional.basestructures.Node;
import graph.Track;
import graph.TrackPoint;
import net.sf.javaml.core.kdtree.KDTree;
import stats.StatUnitInvDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Detector {
    private KDTree kdt;
    Graph graph;
    private HashMap<Node, String> mapping;
    public Detector(Graph graph, KDTree kdt){
        this.graph = graph;
        this.kdt = kdt;
        this.mapping = new HashMap<>();
    }


    public List<Integer> detectPrev(String name, List<Integer> inlist, Track path){
        //List<Integer> outlist = new ArrayList<>();
        int[] classes = new int[inlist.size()];
        TrackPoint candidate = null;
        boolean flag = false;
        //System.out.println(inlist);
        for (int i = 0; i < inlist.size(); i++){
           // System.out.println(i+" "+inlist.get(i));

            if (inlist.get(i) == 0) { //inlier
                candidate = null;
            }
            else{
                if (candidate == null) {
                    TrackPoint tp = path.getPointbyPos(i);
                    candidate = tp;

                    if (i != 0) {
                        int j = i - 1;
                        while (j != 0) {
                            tp = path.getPointbyPos(j);
                            if (!tp.isInterpolated()) {

                                double[] key = new double[2];
                                key[0] = candidate.getLat();
                                key[1] = candidate.getLon();
                                Node outlier = (Node) kdt.nearest(key);
                                mapping.put(outlier, name);
//                                flag = true;
                                classes[j] = 1;
                                break;
                            }
                            j--;
                        }
                    }
                    else{

                        if (!tp.isInterpolated()) {
                            candidate = tp;
                            double[] key = new double[2];
                            key[0] = candidate.getLat();
                            key[1] = candidate.getLon();
                            Node outlier = (Node) kdt.nearest(key);
                            mapping.put(outlier, name);
                            classes[i] = 1;
                        }
                        else{
                            candidate = null;
                        }

                    }
                }

            }
        }
        List<Integer> li = Arrays.stream(classes).boxed().collect(Collectors.toList());
        return li;
    }


    public HashMap<Node, String> getMapping() {
        return mapping;
    }

}
