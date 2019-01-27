package trackfit;

import java.util.List;

public class Pair {
    private int idx;
    private double dist;
    private List<Double> dsts;
    public Pair(int idx, double dist, List<Double> dsts){
        this.idx = idx;
        this.dist = dist;
        this.dsts = dsts;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public List<Double> getDsts() {
        return dsts;
    }

    public void setDsts(List<Double> dsts) {
        this.dsts = dsts;
    }



    //tODO
}
