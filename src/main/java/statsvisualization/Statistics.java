package statsvisualization;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Statistics {
    private TreeMap<Double[], Integer> hist;
    public Statistics(){

    }

    public double mean(List<Double> li){
        double m = 0;
        for (Double unit:li){
            m+=unit;
        }
        m = m/li.size();
        return m;
    }

    public double std(List<Double> li, double mean){
        double sigma = 0;
        for (Double unit:li){
            double t = (unit-mean)*(unit-mean);
            sigma+=t;
        }
        sigma = Math.sqrt(sigma/(li.size()-1));
        return sigma;
    }

    public List<Double> gauss(List<Double> x, double mean, double std){
        List<Double> y = new ArrayList<>();
        for (Double unit:x){
            double yi = 1/Math.sqrt(2*Math.PI*std*std)*Math.exp(-(unit-mean)*(unit-mean)/(2*std*std));
            y.add(yi);
        }
        return y;
    }

    public void histogram(List<Double> x, int numbins){
        //System.out.println(x);
        Collections.sort(x);
        //System.out.println(x);
        double minx = x.get(0);
        double maxx = x.get(x.size()-1);
        //System.out.println(maxx+" "+minx);
        double diff = (maxx-minx)/numbins;
        SerComparator comp = new SerComparator(); //whatever

        this.hist = new TreeMap<>(comp);
        double start = minx;

        for (int i = 0; i < numbins; i++) {
            Double[] interval = new Double[2];
            interval[0] = start;
            start += diff;
            interval[1] = start;
            hist.put(interval, 0);
        }

//        for (Double[] bin:hist.keySet()){
//            System.out.println(bin[0]+" "+bin[1]);
//        }
        for (Double xi: x){
            //System.out.println("!!"+xi);
            for (Double[] bin:hist.keySet()){
                if ((xi>=bin[0]) && (xi<=bin[1])){
                    //System.out.println(bin[0]+" "+bin[1]+" "+xi);
                    hist.put(bin, hist.get(bin)+1);
                    break;
                }
            }
        }
    }

    public TreeMap<Double[], Integer> getHist() {
        return hist;
    }

    public void saveHist(String path) throws IOException {
        if (this.hist!=null){
            FileWriter writer = new FileWriter(path, false);
            String st = "";
            for (Double[] key:hist.keySet()){
                st = Double.toString(key[0])+" "+Double.toString(key[1])+" "+Integer.toString(hist.get(key))+"\n";
                writer.write(st);
            }
            writer.close();
        }
    }

    class SerComparator implements Serializable, Comparator<Double[]>{
        public int compare(Double[] o1, Double[] o2) {
            if (o1[0] > o2[0]) {
                return 1;
            }
            if (o1[0] < o2[0]) {
                return -1;
            }
            return 0;
        }
    }
}
