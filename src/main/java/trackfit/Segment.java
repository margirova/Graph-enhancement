package trackfit;

import com.umotional.basestructures.Node;
import java.util.ArrayList;
import java.util.List;
//import java.util.Objects;

public class Segment {
    private Node node;
    private Node parent;
    private int indexclosest;
    private double distclosest;
    private double areasofar;
    private double dArea;
    private List<Double> dists;



    public Segment(Node node){

        this.node = node;
        this.dists = new ArrayList<Double>();
    }

    public Node getNode() {
        return node;
    }

    public List<Double> getDists() {
        return dists;
    }

    public void addDist(double d){
        dists.add(d);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getIndexclosest() {
        return indexclosest;
    }

    public void setIndexclosest(int indexclosest) {
        this.indexclosest = indexclosest;
    }

    public double getDistclosest() {
        return distclosest;
    }

    public void setDistclosest(double distclosest) {
        this.distclosest = distclosest;
    }

    public double getAreasofar() {
        return areasofar;
    }

    public void setAreasofar(double areasofar) {
        this.areasofar = areasofar;
    }

    public void setDists(List<Double> dists) {
        this.dists = dists;
    }

    public double getdArea() {
        return dArea;
    }

    public void setdArea(double dArea) {
        this.dArea = dArea;
    }
    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Segment)) return false;
//        Segment segment = (Segment) o;
//        return Objects.equals(getNode(), segment.getNode());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getNode());
//    }

    @Override
    public String toString() {
        return "Segment{" +
                "node=" + node +
                ", parent=" + parent +
                ", indexclosest=" + indexclosest +
                ", distclosest=" + distclosest +
                ", areasofar=" + areasofar +
                '}';
    }
}
